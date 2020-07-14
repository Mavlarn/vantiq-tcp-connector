package io.vantiq.ext.tcp.tcpsocket;

import org.junit.Test;

public class TCPSocketClientTest {

    @Test
    public void testClient() throws InterruptedException {
        TCPSocketClient client = new TCPSocketClient();
        client.connect("localhost", 6001);

        Thread.sleep(300000);


    }
}
