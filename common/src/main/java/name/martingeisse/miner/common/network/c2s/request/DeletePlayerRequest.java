/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s.request;

import name.martingeisse.miner.common.network.MessageDecodingException;

import java.nio.ByteBuffer;

/**
 *
 */
public final class DeletePlayerRequest extends Request {

	private final long id;

	public DeletePlayerRequest(long id) {
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
	protected void encodeBody(ByteBuffer buffer) {
		buffer.putLong(id);
	}

	public static DeletePlayerRequest decodeBody(ByteBuffer buffer) throws MessageDecodingException {
		return new DeletePlayerRequest(buffer.getLong());
	}

}
