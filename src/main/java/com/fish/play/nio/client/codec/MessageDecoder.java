package com.fish.play.nio.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

import com.fish.play.nio.client.bean.ResponseEntity;

public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {
	private final Charset charset;

	public MessageDecoder() {
		this(Charset.defaultCharset());
	}

	public MessageDecoder(Charset charset) {
		if (charset == null) {
			throw new NullPointerException("charset");
		}
		this.charset = charset;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		int lengthOfSerialNumAndRequest = msg.readInt();
		long serialNum = msg.readLong();
		ByteBuf byteBuf = msg.readBytes(lengthOfSerialNumAndRequest - 8);
		String request = byteBuf.toString(charset);
		ResponseEntity entity = new ResponseEntity();
		entity.setSerialNum(serialNum);
		entity.setResponse(request);
		
		out.add(entity);
	}

}
