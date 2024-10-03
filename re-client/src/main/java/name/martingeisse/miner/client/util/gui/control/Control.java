/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.control;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;

/**
 * Base class to build higher-level controls from primitive elements.
 */
public class Control extends GuiElement {

	/**
	 * the controlRootElement
	 */
	private GuiElement controlRootElement;

	/**
	 * Constructor.
	 */
	public Control() {
	}

	/**
	 * Getter method for the controlRootElement.
	 * @return the controlRootElement
	 */
	public final GuiElement getControlRootElement() {
		return controlRootElement;
	}

	/**
	 * Setter method for the controlRootElement.
	 * @param controlRootElement the controlRootElement to set
	 */
	protected final void setControlRootElement(GuiElement controlRootElement) {
		this.controlRootElement = controlRootElement;
		if (controlRootElement != null) {
			controlRootElement.notifyAddedToParent(this);
		}
		requestLayout();
	}

	@Override
	public void handleInput(GuiLogicFrameContext context) {
		controlRootElement.handleInput(context);
	}

	@Override
	public void draw(GraphicsFrameContext context) {
		controlRootElement.draw(context);
	}

	@Override
	public final void requestSize(int width, int height) {
		controlRootElement.requestSize(width, height);
		setSize(controlRootElement.getWidth(), controlRootElement.getHeight());
	}

	@Override
	protected final void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		controlRootElement.setAbsolutePosition(absoluteX, absoluteY);
	}

	@Override
	public final ImmutableList<GuiElement> getChildren() {
		return ImmutableList.of(controlRootElement);
	}

}
