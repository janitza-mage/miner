/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message.c2s;

import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageCodes;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 *
 */
public final class DigNotification extends Message {

	private final Vector3i position;

	public DigNotification(Vector3i position) {
		this.position = position;
	}

	public Vector3i getPosition() {
		return position;
	}

	@Override
	public StackdPacket encodePacket() {
		StackdPacket packet = new StackdPacket(MessageCodes.C2S_DIG_NOTIFICATION, Vector3i.ENCODED_SIZE);
		position.encode(packet.getBuffer());
		return packet;
	}

	public static DigNotification decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new DigNotification(Vector3i.decode(buffer));
	}

}
