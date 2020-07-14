package io.vantiq.ext.tcp.tcpsocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPSocketClient {

    private static final Logger LOG = LoggerFactory.getLogger(TCPSocketClient.class);

    private Channel serverChannel;

    public void connect(String host, int port) {

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                // 1.指定线程模型
                .group(workerGroup)
                // 2.指定 IO 类型为 NIO
                .channel(NioSocketChannel.class)
                // 3.IO 处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        serverChannel = ch;
//                        ch.pipeline().addLast(new LineBasedFrameDecoder(1024 * 10));
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new HeartBeatRespondHandler());
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });
        // 4.建立连接
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                LOG.debug("Client connection succeed");
            } else {
                LOG.debug("Client connection failed");
            }

        });
    }
}
