/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message.s2c;

import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageCodes;
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
	public StackdPacket encodePacket() {
		StackdPacket packet = new StackdPacket(MessageCodes.S2C_UPDATE_COINS, 8);
		ChannelBuffer buffer = packet.getBuffer();
		buffer.writeLong(coins);
		return packet;
	}

	public static UpdateCoins decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new UpdateCoins(buffer.readLong());
	}

}
