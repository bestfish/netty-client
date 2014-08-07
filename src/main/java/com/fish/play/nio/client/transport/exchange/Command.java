package com.fish.play.nio.client.transport.exchange;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

public class Command {
	public static final ConcurrentMap<Long, Command> commandMap = new ConcurrentHashMap<Long, Command>();
	private CountDownLatch latch;
	private volatile String result;

	public Command(CountDownLatch latch, long serialNum) {
		this.latch = latch;
		commandMap.put(serialNum, this);
	}

	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public CountDownLatch getLatch() {
		return latch;
	}

	

}
