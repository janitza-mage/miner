/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.wrapper;

import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.util.AreaAlignment;

/**
 * This element requests a specific configurable size from its wrapped element, or the size requested from the outside
 * (per-axis; specify a negative size to use the outside size). If the wrapped element turns out to be bigger, and
 * growing is enabled, this sizer will also grow. Otherwise (smaller wrapped element and/or growing disabled), this
 * sizer will fill the remaining space up to the requested size. The wrapped element can be positioned inside the sizer
 * using an {@link AreaAlignment}.
 * <p>
 * Growing is enabled for both axes by default.
 */
public final class Sizer extends AbstractWrapperElement {

	private volatile int innerWidthRequest;
	private volatile int innerHeightRequest;
	private volatile AreaAlignment alignment;
	private volatile boolean growWidth = true;
	private volatile boolean growHeight = true;

	/**
	 * Constructor using center alignment.
	 *
	 * @param wrappedElement the sized element
	 * @param innerWidth     the width of the inner element
	 * @param innerHeight    the height of the inner element
	 */
	public Sizer(final GuiElement wrappedElement, final int innerWidth, final int innerHeight) {
		this(wrappedElement, innerWidth, innerHeight, AreaAlignment.CENTER);
	}

	/**
	 * Constructor.
	 *
	 * @param wrappedElement     the sized element
	 * @param innerWidthRequest  the width to request from the inner element
	 * @param innerHeightRequest the height to request from the inner element
	 * @param alignment          the alignment of the inner element within the outer one
	 */
	public Sizer(final GuiElement wrappedElement, final int innerWidthRequest, final int innerHeightRequest, final AreaAlignment alignment) {
		super(wrappedElement);
		this.innerWidthRequest = innerWidthRequest;
		this.innerHeightRequest = innerHeightRequest;
		this.alignment = alignment;
	}

	/**
	 * Getter method for the alignment.
	 *
	 * @return the alignment
	 */
	public AreaAlignment getAlignment() {
		return alignment;
	}

	/**
	 * Setter method for the alignment.
	 *
	 * @param alignment the alignment to set
	 * @return this for chaining
	 */
	public Sizer setAlignment(final AreaAlignment alignment) {
		this.alignment = alignment;
		return this;
	}

	public int getInnerWidthRequest() {
		return innerWidthRequest;
	}

	public Sizer setInnerWidthRequest(int innerWidthRequest) {
		this.innerWidthRequest = innerWidthRequest;
		requestLayout();
		return this;
	}

	public int getInnerHeightRequest() {
		return innerHeightRequest;
	}

	public Sizer setInnerHeightRequest(int innerHeightRequest) {
		this.innerHeightRequest = innerHeightRequest;
		requestLayout();
		return this;
	}

	public Sizer setInnerSize(int innerWidthRequest, int innerHeightRequest) {
		this.innerWidthRequest = innerWidthRequest;
		this.innerHeightRequest = innerHeightRequest;
		return this;
	}

	public boolean isGrowWidth() {
		return growWidth;
	}

	public Sizer setGrowWidth(boolean growWidth) {
		this.growWidth = growWidth;
		requestLayout();
		return this;
	}

	public boolean isGrowHeight() {
		return growHeight;
	}

	public Sizer setGrowHeight(boolean growHeight) {
		this.growHeight = growHeight;
		requestLayout();
		return this;
	}

	public Sizer setGrow(boolean grow) {
		this.growWidth = grow;
		this.growHeight = grow;
		requestLayout();
		return this;
	}

	@Override
	public void requestSize(final int width, final int height) {
		requireWrappedElement();
		final GuiElement wrappedElement = getWrappedElement();
		wrappedElement.requestSize(innerWidthRequest < 0 ? width : innerWidthRequest, innerHeightRequest < 0 ? height : innerHeightRequest);
		setSize(
			growWidth ? Math.max(wrappedElement.getWidth(), width) : width,
			growHeight ? Math.max(wrappedElement.getHeight(), height) : height
		);
	}

	@Override
	protected void onAbsolutePositionChanged(final int absoluteX, final int absoluteY) {
		requireWrappedElement();
		final int x = alignment.getHorizontalAlignment().alignSpan(getWidth(), getWrappedElement().getWidth());
		final int y = alignment.getVerticalAlignment().alignSpan(getHeight(), getWrappedElement().getHeight());
		getWrappedElement().setAbsolutePosition(absoluteX + x, absoluteY + y);
	}

}
