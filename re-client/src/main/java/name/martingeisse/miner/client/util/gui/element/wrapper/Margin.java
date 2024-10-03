/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.wrapper;

import name.martingeisse.miner.client.util.gui.GuiElement;

/**
 * This element adds a margin around another element.
 */
public final class Margin extends AbstractWrapperElement {

	private volatile int top;
	private volatile int right;
	private volatile int bottom;
	private volatile int left;

	/**
	 * Constructor.
	 */
	public Margin() {
	}

	/**
	 * Constructor.
	 * @param wrappedElement the wrapped element
	 */
	public Margin(GuiElement wrappedElement) {
		super(wrappedElement);
	}

	/**
	 * Constructor for a uniform margin on all sides.
	 * @param margin the margin
	 */
	public Margin(int margin) {
		this.top = this.right = this.bottom = this.left = margin;
	}

	/**
	 * Constructor for a uniform margin on all sides.
	 * @param wrappedElement the wrapped element
	 * @param margin the margin
	 */
	public Margin(GuiElement wrappedElement, int margin) {
		super(wrappedElement);
		this.top = this.right = this.bottom = this.left = margin;
	}

	/**
	 * Constructor that allows to specify a vertical and a horizontal margin.
	 * @param verticalMargin the vertical margin
	 * @param horizontalMargin the horizontal margin
	 */
	public Margin(int verticalMargin, int horizontalMargin) {
		this.top = this.bottom = verticalMargin;
		this.right = this.left = horizontalMargin;
	}

	/**
	 * Constructor that allows to specify a vertical and a horizontal margin.
	 * @param wrappedElement the wrapped element
	 * @param verticalMargin the vertical margin
	 * @param horizontalMargin the horizontal margin
	 */
	public Margin(GuiElement wrappedElement, int verticalMargin, int horizontalMargin) {
		super(wrappedElement);
		this.top = this.bottom = verticalMargin;
		this.right = this.left = horizontalMargin;
	}

	/**
	 * Constructor that allows to specify top, horizontal and bottom margins.
	 * @param topMargin the top margin
	 * @param horizontalMargin the horizontal margin
	 * @param bottomMargin the bottom margin
	 */
	public Margin(int topMargin, int horizontalMargin, int bottomMargin) {
		this.top = topMargin;
		this.right = this.left = horizontalMargin;
		this.bottom = bottomMargin;
	}

	/**
	 * Constructor that allows to specify top, horizontal and bottom margins.
	 * @param wrappedElement the wrapped element
	 * @param topMargin the top margin
	 * @param horizontalMargin the horizontal margin
	 * @param bottomMargin the bottom margin
	 */
	public Margin(GuiElement wrappedElement, int topMargin, int horizontalMargin, int bottomMargin) {
		super(wrappedElement);
		this.top = topMargin;
		this.right = this.left = horizontalMargin;
		this.bottom = bottomMargin;
	}

	/**
	 * Constructor that allows to specify all four margins individually.
	 * @param top the top margin
	 * @param right the right margin
	 * @param bottom the bottom margin
	 * @param left the left margin
	 */
	public Margin(int top, int right, int bottom, int left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	/**
	 * Constructor that allows to specify all four margins individually.
	 * @param wrappedElement the wrapped element
	 * @param top the top margin
	 * @param right the right margin
	 * @param bottom the bottom margin
	 * @param left the left margin
	 */
	public Margin(GuiElement wrappedElement, int top, int right, int bottom, int left) {
		super(wrappedElement);
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	/**
	 * Getter method for the top.
	 * @return the top
	 */
	public int getTop() {
		return top;
	}

	/**
	 * Setter method for the top.
	 * @param top the top to set
	 * @return this for chaining
	 */
	public Margin setTop(int top) {
		this.top = top;
		return this;
	}

	/**
	 * Getter method for the right.
	 * @return the right
	 */
	public int getRight() {
		return right;
	}

	/**
	 * Setter method for the right.
	 * @param right the right to set
	 * @return this for chaining
	 */
	public Margin setRight(int right) {
		this.right = right;
		return this;
	}

	/**
	 * Getter method for the bottom.
	 * @return the bottom
	 */
	public int getBottom() {
		return bottom;
	}

	/**
	 * Setter method for the bottom.
	 * @param bottom the bottom to set
	 * @return this for chaining
	 */
	public Margin setBottom(int bottom) {
		this.bottom = bottom;
		return this;
	}

	/**
	 * Getter method for the left.
	 * @return the left
	 */
	public int getLeft() {
		return left;
	}

	/**
	 * Setter method for the left.
	 * @param left the left to set
	 * @return this for chaining
	 */
	public Margin setLeft(int left) {
		this.left = left;
		return this;
	}

	@Override
	public void requestSize(int width, int height) {
		requireWrappedElement();
		int remainingWidth = width - left - right;
		int remainingHeight = height - top - bottom;
		GuiElement wrappedElement = requireWrappedElement();
		wrappedElement.requestSize(Math.max(remainingWidth, 0), Math.max(remainingHeight, 0));
		setSize(wrappedElement.getWidth() + left + right, wrappedElement.getHeight() + top + bottom);
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		requireWrappedElement().setAbsolutePosition(absoluteX + left, absoluteY + top);
	}

}
