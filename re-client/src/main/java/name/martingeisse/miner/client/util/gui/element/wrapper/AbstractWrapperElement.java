/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.wrapper;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;

/**
 * Base class for a GUI element that wraps a single other element.
 */
public abstract class AbstractWrapperElement extends GuiElement {

	/**
	 * the wrappedElement
	 */
	private volatile GuiElement wrappedElement;

	/**
	 * Constructor.
	 */
	public AbstractWrapperElement() {
	}

	/**
	 * Constructor.
	 * @param wrappedElement the wrapped element
	 */
	public AbstractWrapperElement(GuiElement wrappedElement) {
		setWrappedElement(wrappedElement);
	}

	/**
	 * Getter method for the wrappedElement.
	 * @return the wrappedElement
	 */
	public GuiElement getWrappedElement() {
		return wrappedElement;
	}

	/**
	 * Setter method for the wrappedElement.
	 * @param wrappedElement the wrappedElement to set
	 * @return this for chaining
	 */
	public AbstractWrapperElement setWrappedElement(GuiElement wrappedElement) {
		this.wrappedElement = wrappedElement;
		requireWrappedElement().notifyAddedToParent(this);
		requestLayout();
		return this;
	}

	@Override
	public void handleInput(GuiLogicFrameContext context) {
		requireWrappedElement().handleInput(context);
	}

	@Override
	public void draw(GraphicsFrameContext context) {
		requireWrappedElement().draw(context);
	}

	/**
	 * Throws an {@link IllegalStateException} if no wrapped element is currently set.
	 */
	public final GuiElement requireWrappedElement() {
		var wrappedElement = this.wrappedElement;
		if (wrappedElement == null) {
			throw new IllegalArgumentException(getClass().getName() + " has no wrapped element set");
		}
		return wrappedElement;
	}

	@Override
	public ImmutableList<GuiElement> getChildren() {
		return ImmutableList.of(wrappedElement);
	}

}
