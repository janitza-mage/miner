/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.engine.prepare;

import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.cubes.Cubes;
import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.cubetype.CubeTypes;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;

/**
 * {@link IWrapPlane} implementation based on the {@link Cubes}
 * of the neighbor section. This class fetches the cubes lazily.
 */
public abstract class CubesBasedWrapPlane implements IWrapPlane {

	private Cubes cubes;

	@Override
	public CubeType getCubeType(AxisAlignedDirection direction, int u, int v) {
		if (cubes == null) {
			cubes = fetchCubes(direction);
		}
		final int plane = (direction.isNegative() ? Constants.SECTION_SIZE.getSize() - 1 : 0);
		final int x = direction.selectByAxis(plane, u, v);
		final int y = direction.selectByAxis(v, plane, u);
		final int z = direction.selectByAxis(u, v, plane);
		final int cubeTypeCode = (cubes.getCubeRelative(Constants.SECTION_SIZE, x, y, z) & 0xff);
		return CubeTypes.CUBE_TYPES[cubeTypeCode];
	}

	/**
	 * Fetches the {@link Cubes} for the neighbor section.
	 * @param direction the direction that points from the current section to the neighbor section
	 * @return the cubes
	 */
	protected abstract Cubes fetchCubes(AxisAlignedDirection direction);

}
