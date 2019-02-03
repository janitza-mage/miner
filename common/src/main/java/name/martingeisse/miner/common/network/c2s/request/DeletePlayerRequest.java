/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s.request;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.MessageDecodingException;

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
	protected void encodeBody(ByteBuf buffer) {
		buffer.writeLong(id);
	}

	public static DeletePlayerRequest decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new DeletePlayerRequest(buffer.readLong());
	}

}
