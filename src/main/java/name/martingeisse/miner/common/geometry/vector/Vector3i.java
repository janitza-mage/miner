/**
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Value-object implementation of {@link ReadableVector3i}.
 */
public final class Vector3i extends ReadableVector3i {

	public final int x;
	public final int y;
	public final int z;

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Vector3i decode(ChannelBuffer buffer) {
		return new Vector3i(buffer.readInt(), buffer.readInt(), buffer.readInt());
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
	public int getZ() {
		return z;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Vector3i) {
			Vector3i otherVector = (Vector3i) other;
			return (x == otherVector.x && y == otherVector.y && z == otherVector.z);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(x).append(y).append(z).toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{Vector3i ");
		builder.append("x = ").append(x);
		builder.append(", y = ").append(y);
		builder.append(", z = ").append(z);
		builder.append('}');
		return builder.toString();
	}

	@Override
	public Vector3i freeze() {
		return this;
	}

}
