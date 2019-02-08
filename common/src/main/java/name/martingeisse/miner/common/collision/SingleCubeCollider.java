/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.collision;

import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.geometry.RectangularRegion;
import name.martingeisse.miner.common.geometry.vector.Vector3i;

/**
 * A collider for a single world cube that is expected to be solid.
 */
public final class SingleCubeCollider implements IAxisAlignedCollider {

	private final Vector3i position;

	public SingleCubeCollider(Vector3i position) {
		this.position = position;
	}

	@Override
	public IAxisAlignedCollider getCurrentCollider() {
		return this;
	}

	@Override
	public boolean collides(final RectangularRegion detailCoordinateRegion) {
		return detailCoordinateRegion.divideAndRoundToOuter(Constants.GEOMETRY_DETAIL_CLUSTER_SIZE).contains(position);
	}

}
