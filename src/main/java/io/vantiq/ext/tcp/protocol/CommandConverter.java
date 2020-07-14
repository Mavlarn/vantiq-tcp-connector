package io.vantiq.ext.tcp.protocol;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CommandConverter {

    private static final Logger LOG = LoggerFactory.getLogger(CommandConverter.class);

    private String commandConf = "tcp-command.json";
    Map<String, CommandDef> commandMap;

    public CommandConverter() {

        String configFileName = System.getProperty("user.dir") + File.separator + commandConf;
        File configFile = new File(configFileName);
        if (!configFile.exists()) {
            configFile = new File("/etc/config/" + commandConf);
        }
        InputStream cfr = null;
        try {
            if (configFile.exists()) {
                cfr = new FileInputStream(configFileName);
                ObjectMapper mapper = new ObjectMapper();
                JavaType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, CommandDef.class);
                commandMap = mapper.readValue(configFile, type);
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
        LOG.debug("Command Config finished:{}", commandMap);
    }

    public ByteBuf encode(String command, Map commandData, String direction) {
        CommandDef commandObj = commandMap.get(command);
        int commandLength = 0;
        if (isRequest(direction)) {
            commandLength = commandObj.request.length;
            ByteBuf dataBuffer = ByteBufAllocator.DEFAULT.buffer(commandLength, commandLength);
            encodeFields(commandObj.request.fields, commandData, dataBuffer);
            return dataBuffer;
        } else if (isResponse(direction)) {
            commandLength = commandObj.response.length;
            ByteBuf dataBuffer = ByteBufAllocator.DEFAULT.buffer(commandLength, commandLength);
            encodeFields(commandObj.response.fields, commandData, dataBuffer);
            return dataBuffer;
        } else {
            LOG.error("Invalid direction in command data:{}", commandData);
            throw new IllegalArgumentException("Invalid direction in command data " + commandData.toString());
        }
    }

    private boolean isRequest(String direction) {
        return direction.equalsIgnoreCase(ProtocolValueConstants.DIRECTION_SERV_TO_CLIENT);
    }

    private boolean isResponse(String direction) {
        return direction.equalsIgnoreCase(ProtocolValueConstants.DIRECTION_CLIENT_TO_SERV);
    }

    private void encodeFields(CommandDef.CommandField[] fields, Map data, ByteBuf dataBuffer) {
        for (CommandDef.CommandField field: fields) {
            String fieldValue = data.get(field.field).toString();
            if (field.type.equalsIgnoreCase("string")) {
                byte[] value = ByteHexUtil.encodeString(fieldValue, field.length);
                dataBuffer.writeBytes(value, 0, field.length);
            } else if (field.type.equalsIgnoreCase("integer")) {
                int intValue = Integer.parseInt(fieldValue);
                byte[] value = ByteHexUtil.encodeInteger(intValue, field.length);
                dataBuffer.writeBytes(value, 0, field.length);
            } else if (field.type.equalsIgnoreCase("byte")) {
                dataBuffer.writeByte(fieldValue.getBytes()[0]);
            }
        }
    }

    public Map decode(String command, ByteBuf data, String direction) {
        CommandDef commandObj = commandMap.get(command.toUpperCase());
        Map result = new HashMap();

        if (isRequest(direction)) {
            decode(commandObj.request.fields, data, result);
        } else if (isResponse(direction)) {
            decode(commandObj.response.fields, data, result);
        } else {
            throw new IllegalArgumentException("Invalid direction:" + direction + " in command data " + data);
        }
        return result;
    }

    private void decode(CommandDef.CommandField[] fields, ByteBuf data, Map result) {
        for (CommandDef.CommandField field: fields) {
            byte[] value = new byte[field.length];
            data.readBytes(value);

            if (field.type.equalsIgnoreCase("string") || field.type.equalsIgnoreCase("byte")) {
                result.put(field.field, ByteHexUtil.decodeString(value));
            } else if (field.type.equalsIgnoreCase("integer")) {
                result.put(field.field, ByteHexUtil.decodeInteger(value));
            }
        }

    }
}
