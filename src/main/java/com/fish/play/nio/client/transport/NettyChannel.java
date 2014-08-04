package com.fish.play.nio.client.transport;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NettyChannel {
	private static final ConcurrentMap<Channel, NettyChannel> channelMap = new ConcurrentHashMap<Channel, NettyChannel>();
	private final Channel channel;

	public NettyChannel(Channel channel) {
		this.channel = channel;
	}

	public static Channel getOrAddChannel(Channel ch) {
		if (ch == null) {
			return null;
		}
		NettyChannel ret = channelMap.get(ch);
		if (ret == null) {
			NettyChannel nc = new NettyChannel(ch);
			if (ch.isActive()) {
				ret = channelMap.putIfAbsent(ch, nc);
			}
			if (ret == null) {
				ret = nc;
			}
		}
		return ret.channel;
	}

	public static void removeChannelIfDisconnected(Channel ch) {
		if (ch != null && !ch.isActive()) {
			channelMap.remove(ch);
		}
	}

}
