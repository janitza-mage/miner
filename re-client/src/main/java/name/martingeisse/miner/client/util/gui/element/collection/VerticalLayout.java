/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.collection;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.util.HorizontalAlignment;
import name.martingeisse.miner.common.util.contract.ParameterUtil;

/**
 * This layout arranges its elements vertically, then aligns
 * them horizontally using the specified alignment.
 */
public final class VerticalLayout extends AbstractListElement {

	/**
	 * the alignment
	 */
	private HorizontalAlignment alignment;

	/**
	 * Constructor.
	 */
	public VerticalLayout() {
		this.alignment = HorizontalAlignment.CENTER;
	}

	/**
	 * Getter method for the alignment.
	 * @return the alignment
	 */
	public HorizontalAlignment getAlignment() {
		return alignment;
	}

	/**
	 * Setter method for the alignment.
	 * @param alignment the alignment to set
	 * @return this for chaining
	 */
	public VerticalLayout setAlignment(HorizontalAlignment alignment) {
		ParameterUtil.ensureNotNull(alignment, "alignment");
		this.alignment = alignment;
		return this;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.GuiElement#finishLayoutSize(int, int)
	 */
	@Override
	public void requestSize(int width, int height) {
		int requiredWidth = width, requiredHeight = 0;
		for (GuiElement element : getWrappedElements()) {
			element.requestSize(width, Gui.GRID);
			requiredWidth = Math.max(requiredWidth, element.getWidth());
			requiredHeight += element.getHeight();
		}
		setSize(requiredWidth, requiredHeight);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.GuiElement#setChildrenLayoutPosition(int, int)
	 */
	@Override
	protected void setChildrenLayoutPosition(int absoluteX, int absoluteY) {
		int width = getWidth();
		for (GuiElement element : getWrappedElements()) {
			element.setAbsolutePosition(absoluteX + alignment.alignSpan(width, element.getWidth()), absoluteY);
			absoluteY += element.getHeight();
		}
	}

}
