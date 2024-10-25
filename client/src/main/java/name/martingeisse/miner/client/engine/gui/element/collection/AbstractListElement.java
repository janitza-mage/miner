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
 * This element allows to obtain the internal list, but note that if you modify that list directly, you must also
 * request a re-layout manually.
 */
public abstract class AbstractListElement extends GuiElement {

	private final List<GuiElement> wrappedElements;

	public AbstractListElement() {
		this.wrappedElements = new ArrayList<>();
	}

	public AbstractListElement(GuiElement... elements) {
		this.wrappedElements = new ArrayList<>();
		for (GuiElement element : elements) {
			addElement(element);
		}
	}

	public final List<GuiElement> getWrappedElements() {
		return wrappedElements;
	}

	/**
	 * Adds the specified element. This method requests a re-layout automatically.
	 * @return this
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
	 * @return this
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
	 * @return this
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
		TODO notify
		wrappedElements.clear();
		requestLayout();
	}

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
		for (GuiElement element : getWrappedElements()) {
			element.handleLogicFrame(context);
		}
	}

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
		for (GuiElement element : getWrappedElements()) {
			element.handleGraphicsFrame(context);
		}
	}

	@Override
	public ImmutableList<GuiElement> getChildren() {
		return ImmutableList.copyOf(wrappedElements);
	}

}
