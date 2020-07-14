package io.vantiq.ext.tcp.tcpsocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.vantiq.ext.tcp.protocol.ProtocolPacketConstants;
import io.vantiq.ext.tcp.protocol.ProtocolValueConstants;

import java.util.Map;


public class HeartBeatRespondHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Map msgData = (Map)msg;

        if (msgData.get("command").toString().equalsIgnoreCase(ProtocolValueConstants.HEART_BEAT_CMD)) {
            msgData.put("direction", ProtocolValueConstants.DIRECTION_CLIENT_TO_SERV);
            msgData.put(ProtocolPacketConstants.DATA_LENGTH, 11);
            Map hbData = (Map)msgData.get("data");
            hbData.put("answer", ProtocolValueConstants.HEART_BEAT_ANSWER);
            ctx.channel().writeAndFlush(msgData);
        }
        super.channelRead(ctx, msg);
    }

}
