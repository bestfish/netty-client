package com.fish.play.nio.client.transport;

import io.netty.channel.Channel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractClient {
	private static final Log log = LogFactory.getLog(AbstractClient.class);
	private final Lock connectLock = new ReentrantLock();
	private final ScheduledThreadPoolExecutor reconnectExecutorService;
	private volatile ScheduledFuture<?> reconnectExecutorFuture;
	private final AtomicInteger reconnect_count = new AtomicInteger(0);
	private String host;
	private int port = 80;
	private int reconnect = 2000;

	public AbstractClient(String host, int port) {
		this.host = host;
		this.port = port;

		reconnectExecutorService = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);
		try {
			doOpen();
			connect();
		} catch (Throwable e) {
			close();
			log.error("initially connect to server " + host + ":" + port + " failed, please check whether the server is startup.", e);
		}
	}

	protected void connect() throws Throwable {
		connectLock.lock();
		try {
			if (isActive()) {
				return;
			}
			enableReconnectOnLossMechanism();
			doConnect();
			if (!isActive()) {
				throw new Exception("Failed connect to server " + host + ":" + port);
			} else {
				if (log.isInfoEnabled()) {
					log.info("Successed connect to server " + host + ":" + port + ", channel is " + getChannel());
				}
			}
			reconnect_count.set(0);
		} catch (Throwable t) {
			throw t;
		} finally {
			connectLock.unlock();
		}
	}

	private synchronized void enableReconnectOnLossMechanism() {
		if (reconnect > 0 && (reconnectExecutorFuture == null || reconnectExecutorFuture.isCancelled())) {
			Runnable reconnectTask = new Runnable() {
				@Override
				public void run() {
					if (!isActive()) {
						int times = 0;
						try {
							times = reconnect_count.incrementAndGet();
							connect();
						} catch (Throwable t) {
							log.error("client reconnect to server " + host + ":" + port + " failed for " + times
									+ " times", t);
						}
					}
				}
			};
			reconnectExecutorFuture = reconnectExecutorService.scheduleWithFixedDelay(reconnectTask, reconnect, reconnect, TimeUnit.MILLISECONDS);

		}

	}

	public boolean isActive() {
		Channel channel = getChannel();
		if (channel == null)
			return false;
		return channel.isActive();
	}

	public void disconnect() {
		connectLock.lock();
		try {
			destroyConnectStatusCheckCommand();
			try {
				Channel channel = getChannel();
				if (channel != null) {
					channel.close();
				}
			} catch (Throwable e) {
				log.warn(e.getMessage(), e);
			}
			try {
				doDisConnect();
			} catch (Throwable e) {
				log.warn(e.getMessage(), e);
			}
		} finally {
			connectLock.unlock();
		}
	}

	private synchronized void destroyConnectStatusCheckCommand() {
		try {
			if (reconnectExecutorFuture != null && !reconnectExecutorFuture.isDone()) {
				reconnectExecutorFuture.cancel(true);
				reconnectExecutorService.purge();
			}
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}
	}

	public void close() {
		try {
			disconnect();
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}
		try {
			doClose();
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	/**
	 * Open client.
	 * 
	 * @throws Throwable
	 */
	protected abstract void doOpen() throws Throwable;

	/**
	 * Close client.
	 * 
	 * @throws Throwable
	 */
	protected abstract void doClose() throws Throwable;

	/**
	 * Connect to server.
	 * 
	 * @throws Throwable
	 */
	protected abstract void doConnect() throws Throwable;

	/**
	 * disConnect to server.
	 * 
	 * @throws Throwable
	 */
	protected abstract void doDisConnect() throws Throwable;

	/**
	 * Get the connected channel.
	 * 
	 * @return channel
	 */
	protected abstract Channel getChannel();
}
