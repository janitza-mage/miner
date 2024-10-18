/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.wrapper;

import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;

/**
 * This element is only visible and only accepts elements when
 * the mouse cursor is over it. Layout is independent of the
 * mouse though.
 */
public final class MouseOverWrapper extends AbstractWrapperElement {

	private boolean visible;

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

	@Override
	public void requestSize(final int width, final int height) {
		var wrapped = getWrappedElement();
		wrapped.requestSize(width, height);
		setSize(wrapped.getWidth(), wrapped.getHeight());
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		getWrappedElement().setAbsolutePosition(absoluteX, absoluteY);
	}

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
		visible = isMouseInside(context);
		if (visible) {
			getWrappedElement().handleLogicFrame(context);
		}
	}

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
		if (visible) {
			getWrappedElement().handleGraphicsFrame(context);
		}
	}
}
