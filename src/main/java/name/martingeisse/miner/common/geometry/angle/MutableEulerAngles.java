/*
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.angle;

/**
 * Mutable implementation of {@link ReadableEulerAngles}.
 */
public class MutableEulerAngles extends ReadableEulerAngles {

	public double horizontalAngle;
	public double verticalAngle;
	public double rollAngle;

	public MutableEulerAngles(double horizontalAngle, double verticalAngle, double rollAngle) {
		this.horizontalAngle = horizontalAngle;
		this.verticalAngle = verticalAngle;
		this.rollAngle = rollAngle;
	}

	@Override
	public double getHorizontalAngle() {
		return horizontalAngle;
	}

	public void setHorizontalAngle(double horizontalAngle) {
		this.horizontalAngle = horizontalAngle;
	}

	@Override
	public double getVerticalAngle() {
		return verticalAngle;
	}

	public void setVerticalAngle(double verticalAngle) {
		this.verticalAngle = verticalAngle;
	}

	@Override
	public double getRollAngle() {
		return rollAngle;
	}

	public void setRollAngle(double rollAngle) {
		this.rollAngle = rollAngle;
	}

	public void copyFrom(ReadableEulerAngles other) {
		horizontalAngle = other.getHorizontalAngle();
		verticalAngle = other.getVerticalAngle();
		rollAngle = other.getRollAngle();
	}

	@Override
	public EulerAngles freeze() {
		return new EulerAngles(horizontalAngle, verticalAngle, rollAngle);
	}

}
