package io.vantiq.ext.tcp.tcpsocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.vantiq.ext.tcp.protocol.ProtocolValueConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HeartBeatTimerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(HeartBeatTimerHandler.class);


    private int interval;
    private int hbSeq = 1;

    public HeartBeatTimerHandler(int interval) {
        this.interval = interval;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("HeartBeat Timer activated");
        scheduleSendHeartBeat(ctx);

        super.channelActive(ctx);
    }

    private void scheduleSendHeartBeat(ChannelHandlerContext ctx) {
        ctx.executor().schedule(() -> {

            if (ctx.channel().isActive()) {
                ctx.channel().writeAndFlush(heartBeatRequestPacket());
                scheduleSendHeartBeat(ctx);
            }

        }, interval, TimeUnit.SECONDS);
    }

    public Map heartBeatRequestPacket() {
        // Use hexString in Java object
        Map heartBeatData = new HashMap();
            /** 3. packet format
                byte[] head;
                byte[] command;
                byte direction; // 1: server to client, or 2: client to server
                byte location;
                byte[] dataLength;
                byte[] data;
                byte checkSum;
                byte[] end;
            */
        heartBeatData.put("head", ProtocolValueConstants.HEAD);
        heartBeatData.put("command", ProtocolValueConstants.HEART_BEAT_CMD);
        heartBeatData.put("direction", ProtocolValueConstants.DIRECTION_SERV_TO_CLIENT);
        heartBeatData.put("location", ProtocolValueConstants.LOCATION);
        heartBeatData.put("dataLength", 10);
        Map data = new HashMap();
        /**
         *       {"field": "controllerNo", "length": 6, "type": "string"},
         *       {"field": "heartBeatSeq", "length": 4, "type": "string"}
         */
        data.put("controllerNo", "GTLAND");
        data.put("heartBeatSeq", String.valueOf(hbSeq));
        heartBeatData.put("data", data);
        heartBeatData.put("checkSum", "17");
        heartBeatData.put("end", ProtocolValueConstants.END);
        hbSeq++;

        return heartBeatData;
    }
}
