/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.frame;

import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;

import java.util.ArrayList;

/**
 * This handler is a list of other handlers. Each method call is
 * forwarded to all of them.
 */
public class HandlerList extends ArrayList<IFrameHandler> implements IFrameHandler {

	/**
	 * Constructor.
	 */
	public HandlerList() {
	}

	@Override
	public void handleStep() throws BreakFrameLoopException {
		for (IFrameHandler handler : this) {
			handler.handleStep();
		}
	}

	@Override
	public void draw(GlWorkerLoop glWorkerLoop) {
		for (IFrameHandler handler : this) {
			handler.draw(glWorkerLoop);
		}
	}

}
