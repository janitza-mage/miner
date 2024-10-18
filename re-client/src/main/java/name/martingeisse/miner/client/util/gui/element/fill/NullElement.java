/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.fill;

import name.martingeisse.miner.client.engine.GraphicsFrameContext;

/**
 * An invisible element that does nothing. This can be used as a
 * placeholder within controls for a place where other elements
 * can go later.
 */
public final class NullElement extends AbstractFillElement {

	/**
	 * The shared instance of this class.
	 */
	public static final NullElement instance = new NullElement();

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
	}

}
