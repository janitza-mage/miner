/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.frame;

import name.martingeisse.miner.client.engine.FrameHandler;
import name.martingeisse.miner.client.engine.GlWorkerLoop;

import java.util.ArrayList;

/**
 * This handler is a list of other handlers. Each method call is
 * forwarded to all of them.
 */
public class HandlerList extends ArrayList<FrameHandler> implements FrameHandler {

	/**
	 * Constructor.
	 */
	public HandlerList() {
	}

	@Override
	public void handleStep(FrameLogicContext context) throws BreakFrameLoopException {
		for (FrameHandler handler : this) {
			handler.handleStep(context);
		}
	}

	@Override
	public void draw(GlWorkerLoop glWorkerLoop) {
		for (FrameHandler handler : this) {
			handler.draw(glWorkerLoop);
		}
	}

}
