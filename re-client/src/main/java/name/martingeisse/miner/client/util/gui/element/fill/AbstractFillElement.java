/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.fill;

import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.util.gui.util.LeafElement;

/**
 * Base class for elements that do not have any children and just fill
 * their available area with some graphic effect.
 */
public abstract class AbstractFillElement extends LeafElement {

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
	}

	@Override
	public final void requestSize(int width, int height) {
		setSize(width, height);
	}

}
