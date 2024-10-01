/**
 * Copyright (c) 2013 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.game;

import name.martingeisse.miner.client.engine.GraphicsFrameContext;

/**
 * A template that can be rendered to represent objects of type T.
 * Rendering will be done by passing work units to a GL worker and may
 * be affected by the current OpenGL state. Especially, there is no
 * position / orientation information being passed to the template --
 * prepare appropriate OpenGL transformations for that.
 */
public interface VisualTemplate<T> {

	/**
	 * Renders the specified subject using this template.
	 * <p></p>
	 * This method must be called from the application thread.
	 *
	 * @param subject the subject to render
	 * @param context the graphics frame context
	 */
	void render(T subject, GraphicsFrameContext context);

}
