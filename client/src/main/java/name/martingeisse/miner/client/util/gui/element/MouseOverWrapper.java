/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element;

import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiEvent;

/**
 * This element is only visible and only accepts elements when
 * the mouse cursor is over it. Layout is independent of the
 * mouse though.
 */
public final class MouseOverWrapper extends AbstractWrapperElement {

	/**
	 * Constructor.
	 */
	public MouseOverWrapper() {
		super();
	}

	/**
	 * Constructor.
	 * @param wrappedElement the wrapped element
	 */
	public MouseOverWrapper(final GuiElement wrappedElement) {
		super(wrappedElement);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.GuiElement#finishLayoutSize(int, int)
	 */
	@Override
	public void requestSize(final int width, final int height) {
		requireWrappedElement();
		getWrappedElement().requestSize(width, height);
		setSize(getWrappedElement().getWidth(), getWrappedElement().getHeight());
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.GuiElement#setChildrenLayoutPosition(int, int)
	 */
	@Override
	protected void setChildrenLayoutPosition(int absoluteX, int absoluteY) {
		requireWrappedElement();
		getWrappedElement().setAbsolutePosition(absoluteX, absoluteY);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.GuiElement#handleEvent(name.martingeisse.stackd.client.gui.GuiEvent)
	 */
	@Override
	public void handleEvent(final GuiEvent event) {
		requireWrappedElement();
		if (isMouseInside()) {
			getWrappedElement().handleEvent(event);
		}
	}

}
