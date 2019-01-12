/**
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

/**
 * Mutable implementation of {@link ReadableVector3d}.
 */
public class MutableVector3d extends ReadableVector3d {

	public double x;
	public double y;
	public double z;

	public MutableVector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
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

	@Override
	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void copyFrom(ReadableVector3d other) {
		x = other.getX();
		y = other.getY();
		z = other.getZ();
	}

	@Override
	public Vector3d freeze() {
		return new Vector3d(x, y, z);
	}

}
