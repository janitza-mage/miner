/*
 * Copyright (c) 2014 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.geometry.angle;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Value-object implementation of {@link ReadableEulerAngles}.
 */
public final class EulerAngles extends ReadableEulerAngles {

	public final double horizontalAngle;
	public final double verticalAngle;
	public final double rollAngle;

	public EulerAngles(double horizontalAngle, double verticalAngle, double rollAngle) {
		this.horizontalAngle = horizontalAngle;
		this.verticalAngle = verticalAngle;
		this.rollAngle = rollAngle;
	}

	public static EulerAngles decode(ByteBuf buffer) {
		return new EulerAngles(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
	}

	@Override
	public double getHorizontalAngle() {
		return horizontalAngle;
	}

	@Override
	public double getVerticalAngle() {
		return verticalAngle;
	}

	@Override
	public double getRollAngle() {
		return rollAngle;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof EulerAngles) {
			EulerAngles otherAngles = (EulerAngles) other;
			return (horizontalAngle == otherAngles.horizontalAngle && verticalAngle == otherAngles.verticalAngle && rollAngle == otherAngles.rollAngle);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(horizontalAngle).append(verticalAngle).append(rollAngle).toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{EulerAngles ");
		builder.append("horizontalAngle = ").append(horizontalAngle);
		builder.append(", verticalAngle = ").append(verticalAngle);
		builder.append(", rollAngle = ").append(rollAngle);
		builder.append('}');
		return builder.toString();
	}

	@Override
	public EulerAngles freeze() {
		return this;
	}

}
