/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

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
	public StackdPacket encodePacket() {
		int messageCode = MessageTypeRegistry.INSTANCE.getCodeForClass(getClass());
		int bodySize = getPacketBodySize();
		if (bodySize < 0) {
			ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
			buffer.writeZero(StackdPacket.HEADER_SIZE);
			encodeBody(buffer);
			return new StackdPacket(messageCode, buffer, false);
		} else {
			StackdPacket packet = new StackdPacket(messageCode, bodySize);
			encodeBody(packet.getBuffer());
			return packet;
		}
	}

	/**
	 * Returns the packet body size, if known before actually encoding a packet, or a negative value to indicate that
	 * the size can only be determined by actually encoding the packet and a dynamic buffer is therefore needed.
	 */
	protected abstract int getPacketBodySize();

	/**
	 * Encodes the packet body to the specified buffer.
	 */
	protected abstract void encodeBody(ChannelBuffer buffer);

	/**
	 * Decodes a message from a network packet. The packet is expected to be in the initial state after receiving,
	 * that is, with the reader index positioned at the start of the packet body and the number of readable bytes
	 * equal to the packet body size.
	 */
	public static Message decodePacket(StackdPacket packet) throws MessageDecodingException {
		return MessageTypeRegistry.INSTANCE.decodePacket(packet);
	}

}
