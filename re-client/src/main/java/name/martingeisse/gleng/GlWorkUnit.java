/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gleng;

/**
 * Base class for all work units executed by the OpenGL worker thread.
 */
public abstract class GlWorkUnit {

	public static volatile int openglTimeMilliseconds = 0;

	/**
	 * Implementation of this work unit -- to be called from the OpenGL thread.
	 */
	protected abstract void gl__Execute();

	/**
	 * Override this method to return true for any work unit that can be skipped under high load.
	 */
	protected boolean isSkippable() {
		return false;
	}

	/**
	 * Schedules this work unit for execution -- to be called from the application thread.
	 */
	public final void schedule() {
		Gleng.engine.glWorkerLoop.schedule(this);
	}

}
