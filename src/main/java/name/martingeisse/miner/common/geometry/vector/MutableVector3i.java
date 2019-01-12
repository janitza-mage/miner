/**
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.vector;

/**
 * Mutable implementation of {@link ReadableVector3i}.
 */
public class MutableVector3i extends ReadableVector3i {

	public int x;
	public int y;
	public int z;

	public MutableVector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
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

	@Override
	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void copyFrom(ReadableVector3i other) {
		x = other.getX();
		y = other.getY();
		z = other.getZ();
	}

	@Override
	public Vector3i freeze() {
		return new Vector3i(x, y, z);
	}

}
