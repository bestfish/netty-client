package com.fish.play.nio.client.transport.exchange;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.Channel;

import com.fish.play.nio.client.transport.NettyClient;

public class ExchangeClient extends NettyClient {
	private static final ConcurrentMap<Long, Command> commandMap = new ConcurrentHashMap<Long, Command>();

	public ExchangeClient(String host, int port) {
		super(host, port);
	}
	
	public void request(String message) {
		Channel channel = getChannel();
		if (channel != null && channel.isActive()) {
			
		}
		
	}
	

}
