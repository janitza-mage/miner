/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c;

import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

import java.nio.ByteBuffer;

/**
 *
 */
public final class UpdateCoins extends Message {

	private final long coins;

	public UpdateCoins(long coins) {
		this.coins = coins;
	}

	public long getCoins() {
		return coins;
	}

	@Override
	protected int getExpectedBodySize() {
		return 8;
	}

	@Override
	protected void encodeBody(ByteBuffer buffer) {
		buffer.putLong(coins);
	}

	public static UpdateCoins decodeBody(ByteBuffer buffer) throws MessageDecodingException {
		return new UpdateCoins(buffer.getLong());
	}

}
