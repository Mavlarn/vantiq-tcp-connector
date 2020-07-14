package io.vantiq.ext.tcp;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class TCPConnectorConfig {

    private static final String TCP_SERVER_PORT = "tcp_server_port";
    private static final String TCP_CLIENT_HOST = "tcp_client_host";
    private static final String TCP_CLIENT_PORT = "tcp_client_port";
    private static final String TCP_HEART_BEAT = "tcp_heart_beat";

    private int tcpServerPort;
    private String tcpClientHost;
    private int tcpClientPort;
    private int heartBeat;

    private boolean isServerMode;
    private boolean isClientMode;

    public static TCPConnectorConfig fromMap(Map<String, Object> sourceConfig) {
        TCPConnectorConfig connectorConfig = new TCPConnectorConfig();
        String tcpServerPortStr = sourceConfig.getOrDefault(TCP_SERVER_PORT, "").toString();
        String tcpClientPortStr = sourceConfig.getOrDefault(TCP_CLIENT_PORT, "").toString();
        String tcpClientHost = sourceConfig.getOrDefault(TCP_CLIENT_HOST, "").toString();
        int heartBeat = (Integer)sourceConfig.getOrDefault(TCP_HEART_BEAT, 15);
        if (StringUtils.isNotBlank(tcpServerPortStr)) {
            connectorConfig.isServerMode = true;
            connectorConfig.setTcpServerPort(Integer.parseInt(tcpServerPortStr));
        }
        if (StringUtils.isNotBlank(tcpClientPortStr) && StringUtils.isNotBlank(tcpClientHost)) {
            connectorConfig.isClientMode = true;
            connectorConfig.setTcpClientPort(Integer.parseInt(tcpClientPortStr));
            connectorConfig.setTcpClientHost(tcpClientHost);
        }
        if (connectorConfig.isServerMode && connectorConfig.isClientMode) {
            throw new IllegalArgumentException("Can not set tcp server and client at the same time");
        }
        if (!connectorConfig.isServerMode && !connectorConfig.isClientMode) {
            throw new IllegalArgumentException("Should set tcp server or client config");
        }
        connectorConfig.setHeartBeat(heartBeat);

        return connectorConfig;
    }

    public boolean isServerMode() {
        return isServerMode;
    }

    public void setServerMode(boolean serverMode) {
        isServerMode = serverMode;
    }

    public boolean isClientMode() {
        return isClientMode;
    }

    public void setClientMode(boolean clientMode) {
        isClientMode = clientMode;
    }

    public int getTcpServerPort() {
        return tcpServerPort;
    }

    public void setTcpServerPort(int tcpServerPort) {
        this.tcpServerPort = tcpServerPort;
    }

    public String getTcpClientHost() {
        return tcpClientHost;
    }

    public void setTcpClientHost(String tcpClientHost) {
        this.tcpClientHost = tcpClientHost;
    }

    public int getTcpClientPort() {
        return tcpClientPort;
    }

    public void setTcpClientPort(int tcpClientPort) {
        this.tcpClientPort = tcpClientPort;
    }

    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }
}
