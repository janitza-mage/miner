/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.network;

import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.vector.Vector3d;

/**
 *
 */
public final class Avatar {

	private Vector3d position = new Vector3d(0, 0, 0);
	private EulerAngles orientation = new EulerAngles(0, 0, 0);
	private String name = "unnamed";

	public Vector3d getPosition() {
		return position;
	}

	public void setPosition(Vector3d position) {
		if (position == null) {
			throw new IllegalArgumentException("position cannot be null");
		}
		this.position = position;
	}

	public EulerAngles getOrientation() {
		return orientation;
	}

	public void setOrientation(EulerAngles orientation) {
		if (orientation == null) {
			throw new IllegalArgumentException("orientation cannot be null");
		}
		this.orientation = orientation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null");
		}
		this.name = name;
	}

}
