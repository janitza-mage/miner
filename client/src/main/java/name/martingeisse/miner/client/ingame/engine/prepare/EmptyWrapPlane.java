/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.engine.prepare;

import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.cubetype.CubeTypes;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;

/**
 * {@link IWrapPlane} implementation that reports all cubes as
 * nonsolid. This is a very simple implementation that creates too many visible faces
 * in most cases. This does not produce visible artifacts but slows down rendering.
 */
public final class EmptyWrapPlane implements IWrapPlane {

	@Override
	public CubeType getCubeType(AxisAlignedDirection direction, int u, int v) {
		return CubeTypes.CUBE_TYPES[0];
	}

}
