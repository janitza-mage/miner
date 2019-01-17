/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message;

import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.c2s.*;
import name.martingeisse.miner.common.network.message.s2c.*;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Base class for all network messages. These messages carry data between client and server. They can be encoded and
 * decoded to {@link StackdPacket}s for transfer.
 * <p>
 * Messages are immutable by design to simplify handling them. They are not value objects though; especially, equals /
 * hashCode support is based on identity.
 * <p>
 * TODO: Refactor using MessageTypeRegistry
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
		try {
			Message message = decodePacketInternal(packet);
			if (packet.getBuffer().readableBytes() != 0) {
				throw new MessageDecodingException("unexpected extra bytes at end of packet");
			}
			return message;
		} catch (IndexOutOfBoundsException e) {
			throw new MessageDecodingException("index out of bounds, probably unexpected end of packet", e);
		}
	}

	private static Message decodePacketInternal(StackdPacket packet) throws MessageDecodingException {
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

			case MessageCodes.C2S_RESUME_PLAYER:
				return ResumePlayer.decodeBody(buffer);

			case MessageCodes.C2S_DIG_NOTIFICATION:
				return DigNotification.decodeBody(buffer);

			case MessageCodes.S2C_PLAYER_LIST_UPDATE:
				return PlayerListUpdate.decodeBody(buffer);

			case MessageCodes.S2C_PLAYER_NAMES_UPDATE:
				return PlayerNamesUpdate.decodeBody(buffer);

			case MessageCodes.S2C_PLAYER_RESUMED:
				return PlayerResumed.decodeBody(buffer);

			case MessageCodes.S2C_SINGLE_SECTION_MODIFICATION_EVENT:
				return SingleSectionModificationEvent.decodeBody(buffer);

			case MessageCodes.C2S_CONSOLE_INPUT:
				return ConsoleInput.decodeBody(buffer);

			case MessageCodes.S2C_CONSOLE_OUTPUT:
				return ConsoleOutput.decodeBody(buffer);

			case MessageCodes.C2S_CUBE_MODIFICATION:
				return CubeModification.decodeBody(buffer);

			case MessageCodes.C2S_INTERACTIVE_SECTION_DATA_REQUEST:
				return InteractiveSectionDataRequest.decodeBody(buffer);

			default:
				throw new MessageDecodingException("unknown packet type: " + packet.getType());
		}
	}

}
