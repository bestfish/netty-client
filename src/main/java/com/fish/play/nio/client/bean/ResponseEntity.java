package com.fish.play.nio.client.bean;

public class ResponseEntity {
	/**
	 * 业务唯一编号
	 */
	private long serialNum;
	/**
	 * 简单测试返回内容
	 */
	private String response;

	public long getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(long serialNum) {
		this.serialNum = serialNum;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
