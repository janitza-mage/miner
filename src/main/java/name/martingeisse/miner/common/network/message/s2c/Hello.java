/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message.s2c;

import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;

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
	protected void encodeBody(ChannelBuffer buffer) {
		buffer.writeInt(sessionId);
	}

	public static Hello decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new Hello(buffer.readInt());
	}

}
