package io.vantiq.ext.tcp;

import io.netty.channel.Channel;
import io.vantiq.ext.tcp.handler.*;
import io.vantiq.extjsdk.VantiqConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TCPConnector extends VantiqConnector {

    private static final Logger LOG = LoggerFactory.getLogger(TCPConnector.class);

    private Channel clientChannel; // used to send data to client from VANTIQ
    private Channel serverChannel; // used to stop/restart the server

    @Override
    public void init() {
        vantiqClient.setConfigHandler(new ConfigHandler(this));
        vantiqClient.setReconnectHandler(new ReconnectHandler(this));
        vantiqClient.setCloseHandler(new CloseHandler(this));
        vantiqClient.setPublishHandler(new PublishHandler(this));
        vantiqClient.setQueryHandler(new QueryHandler(this));
    }

    @Override
    public void close() {
        if (this.serverChannel != null && this.serverChannel.isOpen()) {
            LOG.info("Closing tcp server channel...");
            serverChannel.close();
        }
    }

    public Channel getClientChannel() {
        return clientChannel;
    }

    public void setClientChannel(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }
}
