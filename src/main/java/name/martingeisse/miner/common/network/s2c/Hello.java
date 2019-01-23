/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

/**
 *
 */
public final class Hello extends Message {

	private final int sessionId;

	public Hello(int sessionId) {
		this.sessionId = sessionId;
	}

	public int getSessionId() {
		return sessionId;
	}

	@Override
	protected int getExpectedBodySize() {
		return 4;
	}

	@Override
	protected void encodeBody(ByteBuf buffer) {
		buffer.writeInt(sessionId);
	}

	public static Hello decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new Hello(buffer.readInt());
	}

}
