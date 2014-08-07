package com.fish.play.nio.client.transport.exchange;

import io.netty.channel.Channel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import com.fish.play.nio.client.bean.RequestEntity;
import com.fish.play.nio.client.transport.NettyClient;

public class ExchangeClient extends NettyClient {
	private static final AtomicLong threadNumber = new AtomicLong(0L);
	public static final long DEFAULT_OP_TIMEOUT = 5000L;

	public ExchangeClient(String host, int port) {
		super(host, port);
	}

	public Object request(String message) throws InterruptedException, TimeoutException {
		long serialNum = threadNumber.getAndIncrement();
		Command command = new Command(new CountDownLatch(1), serialNum);
		sendMessage(message, serialNum, command);
		latchWait(serialNum, command);
		Command.commandMap.remove(serialNum);
		return command.getResult();
	}

	private void sendMessage(String message, long serialNum, Command command) {
		Channel channel = getChannel();
		if (channel != null && channel.isActive()) {
			RequestEntity entity = new RequestEntity();
			entity.setSerialNum(serialNum);
			entity.setRequest(message);
			channel.writeAndFlush(entity);
		}

	}

	private void latchWait(long serialNum, Command command)
			throws InterruptedException, TimeoutException {
		if (command.getLatch().await(DEFAULT_OP_TIMEOUT, TimeUnit.MILLISECONDS)) {
			
		} else {
			Command.commandMap.remove(serialNum);
			throw new TimeoutException("exceeded timeout threshold");
		}

	}

}
