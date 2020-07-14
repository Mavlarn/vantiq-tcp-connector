package io.vantiq.ext.tcp.tcpsocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.vantiq.ext.tcp.TCPConnector;
import io.vantiq.ext.tcp.protocol.ProtocolValueConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProcessingHandler extends ChannelInboundHandlerAdapter {

    private TCPConnector connector;

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingHandler.class);

    public ProcessingHandler(TCPConnector connector) {
        this.connector = connector;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Map msgData = (Map)msg;
        String cmd = msgData.get("command").toString();
        String direction = msgData.get("direction").toString();
        if (direction.equalsIgnoreCase(ProtocolValueConstants.DIRECTION_CLIENT_TO_SERV)) {
            LOG.info("Got client side msg to send t o VANTIQ:{}", msgData);
            if (cmd.equalsIgnoreCase(ProtocolValueConstants.HEART_BEAT_CMD)) {
                LOG.info("Got heart beat response.");
            }
            connector.getVantiqClient().sendNotification(msgData);
        }
        super.channelRead(ctx, msg);
    }
}
