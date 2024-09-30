/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.frame;

import name.martingeisse.miner.client.engine.FrameHandler;
import name.martingeisse.miner.client.engine.GlWorkerLoop;

/**
 * Base implementation of {@link FrameHandler}.
 */
public abstract class AbstractFrameHandler implements FrameHandler {

	@Override
	public void handleStep(FrameLogicContext context) throws BreakFrameLoopException {
	}

	@Override
	public void draw(GlWorkerLoop glWorkerLoop) {
	}

}
