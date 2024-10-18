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

	public static volatile int openglTimeMilliseconds = 0;

	public static GlWorkUnit NOP_WORK_UNIT = new GlWorkUnit() {
		@Override
		public void execute() {
		}
	};

	/**
	 * Executes this work unit.
	 */
	public abstract void execute();

}
