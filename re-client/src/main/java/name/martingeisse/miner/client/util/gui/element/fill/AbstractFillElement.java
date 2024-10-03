/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.fill;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;

/**
 * Base class for elements that do not have any children and just fill
 * their available area with some graphic effect.
 */
public abstract class AbstractFillElement extends GuiElement {

	@Override
	public void handleInput(GuiLogicFrameContext context) {
	}

	@Override
	public final void requestSize(int width, int height) {
		setSize(width, height);
	}

	@Override
	public ImmutableList<GuiElement> getChildren() {
		return ImmutableList.of();
	}

}
