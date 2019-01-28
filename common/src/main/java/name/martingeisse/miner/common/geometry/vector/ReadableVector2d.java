/*
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

import io.netty.buffer.ByteBuf;

/**
 *
 */
public abstract class ReadableVector2d {

	public abstract double getX();
	public abstract double getY();

	public abstract Vector2d freeze();

	public final void encode(ByteBuf buffer) {
		buffer.writeDouble(getX());
		buffer.writeDouble(getY());
	}

	public static final int ENCODED_SIZE = 16;

}
