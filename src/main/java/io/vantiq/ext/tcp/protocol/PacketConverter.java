package io.vantiq.ext.tcp.protocol;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketConverter {

    private static final Logger LOG = LoggerFactory.getLogger(PacketConverter.class);

    private CommandConverter commandConverter = new CommandConverter();
    private String packetConf = "tcp-packet.json";
    List<PacketField> fields;

    public static PacketConverter INSTANCE = new PacketConverter();

    private PacketConverter() {

        String configFileName = System.getProperty("user.dir") + File.separator + packetConf;
        File configFile = new File(configFileName);
        if (!configFile.exists()) {
            configFile = new File("/etc/config/" + packetConf);
        }
        InputStream cfr = null;
        try {
            if (configFile.exists()) {
                cfr = new FileInputStream(configFileName);
                ObjectMapper mapper = new ObjectMapper();

                JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, PacketField.class);
                fields = mapper.readValue(configFile, type);

            } else {
                LOG.error("Location specified for configuration directory ({}) does not exist.", configFile.getAbsolutePath());
                throw new RuntimeException("No command configuration file");
            }

        } catch (IOException e) {
            LOG.error("Config file ({}) was not readable: {}", configFileName, e.getMessage());
            throw new RuntimeException("Invalid configuration file");
        } finally {
            if (cfr != null) {
                try {
                    cfr.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        LOG.debug("Package Config finished:{}", fields);
    }

    public void encode(Map packet, ByteBuf out) {
        try {
            for (PacketField field: fields) {
                Object value = packet.get(field.field);
                if (field.type.equalsIgnoreCase("integer")) {
                    Integer valueInt = Integer.parseInt(value.toString());
                    if (field.getLength() == 2) {
                        out.writeShort(valueInt);
                    } else if (field.getLength() == 4) {
                        out.writeInt(valueInt);
                    }
                } else if (field.type.equalsIgnoreCase("byte")) {

                    if (value instanceof String) {
                        byte[] byteValue = ByteBufUtil.decodeHexDump(value.toString());
                        out.writeBytes(byteValue);
                    } else if (value instanceof byte[] || value instanceof Byte[]) {
                        byte[] byteValue = (byte[])value;
                        out.writeBytes(byteValue);
                    } else if (value instanceof Byte) {
                        out.writeByte((byte)value);
                    }
                } else if (field.type.equalsIgnoreCase("data")) {
                    Map data = (Map)value;
                    String cmd = packet.get("command").toString().toUpperCase();
                    ByteBuf datas = commandConverter.encode(cmd, data, packet.get(ProtocolPacketConstants.DIRECTION).toString());
                    out.writeBytes(datas);
                }
            }
            if (LOG.isDebugEnabled()) {
                byte[] encodedBytes = new byte[out.readableBytes()];
                out.getBytes(0, encodedBytes);
                LOG.debug("Encode:{}\nto:{}", packet, ByteBufUtil.hexDump(encodedBytes));
            }
        } catch (Exception e) {
            LOG.error("Encode error:" + e.getMessage(), e);
        }
    }

    public Map<String, Object> decode(ByteBuf data) {
        byte[] encodedBytes = null;
        if (LOG.isDebugEnabled()) {
            encodedBytes = new byte[data.readableBytes()];
            data.getBytes(0, encodedBytes);
        }

        Map result = new HashMap();
        String command = null;
        int dataLength = 0;
        String direction = null;

        for (PacketField field: fields) {
            byte[] value = new byte[field.length];
            data.readBytes(value);

            if (field.type.equalsIgnoreCase("byte")) {
                String hexStr = ByteBufUtil.hexDump(value).toUpperCase();
                result.put(field.field, hexStr);
                if (field.field.equalsIgnoreCase("command")) {
                    command = hexStr;
                } else if (field.field.equalsIgnoreCase("direction")) {
                    direction = hexStr;
                }
            } else if (field.type.equalsIgnoreCase("integer")) {
                int length = new BigInteger(value).intValue();
                result.put(field.field, length);
                if (field.field.equalsIgnoreCase("dataLength")) {
                    dataLength = length;
                }
            } else if (field.type.equalsIgnoreCase("data")) {
                Map dataMap = commandConverter.decode(command, data.readBytes(dataLength), direction);
                result.put("data", dataMap);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Decode:{}\nto:{}", ByteBufUtil.hexDump(encodedBytes), result);
        }
        return result;
    }

}
