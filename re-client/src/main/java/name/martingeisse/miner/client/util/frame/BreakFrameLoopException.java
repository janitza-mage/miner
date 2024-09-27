/**
 * Copyright (c) 2011 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.frame;

/**
 * Frame handlers can throw this exception to stop the frame loop.
 */
public final class BreakFrameLoopException extends RuntimeException {

	/**
	 * Constructor.
	 */
	public BreakFrameLoopException() {
	}

}
