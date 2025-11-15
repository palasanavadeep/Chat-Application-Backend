package com.navadeep.ChatApplication.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class NettyWebSocketServer implements ApplicationContextAware {

    private final int port;
    private ApplicationContext applicationContext;
    Log log =  LogFactory.getLog(NettyWebSocketServer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    public NettyWebSocketServer(int port) {
        this.port = port;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void start() {
        new Thread(() -> {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new HttpServerCodec());
                                pipeline.addLast(new HttpObjectAggregator(5242880));
                                pipeline.addLast(new WebSocket08FrameDecoder(
                                        true, // allow extensions
                                        false, // not masking client frames
                                        20 * 1024 * 1024 // max frame length 20 MB
                                ));
                               pipeline.addLast(new ChatWebSocketHandler(applicationContext));
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.TCP_NODELAY, true);

                ChannelFuture future = bootstrap.bind(port).sync();
                serverChannel = future.channel();
                log.info("Netty WebSocket server started on port :: "+ port);
                serverChannel.closeFuture().sync();
            } catch (Exception e) {
                log.error("Unable to start Netty Server :: "+e.getMessage(),e);
            } finally {
                shutdown();
            }
        }, "NettyServerThread").start();
    }

    public void stop() {
        log.info("Shutting down Netty WebSocket server...");
        shutdown();
    }

    private void shutdown() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
