/**
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

	public static Vector3d decode(ByteBuf buffer) {
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

	public Vector3d add(Vector3d other) {
		return new Vector3d(x + other.x, y + other.y, z + other.z);
	}

	public Vector3d subtract(Vector3d other) {
		return new Vector3d(x + other.x, y + other.y, z + other.z);
	}

	public Vector3d multiply(double a) {
		return new Vector3d(x * a, y * a, z * a);
	}

	public Vector3d divide(double a) {
		return new Vector3d(x / a, y / a, z / a);
	}

	public double dot(Vector3d other) {
		return x * other.x + y * other.y + z * other.z;
	}

	public double normSquared() {
		return x * x + y * y + z * z;
	}

	public double norm() {
		return Math.sqrt(normSquared());
	}

	public double distanceSquared(Vector3d other) {
		double dx = x - other.x;
		double dy = y - other.y;
		double dz = z - other.z;
		return dx * dx + dy * dy + dz * dz;
	}

	public double distance(Vector3d other) {
		return Math.sqrt(distanceSquared(other));
	}

}
