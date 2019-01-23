/*
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.angle;

import io.netty.buffer.ByteBuf;

/**
 * Euler angles (actually, nautical angles), expressed as a horizontal angle (yaw),
 * vertical angle (pitch) and roll angle. All angles are expressed in degrees, not radians.
 */
public abstract class ReadableEulerAngles {

	public abstract double getHorizontalAngle();
	public abstract double getVerticalAngle();
	public abstract double getRollAngle();

	/**
	 * Returns an immutable copy of this object.
	 */
	public abstract EulerAngles freeze();

	public final void encode(ByteBuf buffer) {
		buffer.writeDouble(getHorizontalAngle());
		buffer.writeDouble(getVerticalAngle());
		buffer.writeDouble(getRollAngle());
	}

	public static final int ENCODED_SIZE = 24;

}
