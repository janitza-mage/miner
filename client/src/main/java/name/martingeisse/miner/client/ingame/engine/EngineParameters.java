/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.engine;

import name.martingeisse.miner.client.util.lwjgl.StackdTexture;

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
	 * Constructor.
	 * @param cubeTextures the cube textures
	 */
	public EngineParameters(final StackdTexture[] cubeTextures) {
		this.cubeTextures = cubeTextures;
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

}
