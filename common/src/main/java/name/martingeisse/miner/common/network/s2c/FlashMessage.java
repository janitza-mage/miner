/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c;

import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public final class FlashMessage extends Message {

	private final String text;

	public FlashMessage(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	protected int getExpectedBodySize() {
		return -1;
	}

	@Override
	protected void encodeBody(ByteBuffer buffer) {
		buffer.putBytes(text.getBytes(StandardCharsets.UTF_8));
	}

	public static FlashMessage decodeBody(ByteBuffer buffer) throws MessageDecodingException {
		byte[] binary = new byte[buffer.readableBytes()];
		buffer.readBytes(binary);
		String text = new String(binary, StandardCharsets.UTF_8); // replaces broken characters
		return new FlashMessage(text);
	}

}
