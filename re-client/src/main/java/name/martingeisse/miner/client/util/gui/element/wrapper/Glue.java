/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.wrapper;

import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.element.fill.NullElement;
import name.martingeisse.miner.client.util.gui.util.AreaAlignment;
import name.martingeisse.miner.common.util.contract.ParameterUtil;

/**
 * This element asks its wrapped element to be as small as possible, then takes up the remaining space to behave as the
 * size its enclosing element expects. An {@link AreaAlignment} controls the positioning of the wrapped element.
 * <p>
 * Boolean parameters are used to control whether any space is actually filled along both the horizontal and vertical
 * axes.
 * <p>
 * TODO why make the wrapped element as small as possible and not pass on the requested size? Might use another
 * element, e.g. {@link Sizer}, to make the wrapped element as small as possible.
 */
public final class Glue extends AbstractWrapperElement {

	private boolean fillHorizontal;
	private boolean fillVertical;
	private AreaAlignment alignment;

	/**
	 * Constructor using CENTER alignment.
	 */
	public Glue() {
		this(NullElement.instance);
	}

	/**
	 * Constructor using CENTER alignment.
	 * @param wrappedElement the wrapped element
	 */
	public Glue(GuiElement wrappedElement) {
		super(wrappedElement);
		this.fillHorizontal = false;
		this.fillVertical = false;
		this.alignment = AreaAlignment.CENTER;
	}

	/**
	 * Getter method for the fillHorizontal.
	 * @return the fillHorizontal
	 */
	public boolean isFillHorizontal() {
		return fillHorizontal;
	}

	/**
	 * Setter method for the fillHorizontal.
	 * @param fillHorizontal the fillHorizontal to set
	 * @return this for chaining
	 */
	public Glue setFillHorizontal(final boolean fillHorizontal) {
		this.fillHorizontal = fillHorizontal;
		requestLayout();
		return this;
	}

	/**
	 * Getter method for the fillVertical.
	 * @return the fillVertical
	 */
	public boolean isFillVertical() {
		return fillVertical;
	}

	/**
	 * Setter method for the fillVertical.
	 * @param fillVertical the fillVertical to set
	 * @return this for chaining
	 */
	public Glue setFillVertical(final boolean fillVertical) {
		this.fillVertical = fillVertical;
		requestLayout();
		return this;
	}

	/**
	 * Getter method for the alignment.
	 * @return the alignment
	 */
	public AreaAlignment getAlignment() {
		return alignment;
	}

	/**
	 * Setter method for the alignment.
	 * @param alignment the alignment to set
	 * @return this for chaining
	 */
	public Glue setAlignment(final AreaAlignment alignment) {
		ParameterUtil.ensureNotNull(alignment, "alignment");
		this.alignment = alignment;
		requestLayout();
		return this;
	}

	@Override
	public void requestSize(int width, int height) {
		final GuiElement wrappedElement = getWrappedElement();
		wrappedElement.requestSize(0, 0);
		width = (fillHorizontal ? Math.max(width, wrappedElement.getWidth()) : wrappedElement.getWidth());
		height = (fillVertical ? Math.max(height, wrappedElement.getHeight()) : wrappedElement.getHeight());
		setSize(width, height);
	}

	@Override
	protected void onAbsolutePositionChanged(final int absoluteX, final int absoluteY) {
		final GuiElement wrappedElement = getWrappedElement();
		final int dx = alignment.getHorizontalAlignment().alignSpan(getWidth(), wrappedElement.getWidth());
		final int dy = alignment.getVerticalAlignment().alignSpan(getHeight(), wrappedElement.getHeight());
		wrappedElement.setAbsolutePosition(absoluteX + dx, absoluteY + dy);
	}

}
