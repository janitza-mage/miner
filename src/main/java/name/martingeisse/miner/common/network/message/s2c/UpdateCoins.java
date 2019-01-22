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
	protected void encodeBody(ChannelBuffer buffer) {
		buffer.writeLong(coins);
	}

	public static UpdateCoins decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new UpdateCoins(buffer.readLong());
	}

}
