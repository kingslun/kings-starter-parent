package com.kings.base.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class NettyClient {

    private final String host;
    private final int port;
    private boolean started = false;

    NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @PreDestroy
    public void close() {
        this.started = false;
    }

    @PostConstruct
    public void init() {
        this.started = true;
    }

    @Slf4j
    static class MessageHandler extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String s) {
            log.debug("服务端说：" + s);
        }
    }

    void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new MessageHandler());
            Channel channel = bootstrap.connect(host, port).sync().channel();
            while (started) {
                channel.writeAndFlush(in.readLine() + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyClient("127.0.0.1", 65535).start();
    }
}
