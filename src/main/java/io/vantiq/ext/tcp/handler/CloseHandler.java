package io.vantiq.ext.tcp.handler;

import io.vantiq.ext.tcp.TCPConnector;
import io.vantiq.extjsdk.ExtensionWebSocketClient;
import io.vantiq.extjsdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.vantiq.extjsdk.ConnectorConstants.CONNECTOR_CONNECT_TIMEOUT;
import static io.vantiq.extjsdk.ConnectorConstants.RECONNECT_INTERVAL;


public class CloseHandler extends Handler<ExtensionWebSocketClient> {

    static final Logger LOG = LoggerFactory.getLogger(CloseHandler.class);

    private TCPConnector connector;

    public CloseHandler(TCPConnector connector) {
        this.connector = connector;
    }

    @Override
    public void handleMessage(ExtensionWebSocketClient client) {

        LOG.info("Close handler: {}", client);
        connector.close();

        // reconnect
        boolean sourcesSucceeded = false;
        while (!sourcesSucceeded) {
            client.initiateFullConnection(connector.getConnectionInfo().getVantiqUrl(), connector.getConnectionInfo().getToken());
            sourcesSucceeded = connector.getVantiqClient().checkConnectionFails(CONNECTOR_CONNECT_TIMEOUT);
            if (!sourcesSucceeded) {
                try {
                    Thread.sleep(RECONNECT_INTERVAL);
                } catch (InterruptedException e) {
                    LOG.error("An error occurred when trying to sleep the current thread. Error Message: ", e);
                }
            }
        }


    }
}
