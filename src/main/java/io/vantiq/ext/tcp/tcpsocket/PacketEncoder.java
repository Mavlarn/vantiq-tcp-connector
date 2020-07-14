package io.vantiq.ext.tcp.tcpsocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.vantiq.ext.tcp.protocol.PacketConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PacketEncoder extends MessageToByteEncoder<Map> {

    private static final Logger LOG = LoggerFactory.getLogger(PacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Map packetData, ByteBuf out) {
        PacketConverter.INSTANCE.encode(packetData, out);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Packet encoder error:" + cause.getLocalizedMessage(), cause);
        ctx.close();
    }
}
