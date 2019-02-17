/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.fill;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiEvent;

/**
 * Base class for elements that do not have any children and just fill
 * their available area with some graphic effect.
 */
public abstract class AbstractFillElement extends GuiElement {

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.GuiElement#handleEvent(name.martingeisse.stackd.client.gui.event.GuiEvent)
	 */
	@Override
	public final void handleEvent(GuiEvent event) {
		if (event == GuiEvent.DRAW) {
			draw();
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.GuiElement#finishLayout(int, int)
	 */
	@Override
	public final void requestSize(int width, int height) {
		setSize(width, height);
	}

	/**
	 * Draws this element.
	 */
	protected abstract void draw();

	@Override
	public ImmutableList<GuiElement> getChildren() {
		return ImmutableList.of();
	}

}
