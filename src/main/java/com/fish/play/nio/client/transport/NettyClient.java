package com.fish.play.nio.client.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fish.play.nio.client.codec.MessageDecoder;
import com.fish.play.nio.client.codec.MessageEncoder;
import com.fish.play.nio.client.handler.MessageHandler;

public class NettyClient extends AbstractClient {
	private static final Log log = LogFactory.getLog(NettyClient.class);
	private Bootstrap bootstrap;
	private EventLoopGroup group;
	private volatile Channel channel;

	public NettyClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void doOpen() throws Throwable {
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
						ch.pipeline().addLast(new MessageDecoder());
						ch.pipeline().addLast(new MessageEncoder());
						ch.pipeline().addLast(new MessageHandler());

					}
				});
	}

	@Override
	protected void doClose() throws Throwable {
		
	}

	@Override
	protected void doConnect() throws Throwable {
		ChannelFuture channelFuture = bootstrap.connect(getHost(), getPort()).sync();
		if (channelFuture.isSuccess()) {
			Channel newChannel = channelFuture.channel();
			Channel oldChannel = this.channel;
			NettyClient.this.channel = newChannel;
			if (oldChannel != null) {
				try {
					oldChannel.close();
				} catch (Exception e) {
					log.error("old channel close error.", e);
				} finally {
					NettyChannel.removeChannelIfDisconnected(oldChannel);
				}
			}
		} else {
			if (channelFuture.cause() != null) {
				log.error("do connect error message is: ", channelFuture.cause());
			}
			channelFuture.cancel(true);
		}

	}

	@Override
	protected void doDisConnect() throws Throwable {
		NettyChannel.removeChannelIfDisconnected(channel);
	}

	@Override
	protected Channel getChannel() {
		Channel ch = channel;
		if (ch == null || !ch.isActive())
			return null;
		return NettyChannel.getOrAddChannel(ch);
	}
	
	public static void main(String[] args) {
		new NettyClient("127.0.0.1", 8080);
	}

}
