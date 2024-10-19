/**
 * Copyright (c) 2011 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine.temp_old;

/**
 * Represents an action that gets repeated every frame.
 * <p>
 * If requested, this class separates graphics frames (repeated as often as possible) from logic frames (repeated in a
 * fixed interval). Both are called by the game thread to avoid having to synchronize access to the game state.
 */
public interface FrameHandler {

	/**
	 * Handles a logic frame. This method performs the game logic.
	 */
	void handleLogicFrame(LogicFrameContext context);

	/**
	 * Handles a graphics frame. This method draws the screen contents by passing OpenGL work units to the context
	 * which are then drawn by the OpenGL thread.
	 */
	void handleGraphicsFrame(GraphicsFrameContext context);

}
