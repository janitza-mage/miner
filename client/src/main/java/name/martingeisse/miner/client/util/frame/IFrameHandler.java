/**
 * Copyright (c) 2011 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.frame;

import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;

/**
 * Represents an action that gets repeated every frame.
 * <p>
 * TODO plan for the future (currently fixed-interval logic frames aren't really needed)
 * This class separates drawing frames (repeated as often as possible) from logic frames (repeated in a fixed interval).
 * Both are called by the main game thread.
 */
public interface IFrameHandler {

	/**
	 * Handles a game step. This method performs the game logic.
	 *
	 * @throws BreakFrameLoopException if this handler wants to break the frame loop
	 */
	void handleStep() throws BreakFrameLoopException;

	/**
	 * Draws the screen contents using OpenGL.
	 *
	 * @param glWorkerLoop the OpenGL worker loop
	 */
	void draw(GlWorkerLoop glWorkerLoop);

}
