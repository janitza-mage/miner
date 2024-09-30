/**
 * Copyright (c) 2011 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.frame;

import name.martingeisse.miner.client.engine.GlWorkerLoop;

/**
 * Performs a loop, typically drawing the world each
 * frame and potentially doing other things. At a lower
 * level, this loop object keeps a hierarchy of handlers that
 * are executed each frame. The hierarchy is anchored in a
 * single root handler of type {@link SwappableHandler} that allows
 * to put an arbitrary application handler -- typically itself
 * a {@link HandlerList} -- in place.
 *
 * This class can optionally store a GL worker loop. If this is the case, then
 * the following things happen:
 *
 * - the worker loop gets passed to all frame handlers when drawing, so the
 *   frame handlers can pass drawing WUs to the worker loop
 *
 * - the frame loop's call to glfwSwapBuffers() is passed to the worker
 *   loop instead of executed directly
 *
 * - if the worker loop is overloaded, whole drawing phases are skipped.
 *
 * - after drawing, this class adds a frame boundary marker to the GL worker loop
 *
 */
public final class FrameLoop {

	private final long windowId;

	/**
	 * the rootHandler
	 */
	private final SwappableHandler rootHandler;

	/**
	 * the glWorkerLoop
	 */
	private final GlWorkerLoop glWorkerLoop;

	/**
	 * Constructor.
	 */
	public FrameLoop(long windowId) {
		this(windowId, null);
	}

	/**
	 * Constructor.
	 * @param glWorkerLoop optional GL worker loop as explained in the class comment
	 */
	public FrameLoop(long windowId, GlWorkerLoop glWorkerLoop) {
		this.windowId = windowId;
		this.rootHandler = new SwappableHandler();
		this.glWorkerLoop = glWorkerLoop;
	}

	/**
	 * Getter method for the rootHandler.
	 * @return the rootHandler
	 */
	public SwappableHandler getRootHandler() {
		return rootHandler;
	}


}
