/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine.gui.element.fill;

import name.martingeisse.miner.client.engine.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.engine.gui.element.LeafElement;

/**
 * Base class for elements that do not have any children and just fill their available area with some graphic effect
 * whose work unit gets dynamically created each frame. This is somewhat expensive and should not be used for elements
 * that get used in large numbers. See the {@link AbstractFillElement} subclass which caches its work unit.
 */
public abstract class AbstractDynamicFillElement extends LeafElement {

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
	}

	@Override
	public void requestSize(int width, int height) {
		setSize(width, height);
	}

}
