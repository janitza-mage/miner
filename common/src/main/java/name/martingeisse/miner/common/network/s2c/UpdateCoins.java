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
	protected void encodeBody(ByteBuf buffer) {
		buffer.writeLong(coins);
	}

	public static UpdateCoins decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new UpdateCoins(buffer.readLong());
	}

}
