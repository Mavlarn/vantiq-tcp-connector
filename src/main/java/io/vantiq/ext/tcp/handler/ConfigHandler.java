package io.vantiq.ext.tcp.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vantiq.ext.tcp.TCPConnector;
import io.vantiq.ext.tcp.TCPConnectorConfig;
import io.vantiq.ext.tcp.tcpsocket.TCPSocketServer;
import io.vantiq.extjsdk.ExtensionServiceMessage;
import io.vantiq.extjsdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ConfigHandler extends Handler<ExtensionServiceMessage> {

    static final Logger LOG = LoggerFactory.getLogger(ConfigHandler.class);

    private static final String CONFIG = "config";

    private TCPConnector connector;
    private ObjectMapper om = new ObjectMapper();

    public ConfigHandler(TCPConnector connector) {
        this.connector = connector;
    }

    /**
     *
     * @param message   A message to be handled
     */
    @Override
    public void handleMessage(ExtensionServiceMessage message) {
        LOG.info("Configuration for source:{}", message.getSourceName());
        Map<String, Object> configObject = (Map) message.getObject();
        Map<String, Object> sourceConfig;

        // Obtain entire config from the message object
        if ( !(configObject.get(CONFIG) instanceof Map)) {
            LOG.error("Configuration failed. No configuration suitable for AMQP Connector.");
            failConfig();
            return;
        }
        sourceConfig = (Map) configObject.get(CONFIG);
        TCPConnectorConfig config = TCPConnectorConfig.fromMap(sourceConfig);

        if (config.isServerMode()) {
            // start TCP server to accept TCP client
            new Thread(() -> new TCPSocketServer(connector, config.getTcpServerPort(), config.getHeartBeat())).start();
        }

    }

    /**
     * Closes the source {@link AMQPConnector} and marks the configuration as completed. The source will
     * be reactivated when the source reconnects, due either to a Reconnect message (likely created by an update to the
     * configuration document) or to the WebSocket connection crashing momentarily.
     */
    private void failConfig() {
        connector.close();
    }

}
