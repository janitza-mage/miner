/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.frame;

import name.martingeisse.miner.client.engine.FrameHandler;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.engine.LogicFrameContext;

/**
 * Base implementation of {@link FrameHandler}.
 */
public abstract class AbstractFrameHandler implements FrameHandler {

	@Override
	public void handleLogicFrame(LogicFrameContext context) {
	}

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
	}

}
