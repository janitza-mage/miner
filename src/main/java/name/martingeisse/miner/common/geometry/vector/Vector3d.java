/**
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Value-object implementation of {@link ReadableVector3d}.
 */
public final class Vector3d extends ReadableVector3d {

	public final double x;
	public final double y;
	public final double z;

	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Vector3d decode(ChannelBuffer buffer) {
		return new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
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
	public double getZ() {
		return z;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Vector3d) {
			Vector3d otherVector = (Vector3d)other;
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
		StringBuilder builder = new StringBuilder("{Vector3d ");
		builder.append("x = ").append(x);
		builder.append(", y = ").append(y);
		builder.append(", z = ").append(z);
		builder.append('}');
		return builder.toString();
	}

	@Override
	public Vector3d freeze() {
		return this;
	}

}
