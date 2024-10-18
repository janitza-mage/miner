/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.atom;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;

/**
 * A simple invisible element with fixed size that is meant to take up space.
 */
public final class Spacer extends GuiElement {

	/**
	 * Constructor for a square spacer.
	 */
	public Spacer(final int size) {
		this(size, size);
	}

	/**
	 * Constructor for a rectangular spacer.
	 */
	public Spacer(final int width, final int height) {
		setSize(width, height);
	}

	@Override
	public void requestSize(int width, int height) {
	}

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
	}

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
	}

	@Override
	public ImmutableList<GuiElement> getChildren() {
		return ImmutableList.of();
	}

}
