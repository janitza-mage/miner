/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s;

import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;
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
	protected int getPacketBodySize() {
		return Vector3i.ENCODED_SIZE;
	}

	@Override
	protected void encodeBody(ChannelBuffer buffer) {
		position.encode(buffer);
	}

	public static DigNotification decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new DigNotification(Vector3i.decode(buffer));
	}

}
