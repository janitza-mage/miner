/**
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

import java.nio.ByteBuffer;

/**
 *
 */
public abstract class ReadableVector3d {

	public abstract double getX();

	public abstract double getY();

	public abstract double getZ();

	public abstract Vector3d freeze();

	public final void encode(ByteBuffer buffer) {
		buffer.putDouble(getX());
		buffer.putDouble(getY());
		buffer.putDouble(getZ());
	}

	public static final int ENCODED_SIZE = 24;

}
