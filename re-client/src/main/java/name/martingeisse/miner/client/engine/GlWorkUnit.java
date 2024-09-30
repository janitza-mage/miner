/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine;

/**
 * Base class for all work units executed by the OpenGL worker thread.
 */
public abstract class GlWorkUnit {

	/**
	 * Executes this work unit.
	 */
	public abstract void execute();

}
