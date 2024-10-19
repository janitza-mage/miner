/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.common.util.contract.ParameterUtil;

/**
 * The root class for all GUI elements.
 * <p>
 * Each element belongs to a {@link Gui}. An element has a parent unless it is the root element of that GUI.
 * <p>
 * <h1>GUI hierarchy</h1>
 * An element gets added to its parent using a specific method of that parent. Different kinds of parents have different
 * methods for that, depending on things such as whether they can handle zero, one, or multiple children. The new parent
 * informs its new child about the change by calling {@link #notifyAddedToParent(GuiElement)} -- each kind of parent
 * must implement that call.
 * <p>
 * <h1>Layout</h1>
 * The size and position of elements is determined by the following steps:
 * <ul>
 *     <li>ask elements to determine their sizes, from root to leaves</li>
 *     <li>determine absolute position for all elements, from root to leaves</li>
 * </ul>
 * <p>
 * The first step starts with the total GUI size (usually the screen size) and invokes {@link #requestSize(int, int)}
 * from root to leaves, passing the available size for each child element. Each child sets the resulting size using
 * {@link #setSize(int, int)}. Children that can expand to fill the available size should do so; children with an
 * intrinsic size should use that, even if it exceeds the available size. If an element cannot decide the
 * available size for a child, it passes 0, expecting a child with an intrinsic size to ignore it.
 * <p>
 * In the second step, each element (from root to leaves) gets told its absolute position and stores it, then
 * determines the resulting absolute position of all children in {@link #onAbsolutePositionChanged(int, int)},
 * possibly using the size of the children to arrange them. The relative position is not stored to reduce the amount
 * of state to keep in sync.
 * <p>
 * The {@link #requestLayout()} method should be called by any element whose state changes in such a way that
 * recomputing the layout is necessary, such as adding children or changing any property that affects the element's
 * size.
 */
public abstract class GuiElement {

	private Gui gui;
	private GuiElement parent;
	private int absoluteX;
	private int absoluteY;
	private int width;
	private int height;

	public GuiElement() {
	}

	// region GUI hierarchy

	final void notifyUsedAsRootElement(Gui gui) {
		ParameterUtil.ensureNotNull(gui, "gui");

		this.gui = gui;
		this.parent = null;
	}

	/**
	 * This method should only be used by the old parent to inform the child about it.
	 */
	public final void notifyRemovedFromParent() {
		this.parent = null;
		this.gui = null;
	}

	/**
	 * This method should only be used by the new parent to inform the child about it.
	 */
	public final void notifyAddedToParent(GuiElement parent) {
		ParameterUtil.ensureNotNull(parent, "parent");

		this.parent = parent;
		this.gui = null;
	}

	public final GuiElement getParent() {
		return parent;
	}

	public abstract ImmutableList<GuiElement> getChildren();

	/**
	 * Returns null if not added to a gui yet.
	 */
	public final Gui getGuiOrNull() {
		if (gui == null && parent != null) {
			gui = parent.getGuiOrNull();
		}
		return gui;
	}

	/**
	 * Throws an exception if not added to a gui yet.
	 */
	public final Gui getGui() {
		if (gui == null) {
			if (parent == null) {
				throw new IllegalStateException("This element has not been added to a parent element yet.");
			}
			gui = parent.getGui();
			if (gui == null) {
				throw new IllegalStateException("This element has not been connected to a GUI yet.");
			}
		}
		return gui;
	}

	// endregion

	// region layout

	/**
	 * Requests a re-layout from the GUI. This method should be called when some property of this element changes
	 * that affects the layout. The GUI will then re-layout this element and all its descendants.
	 * <p>
	 * This method is a shortcut for calling {@link Gui#requestLayout()} on the GUI this element is part of.
	 * If no GUI is set, this method does nothing. This is OK because as soon as this element gets added to a GUI,
	 * the new parent element will request a re-layout.
	 */
	protected final void requestLayout() {
		if (gui != null) {
			gui.requestLayout();
		}
	}

	/**
	 * This method should be called by the parent element during layout. It attempts to set the size of this object
	 * based on the size the containing element has determined for this one. It depends on the actual element what
	 * size is actually set.
	 */
	public abstract void requestSize(int width, int height);

	/**
	 * This method should be called by the {@link #requestSize(int, int)} implementation of this element to set the
	 * effective size.
	 */
	protected final void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	/**
	 * This method should be called by the parent element during layout. It recursively sets the absolute position of
	 * this element and all its descendants, based on the actual size that was previously set using
	 * {@link #requestSize(int, int)} and {@link #setSize(int, int)}.
	 */
	public final void setAbsolutePosition(int absoluteX, int absoluteY) {
		this.absoluteX = absoluteX;
		this.absoluteY = absoluteY;
		onAbsolutePositionChanged(absoluteX, absoluteY);
	}

	/**
	 * This method must be implemented by elements that need to react to changes in their absolute position, e.g. to
	 * invalidate cached work units.
	 * <p>
	 * It must also be implemented by elements with children to call {@link #setAbsolutePosition(int, int)} on all
	 * children. The implementation decides how to compute the absolute position of the children from its own absolute
	 * position and size.
	 */
	protected abstract void onAbsolutePositionChanged(int absoluteX, int absoluteY);

	public final int getAbsoluteX() {
		return absoluteX;
	}

	public final int getAbsoluteY() {
		return absoluteY;
	}

	/**
	 * Determines whether the mouse cursor is currently inside this element, based on the current mouse position,
	 * the element's absolute position and size, and whether the mouse is currently obscured at its position.
	 */
	public final boolean isMouseInside(GuiLogicFrameContext context) {
		ParameterUtil.ensureNotNull(context, "context");

		if (context.isMouseObscured()) {
			return false;
		}
		var scale = getGui().getScale();
		double x = scale.pixelsToUnitsDouble(context.getMouseX());
		double y = scale.pixelsToUnitsDouble(context.getMouseY());
		// TODO what are the units sent by GLFW? pixels?
		return (x >= absoluteX && x < absoluteX + getWidth() && y >= absoluteY && y < absoluteY + getHeight());
	}

	// endregion

	// region input, logic and drawing

	public abstract void handleLogicFrame(GuiLogicFrameContext context);
	public abstract void handleGraphicsFrame(GraphicsFrameContext context);

	// endregion


}
