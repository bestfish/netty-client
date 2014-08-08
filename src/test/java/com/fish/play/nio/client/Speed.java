package com.fish.play.nio.client;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fish.play.nio.client.exchange.ExchangeClient;

public class Speed {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		final ExchangeClient client = new ExchangeClient("127.0.0.1", 8080);
		final Random random = new Random();
		ExecutorService exec = Executors.newFixedThreadPool(8);
		for (int i = 0; i < 100; i ++) {
			exec.submit(new Callable<String>() {
				
				@Override
				public String call() throws Exception {
					int index = random.nextInt(3);
					String result = client.request(String.valueOf(index));
					System.out.println(index + "-" + result);
					return result;
				}
			});
		}
		

	}

}
