/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.collection;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;
import name.martingeisse.miner.common.util.contract.ParameterUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for a GUI element that wraps a list of other elements.
 * <p>
 * This element allows to obtain the internal list, but note that
 * if you modify that list directly, you must also request a
 * re-layout manually.
 */
public abstract class AbstractListElement extends GuiElement {

	/**
	 * the wrappedElements
	 */
	private final List<GuiElement> wrappedElements;

	/**
	 * Constructor.
	 */
	public AbstractListElement() {
		this.wrappedElements = new ArrayList<>();
	}

	/**
	 * Constructor.
	 * @param elements the elements to add to the list
	 */
	public AbstractListElement(GuiElement... elements) {
		this.wrappedElements = new ArrayList<>();
		for (GuiElement element : elements) {
			addElement(element);
		}
	}

	/**
	 * Getter method for the wrappedElements.
	 * @return the wrappedElements
	 */
	public final List<GuiElement> getWrappedElements() {
		return wrappedElements;
	}

	/**
	 * Adds the specified element. This method requests a re-layout automatically.
	 *
	 * @param element the element to add
	 * @return this for chaining
	 */
	public final AbstractListElement addElement(GuiElement element) {
		ParameterUtil.ensureNotNull(element, "element");
		wrappedElements.add(element);
		element.notifyAddedToParent(this);
		requestLayout();
		return this;
	}

	/**
	 * Replaces the specified element. This method requests a re-layout automatically.
	 * @param index the index of the element to replace
	 * @param newElement the new element
	 * @return this for chaining
	 */
	public final AbstractListElement replaceElement(int index, GuiElement newElement) {
		ParameterUtil.ensureNotNull(newElement, "newElement");
		getWrappedElements().get(index).notifyRemovedFromParent();
		getWrappedElements().set(index, newElement);
		newElement.notifyAddedToParent(this);
		requestLayout();
		return this;
	}

	/**
	 * Removes the specified element. This method requests a re-layout automatically.
	 * @param index the index of the element to remove
	 * @return this for chaining
	 */
	public final AbstractListElement removeElement(int index) {
		getWrappedElements().get(index).notifyRemovedFromParent();
		getWrappedElements().remove(index);
		requestLayout();
		return this;
	}

	/**
	 * Clears the list of wrapped elements. This method requests a re-layout
	 * automatically.
	 */
	public final void clearElements() {
		wrappedElements.clear();
		requestLayout();
	}

	@Override
	public void handleInput(GuiLogicFrameContext context) {
		for (GuiElement element : getWrappedElements()) {
			element.handleInput(context);
		}
	}

	@Override
	public void draw(GraphicsFrameContext context) {
		for (GuiElement element : getWrappedElements()) {
			element.draw(context);
		}
	}

	@Override
	public ImmutableList<GuiElement> getChildren() {
		return ImmutableList.copyOf(wrappedElements);
	}

}
