/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

/**
 *
 */
public final class ResumePlayer extends Message {

	private final long id;

	public ResumePlayer(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	@Override
	protected int getExpectedBodySize() {
		return 8;
	}

	@Override
	protected void encodeBody(ByteBuf buffer) {
		buffer.writeLong(id);
	}

	public static ResumePlayer decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new ResumePlayer(buffer.readLong());
	}

}
