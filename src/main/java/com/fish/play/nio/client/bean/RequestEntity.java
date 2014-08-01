package com.fish.play.nio.client.bean;

public class RequestEntity {
	/**
	 * 业务唯一编号
	 */
	private long serialNum;
	/**
	 * 简单测试请求内容
	 */
	private String request;

	public long getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(long serialNum) {
		this.serialNum = serialNum;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

}
