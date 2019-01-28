/*
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

/**
 * Mutable implementation of {@link ReadableVector2d};
 */
public class MutableVector2d extends ReadableVector2d {

	public double x;
	public double y;

	public MutableVector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	@Override
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void copyFrom(ReadableVector2d other) {
		x = other.getX();
		y = other.getY();
	}

	@Override
	public Vector2d freeze() {
		return new Vector2d(x, y);
	}

}
