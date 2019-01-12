/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message;

import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.c2s.UpdatePosition;
import name.martingeisse.miner.common.network.message.s2c.FlashMessage;
import name.martingeisse.miner.common.network.message.s2c.Hello;
import name.martingeisse.miner.common.network.message.s2c.UpdateCoins;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Base class for all network messages. These messages carry data between client and server. They can be encoded and
 * decoded to {@link StackdPacket}s for transfer.
 * <p>
 * Messages are immutable by design to simplify handling them. They are not value objects though; especially, equals /
 * hashCode support is based on identity.
 */
public abstract class Message {

	/**
	 * Encodes this message to a new network packet.
	 */
	public abstract StackdPacket encodePacket();

	/**
	 * Decodes a message from a network packet. The packet is expected to be in the initial state after receiving,
	 * that is, with the reader index positioned at the start of the packet body and the number of readable bytes
	 * equal to the packet body size.
	 */
	public static Message decodePacket(StackdPacket packet) throws MessageDecodingException {
		ChannelBuffer buffer = packet.getBuffer();
		switch (packet.getType()) {

			case MessageCodes.C2S_UPDATE_POSITION:
				return UpdatePosition.decodeBody(buffer);

			case MessageCodes.S2C_HELLO:
				return Hello.decodeBody(buffer);

			case MessageCodes.S2C_UPDATE_COINS:
				return UpdateCoins.decodeBody(buffer);

			case MessageCodes.S2C_FLASH_MESSAGE:
				return FlashMessage.decodeBody(buffer);

			default:
				throw new MessageDecodingException("unknown packet type: " + packet.getType());
		}
	}

	/**
	 * Validates that the remaining bytes in the specified buffer are equal to a certain expected size.
	 */
	protected static void validateSize(ChannelBuffer buffer, int expectedSizeInBytes) throws MessageDecodingException {
		if (buffer.readableBytes() != expectedSizeInBytes) {
			throw new MessageDecodingException("wrong packet size; expected " + expectedSizeInBytes + " but got " + buffer.readableBytes());
		}
	}
}
