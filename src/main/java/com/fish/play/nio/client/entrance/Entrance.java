package com.fish.play.nio.client.entrance;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fish.play.nio.client.codec.MessageDecoder;
import com.fish.play.nio.client.codec.MessageEncoder;
import com.fish.play.nio.client.handler.MessageHandler;

public class Entrance {
	private ExecutorService executor = Executors.newScheduledThreadPool(1);

	public void connect(final int port, final String host) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
							ch.pipeline().addLast(new MessageDecoder());
							ch.pipeline().addLast(new MessageEncoder());
							ch.pipeline().addLast(new MessageHandler());
							
						}
					});
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
			
			System.out.println("======" + f.channel().isActive());

		} finally {
			System.out.println("关闭不可用的连接.");
			group.shutdownGracefully();

			System.out.println("开始重连......");
			executor.execute(new Runnable() {

				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(5);

						try {
							connect(port, host);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (InterruptedException e) {
						System.out.println("被中断的操作," + e);
					}
				}
			});

		}
	}

	public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				port = 8080;
			}
		}
		final Entrance client = new Entrance();
		client.connect(port, "127.0.0.1");

	}

}
