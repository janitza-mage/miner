/**
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Value-object implementation of {@link ReadableVector2i}.
 */
public final class Vector2i extends ReadableVector2i {

	public final int x;
	public final int y;

	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static Vector2i decode(ByteBuf buffer) {
		return new Vector2i(buffer.readInt(), buffer.readInt());
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Vector2i) {
			Vector2i otherVector = (Vector2i) other;
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
		StringBuilder builder = new StringBuilder("{Vector2i ");
		builder.append("x = ").append(x);
		builder.append(", y = ").append(y);
		builder.append('}');
		return builder.toString();
	}

	@Override
	public Vector2i freeze() {
		return this;
	}

}
