/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c.response;

import name.martingeisse.miner.common.network.MessageDecodingException;

import java.nio.ByteBuffer;

/**
 * General-purpose response for request messages that use a request-response scheme but don't have any data to
 * return in the response.
 */
public final class OkayResponse extends Response {

	@Override
	protected int getExpectedBodySize() {
		return 0;
	}

	@Override
	protected void encodeBody(ByteBuffer buffer) {
	}

	public static OkayResponse decodeBody(ByteBuffer buffer) throws MessageDecodingException {
		return new OkayResponse();
	}

}
