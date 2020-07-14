package io.vantiq.ext.tcp.handler;

import io.vantiq.ext.tcp.TCPConnector;
import io.vantiq.extjsdk.ExtensionServiceMessage;
import io.vantiq.extjsdk.ExtensionWebSocketClient;
import io.vantiq.extjsdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class PublishHandler extends Handler<ExtensionServiceMessage> {

    static final Logger LOG = LoggerFactory.getLogger(PublishHandler.class);

    private TCPConnector connector;

    public PublishHandler(TCPConnector connector) {
        this.connector = connector;
    }

    @Override
    public void handleMessage(ExtensionServiceMessage message) {
        LOG.debug("Publish with message " + message.toString());

        String replyAddress = ExtensionServiceMessage.extractReplyAddress(message);
        ExtensionWebSocketClient client = connector.getVantiqClient();

        if ( !(message.getObject() instanceof Map) ) {
            client.sendQueryError(replyAddress, "io.vantiq.videoCapture.handler.PublishHandler",
                    "Request must be a map", null);
        }

        Map<String, ?> request = (Map<String, ?>) message.getObject();

        publish(request);

    }

    private void publish(Map<String, ?> request) {

        // Gather query results, or send a query error if an exception is caught
        try {
            this.connector.getClientChannel().writeAndFlush(request);

            LOG.trace("Sent message: ", request);
        } catch (Exception e) {
            LOG.error("An unexpected error occurred when executing publish.", e);
            LOG.error("Request was: {}", request);
        }
    }

}
