/**
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

import io.netty.buffer.ByteBuf;

/**
 *
 */
public abstract class ReadableVector3i {

	public abstract int getX();
	public abstract int getY();
	public abstract int getZ();

	public abstract Vector3i freeze();

	public final void encode(ByteBuf buffer) {
		buffer.writeInt(getX());
		buffer.writeInt(getY());
		buffer.writeInt(getZ());
	}

	public static final int ENCODED_SIZE = 12;

}
