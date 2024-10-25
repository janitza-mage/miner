/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.wrapper;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.util.gui.element.fill.NullElement;
import name.martingeisse.miner.common.util.contract.ParameterUtil;

/**
 * Base class for a GUI element that wraps a single other element.
 * <p>
 * The base class stores a pre/post pair of cached work units for this which get invalidated on position and size
 * changes; subclasses should also invalidate it when needed due to other changes.
 */
public abstract class AbstractWrapperElement extends GuiElement {

	private GuiElement wrappedElement = NullElement.instance;
	private GlWorkUnit cachedPreWorkUnit;
	private GlWorkUnit cachedPostWorkUnit;

	public AbstractWrapperElement() {
		this(NullElement.instance);
	}

	public AbstractWrapperElement(GuiElement wrappedElement) {
		setWrappedElement(wrappedElement);
	}

	/**
	 * Getter method for the wrappedElement.
	 * @return the wrappedElement
	 */
	public final GuiElement getWrappedElement() {
		return wrappedElement;
	}

	// there is no method to invalidate only one of the cached work units because setWrappedElement() would not know
	// whether to use it
	protected final void invalidateCachedWorkUnits() {
		cachedPreWorkUnit = null;
		cachedPostWorkUnit = null;
	}

	protected GlWorkUnit createPreWorkUnit() {
		return GlWorkUnit.NOP_WORK_UNIT;
	}

	protected GlWorkUnit createPostWorkUnit() {
		return GlWorkUnit.NOP_WORK_UNIT;
	}

	/**
	 * Setter method for the wrappedElement.
	 * @param wrappedElement the wrappedElement to set
	 * @return this for chaining
	 */
	public AbstractWrapperElement setWrappedElement(GuiElement wrappedElement) {
		ParameterUtil.ensureNotNull(wrappedElement, "wrappedElement");

		this.wrappedElement.notifyRemovedFromParent();
		this.wrappedElement = wrappedElement;
		this.wrappedElement.notifyAddedToParent(this);
		requestLayout();
		invalidateCachedWorkUnits();
		return this;
	}

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
		wrappedElement.handleLogicFrame(context);
	}

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
		if (cachedPreWorkUnit == null) {
			cachedPreWorkUnit = createPreWorkUnit();
		}
		if (cachedPostWorkUnit == null) {
			cachedPostWorkUnit = createPostWorkUnit();
		}
		context.schedule(cachedPreWorkUnit);
		wrappedElement.handleGraphicsFrame(context);
		context.schedule(cachedPostWorkUnit);
	}

	@Override
	public ImmutableList<GuiElement> getChildren() {
		return ImmutableList.of(wrappedElement);
	}

}
