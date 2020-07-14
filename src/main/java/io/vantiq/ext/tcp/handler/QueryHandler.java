package io.vantiq.ext.tcp.handler;

import io.vantiq.ext.tcp.TCPConnector;
import io.vantiq.extjsdk.ExtensionServiceMessage;
import io.vantiq.extjsdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryHandler extends Handler<ExtensionServiceMessage> {

    static final Logger LOG = LoggerFactory.getLogger(QueryHandler.class);

    private TCPConnector extension;

    public QueryHandler(TCPConnector extension) {
        this.extension = extension;
    }

    @Override
    public void handleMessage(ExtensionServiceMessage msg) {

        LOG.warn("TCP connector does not support query");
    }
}
