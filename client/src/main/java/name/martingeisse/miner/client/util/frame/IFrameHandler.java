/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.frame;

import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;

/**
 * Represents an action that gets repeated every frame.
 * 
 * A frame handler can have child handlers. The children's
 * methods are simply called after the parent's method; this
 * allows to combine handlers simple by returning other handlers
 * as children.
 */
public interface IFrameHandler {

	/**
	 * Handles a game step. This method performs the game logic.
	 * @throws BreakFrameLoopException if this handler wants to break the frame loop
	 */
	void handleStep() throws BreakFrameLoopException;
	
	/**
	 * Called just before calling {@link #draw(GlWorkerLoop)} on all handlers.
	 * This method should prepare data that this or other handlers might
	 * require in their draw() method.
	 * 
	 * @param glWorkerLoop the OpenGL worker loop
	 */
	void onBeforeDraw(GlWorkerLoop glWorkerLoop);
	
	/**
	 * Draws the screen contents using OpenGL.
	 * 
	 * @param glWorkerLoop the OpenGL worker loop
	 */
	void draw(GlWorkerLoop glWorkerLoop);

}
