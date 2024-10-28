/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s;

import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

import java.nio.ByteBuffer;

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
	protected int getExpectedBodySize() {
		return Vector3i.ENCODED_SIZE;
	}

	@Override
	protected void encodeBody(ByteBuffer buffer) {
		position.encode(buffer);
	}

	public static DigNotification decodeBody(ByteBuffer buffer) throws MessageDecodingException {
		return new DigNotification(Vector3i.decode(buffer));
	}

}
