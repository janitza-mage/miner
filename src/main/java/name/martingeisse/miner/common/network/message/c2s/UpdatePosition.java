/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message.c2s;

import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.angle.ReadableEulerAngles;
import name.martingeisse.miner.common.geometry.vector.ReadableVector3d;
import name.martingeisse.miner.common.geometry.vector.Vector3d;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 *
 */
public final class UpdatePosition extends Message {

	private final Vector3d position;
	private final EulerAngles orientation;

	public UpdatePosition(ReadableVector3d position, ReadableEulerAngles orientation) {
		this.position = position.freeze();
		this.orientation = orientation.freeze();
	}

	public Vector3d getPosition() {
		return position;
	}

	public EulerAngles getOrientation() {
		return orientation;
	}

	@Override
	protected int getExpectedBodySize() {
		return Vector3d.ENCODED_SIZE + EulerAngles.ENCODED_SIZE;
	}

	@Override
	protected void encodeBody(ChannelBuffer buffer) {
		position.encode(buffer);
		orientation.encode(buffer);
	}

	public static UpdatePosition decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new UpdatePosition(Vector3d.decode(buffer), EulerAngles.decode(buffer));
	}

}
