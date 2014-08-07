package com.fish.play.nio.client.transport.exchange;

import java.util.concurrent.CountDownLatch;

public class Command {
	private CountDownLatch latch;
	private volatile Object result;

	public Command(CountDownLatch latch) {
		this.latch = latch;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
