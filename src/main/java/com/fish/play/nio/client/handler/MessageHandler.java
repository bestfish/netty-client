package com.fish.play.nio.client.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import com.fish.play.nio.client.bean.RequestEntity;
import com.fish.play.nio.client.bean.ResponseEntity;

public class MessageHandler extends ChannelHandlerAdapter {
	

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		RequestEntity entity = new RequestEntity();
		entity.setSerialNum(386);
		entity.setRequest("2");
		ctx.writeAndFlush(entity);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ResponseEntity result = (ResponseEntity) msg;
		System.out.println(result.getResponse());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
