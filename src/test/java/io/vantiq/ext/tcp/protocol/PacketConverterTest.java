package io.vantiq.ext.tcp.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.vantiq.ext.tcp.tcpsocket.HeartBeatTimerHandler;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PacketConverterTest {

    private static final Logger LOG = LoggerFactory.getLogger(PacketConverterTest.class);

    PacketConverter converter = PacketConverter.INSTANCE;

    @Test
    public void testDecode() {
        String[] aa = new String[] {"1", "s"};
        int cc = 0xB6;
        byte bb = (byte)cc;
        byte[] data = new byte[]{0x78, (byte)0xB6, 0x4C, 0x5A, 0x01, 0x03, 0x00, 0x0A, 0x47, 0x54, 0x4C, 0x41, 0x4E, 0x44, 0x38, 0x39, 0x33, 0x37, 0x11, 0x21, (byte)0xD3};
        String content = "78B64C5A0103000A47544C414E44383933371121D3";
        ByteBuf bf = ByteBufAllocator.DEFAULT.ioBuffer();
        bf.writeBytes(data);
        int packetLength = bf.readableBytes();
        Map<String, Object> packet = converter.decode(bf);
        LOG.info("Decoded data:{}", packet);

        ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();
        converter.encode(packet, out);
        Assert.assertTrue(out.readableBytes() == packetLength);

        Map decodedPacket = converter.decode(out);

        Assert.assertTrue(decodedPacket.equals(packet));


    }

    @Test
    public void testHeartBeat() {
        HeartBeatTimerHandler handler = new HeartBeatTimerHandler(10);
        Map data = handler.heartBeatRequestPacket();
        LOG.info("Heartbeat data:{}", data);

        ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();
        converter.encode(data, out);

        Map decodedPacket = converter.decode(out);

        Assert.assertTrue(decodedPacket.equals(data));
    }
}
