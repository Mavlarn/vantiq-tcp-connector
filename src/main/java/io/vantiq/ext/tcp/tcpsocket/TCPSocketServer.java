package io.vantiq.ext.tcp.tcpsocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.vantiq.ext.tcp.TCPConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPSocketServer {

    private static final Logger LOG = LoggerFactory.getLogger(TCPSocketServer.class);

    private TCPConnector connector;

    public TCPSocketServer(TCPConnector connector, int port, int heartbeatInterval) {
        this.connector = connector;
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        protected void initChannel(NioSocketChannel ch) {

                            connector.setClientChannel(ch);
                            ch.pipeline().addLast(new HeartBeatTimerHandler(heartbeatInterval));
                            ch.pipeline().addLast(new PacketDecoder());
                            ch.pipeline().addLast(new ProcessingHandler(connector));
                            ch.pipeline().addLast(new PacketEncoder());

                        }
                    });

            ChannelFuture f = serverBootstrap.bind(port);
            connector.setServerChannel(f.channel());
            LOG.debug("TCP Server started on port:{}", port);

            f.sync(); // wait for stop
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOG.error("TCP Server error", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
