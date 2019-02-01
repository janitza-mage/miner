/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.frame;

import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;

/**
 * This is a simple handler that contains another wrapped handler
 * and allows to swap that wrapped handler during runtime.
 * All handler methods are simply forwarded to the wrapped handler,
 * or skipped when the wrapped handler was set to null.
 */
public final class SwappableHandler implements IFrameHandler {

	/**
	 * the wrappedHandler
	 */
	private IFrameHandler wrappedHandler;

	/**
	 * Constructor.
	 */
	public SwappableHandler() {
	}

	/**
	 * Constructor.
	 * @param wrappedHandler the wrapped handler
	 */
	public SwappableHandler(IFrameHandler wrappedHandler) {
		this.wrappedHandler = wrappedHandler;
	}

	/**
	 * Getter method for the wrappedHandler.
	 * @return the wrappedHandler
	 */
	public IFrameHandler getWrappedHandler() {
		return wrappedHandler;
	}

	/**
	 * Setter method for the wrappedHandler.
	 * @param wrappedHandler the wrappedHandler to set
	 */
	public void setWrappedHandler(IFrameHandler wrappedHandler) {
		this.wrappedHandler = wrappedHandler;
	}

	@Override
	public void handleStep() throws BreakFrameLoopException {
		if (wrappedHandler != null) {
			wrappedHandler.handleStep();
		}
	}

	@Override
	public void draw(GlWorkerLoop glWorkerLoop) {
		if (wrappedHandler != null) {
			wrappedHandler.draw(glWorkerLoop);
		}
	}

}
