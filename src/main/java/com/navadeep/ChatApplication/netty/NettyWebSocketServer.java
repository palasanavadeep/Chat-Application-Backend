package com.navadeep.ChatApplication.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class NettyWebSocketServer implements ApplicationContextAware {

    private int port;
    private ApplicationContext applicationContext;

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
                                pipeline.addLast(new HttpObjectAggregator(65536));
                                pipeline.addLast(new ChatWebSocketHandler(applicationContext));
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.TCP_NODELAY, true);

                ChannelFuture future = bootstrap.bind(port).sync();
                serverChannel = future.channel();
                System.out.println("Netty WebSocket server started on port " + port);
                serverChannel.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                shutdown();
            }
        }, "NettyServerThread").start();
    }

    public void stop() {
        System.out.println("Shutting down Netty WebSocket server...");
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
