/**
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

/**
 * Mutable implementation of {@link ReadableVector2i}.
 */
public class MutableVector2i extends ReadableVector2i {

	public int x;
	public int y;

	public MutableVector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	@Override
	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void copyFrom(ReadableVector2i other) {
		x = other.getX();
		y = other.getY();
	}

	@Override
	public Vector2i freeze() {
		return new Vector2i(x, y);
	}

}
