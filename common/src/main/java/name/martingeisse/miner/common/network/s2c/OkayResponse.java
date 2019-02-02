/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

/**
 * General-purpose response for request messages that use a request-response scheme but don't have any data to
 * return in the response.
 */
public final class OkayResponse extends Message {

	@Override
	protected int getExpectedBodySize() {
		return 0;
	}

	@Override
	protected void encodeBody(ByteBuf buffer) {
	}

	public static OkayResponse decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new OkayResponse();
	}

}
