package com.fish.play.nio.client.exchange;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fish.play.nio.client.bean.RequestEntity;
import com.fish.play.nio.client.transport.NettyClient;

public class ExchangeClient extends NettyClient {
	private static final Log log = LogFactory.getLog(ExchangeClient.class);
	private static final AtomicLong threadNumber = new AtomicLong(0l);
	private static final long DEFAULT_OP_TIMEOUT = 2500L;

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

	private void sendMessage(String message, final long serialNum, Command command) {
		Channel channel = getChannel();
		if (channel != null && channel.isActive()) {
			RequestEntity entity = new RequestEntity();
			entity.setSerialNum(serialNum);
			entity.setRequest(message);
			ChannelFuture future = channel.writeAndFlush(entity);
			future.addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
						if (future.isCancelled()) {
							log.error("execution is cancelled, serialNum:" + serialNum);
						} else {
							Throwable t = future.cause();
							if (t != null) {
								log.error("channel write exception with serialNum:" + serialNum, t);
							}
						}
					}
				}
			});
		}

	}

	private void latchWait(long serialNum, Command command) throws InterruptedException, TimeoutException {
		if (command.getLatch().await(DEFAULT_OP_TIMEOUT, TimeUnit.MILLISECONDS)) {

		} else {
			Command.commandMap.remove(serialNum);
			throw new TimeoutException("exceeded timeout threshold");
		}

	}

}
