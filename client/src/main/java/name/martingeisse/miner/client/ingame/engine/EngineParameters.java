/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.engine;

import name.martingeisse.miner.client.util.lwjgl.StackdTexture;
import name.martingeisse.miner.common.cubetype.CubeType;

import java.util.Arrays;

/**
 * Static parameters for the engine, such as various strategies. TODO replace references to this by references to
 * cube type singletons / injected, then remove this class.
 */
public final class EngineParameters {

	/**
	 * the cubeTextures
	 */
	private final StackdTexture[] cubeTextures;

	/**
	 * the cubeTypes
	 */
	private final CubeType[] cubeTypes;

	/**
	 * Constructor.
	 * @param cubeTextures the cube textures
	 * @param cubeTypes defines how different cubes behave
	 */
	public EngineParameters(final StackdTexture[] cubeTextures, final CubeType[] cubeTypes) {
		this.cubeTextures = cubeTextures;
		this.cubeTypes = cubeTypes;
	}

	/**
	 * @return the number of cube textures
	 */
	public int getCubeTextureCount() {
		return cubeTextures.length;
	}
	
	/**
	 * Returns the cube texture for the specified cube texture index.
	 * @param cubeTextureIndex the cube texture index
	 * @return the cube texture
	 */
	public StackdTexture getCubeTexture(final int cubeTextureIndex) {
		return cubeTextures[cubeTextureIndex];
	}

	/**
	 * Returns a copy of the internal cube texture array.
	 * @return a new array containing all cube textures
	 */
	public StackdTexture[] getCubeTextures() {
		return Arrays.copyOf(cubeTextures, cubeTextures.length);
	}

	/**
	 * @return the number of cube types
	 */
	public int getCubeTypeCount() {
		return cubeTypes.length;
	}
	
	/**
	 * Returns the cube type for the specified type index.
	 * @param cubeTypeIndex the cube type index
	 * @return the cube type
	 */
	public CubeType getCubeType(final int cubeTypeIndex) {
		return cubeTypes[cubeTypeIndex];
	}

	/**
	 * Returns a copy of the internal cube type array.
	 * @return a new array containing all cube types
	 */
	public CubeType[] getCubeTypes() {
		return Arrays.copyOf(cubeTypes, cubeTypes.length);
	}

}
