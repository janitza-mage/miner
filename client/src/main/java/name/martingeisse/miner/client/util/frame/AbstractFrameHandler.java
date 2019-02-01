/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.frame;

import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;

/**
 * Base implementation of {@link IFrameHandler}.
 */
public abstract class AbstractFrameHandler implements IFrameHandler {

	@Override
	public void handleStep() throws BreakFrameLoopException {
	}

	@Override
	public void draw(GlWorkerLoop glWorkerLoop) {
	}

}
