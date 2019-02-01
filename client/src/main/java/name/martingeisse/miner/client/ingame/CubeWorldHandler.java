/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame;

import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.client.util.frame.BreakFrameLoopException;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;

/**
 *
 */
public class CubeWorldHandler extends AbstractFrameHandler {

	@Override
	public void handleStep() throws BreakFrameLoopException {
		Ingame.get().getCubeWorldHelper().step();

		// TODO avoid filling up the render queue, should detect when the logic thread is running too fast
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
		}

	}

	@Override
	public void draw(GlWorkerLoop glWorkerLoop) {
		Ingame.get().getCubeWorldHelper().draw(glWorkerLoop);
	}

}
