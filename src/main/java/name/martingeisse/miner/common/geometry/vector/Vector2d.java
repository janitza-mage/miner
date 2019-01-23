/*
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Value-object implementation of {@link ReadableVector2d}.
 */
public final class Vector2d extends ReadableVector2d {

	public final double x;
	public final double y;

	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static Vector2d decode(ByteBuf buffer) {
		return new Vector2d(buffer.readDouble(), buffer.readDouble());
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Vector2d) {
			Vector2d otherVector = (Vector2d) other;
			return (x == otherVector.x && y == otherVector.y);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(x).append(y).toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{Vector2d ");
		builder.append("x = ").append(x);
		builder.append(", y = ").append(y);
		builder.append('}');
		return builder.toString();
	}

	@Override
	public Vector2d freeze() {
		return this;
	}

}
