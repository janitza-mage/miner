/*
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

import java.nio.ByteBuffer;

/**
 *
 */
public abstract class ReadableVector2d {

	public abstract double getX();

	public abstract double getY();

	public abstract Vector2d freeze();

	public final void encode(ByteBuffer buffer) {
		buffer.putDouble(getX());
		buffer.putDouble(getY());
	}

	public static final int ENCODED_SIZE = 16;

}
