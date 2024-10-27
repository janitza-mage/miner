/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine.gui.control;

import name.martingeisse.miner.client.engine.gui.GuiElement;
import name.martingeisse.miner.client.engine.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.engine.gui.element.collection.OverlayStack;
import name.martingeisse.miner.client.engine.gui.element.fill.FillColor;
import name.martingeisse.miner.client.engine.gui.element.fill.NullElement;
import name.martingeisse.miner.client.engine.gui.util.AreaAlignment;
import name.martingeisse.miner.client.engine.gui.util.Color;

/**
 * This element shows a main element over a background filler, and optionally a popup element that can be
 * added/removed/exchanged at runtime.
 * <p>
 * Pages try to catch all exceptions that occur during event handling in the enclosed elements. These exceptions are
 * passed to {@link #onException(Throwable)}. The default behavior is to catch and log the exceptions.
 * <p>
 * To support all this, subclasses that want to override {@link #handleLogicFrame(GuiLogicFrameContext)} must override
 * {@link #handlePageLogic(GuiLogicFrameContext)} instead.
 * <p>
 * Pages call the helper method {@link #onAttach()} once (and only once) when the GUI becomes available. This can be
 * used, for example, to set the initial input focus.
 * <p>
 * Note that setting any of the sub-elements, such as the popup element, must typically be done in a follow-up logic
 * action to avoid element propagation to reach the new element immediately.
 */
public class Page extends Control {

	private static final Color DARK_OVERLAY = new Color(0, 0, 0, 192);

	private boolean attached = false;

	/**
	 * Constructor.
	 *
	 * @param backgroundElement the background element, or null if none
	 * @param mainElement the main element
	 */
	protected final void initializePage(final GuiElement backgroundElement, final GuiElement mainElement) {
		final OverlayStack stack = new OverlayStack();
		stack.setAlignment(AreaAlignment.CENTER);
		stack.addElement(backgroundElement == null ? NullElement.instance : backgroundElement);
		stack.addElement(mainElement);
		stack.addElement(new FillColor(Color.TRANSPARENT));
		setControlRootElement(stack);
	}

	public final GuiElement getBackgroundElement() {
		return getStack().getWrappedElements().getFirst();
	}

	public final void setBackgroundElement(final GuiElement popupElement) {
		getStack().replaceElement(0, popupElement == null ? NullElement.instance : popupElement);
	}

	public final GuiElement getMainElement() {
		return getStack().getWrappedElements().get(1);
	}

	public final void setMainElement(final GuiElement mainElement) {
		getStack().replaceElement(1, mainElement == null ? NullElement.instance : mainElement);
	}

	/**
	 * Obtains the popup element, if any.
	 * @return the popup element, or null if none
	 */
	public final GuiElement getPopupElement() {
		OverlayStack stack = getStack();
		return (stack.getWrappedElements().size() > 3 ? stack.getWrappedElements().get(3) : null);
	}

	/**
	 * Sets the popup element.
	 *
	 * @param newPopupElement the popup element to use, or null for none
	 */
	public final void setPopupElement(final GuiElement newPopupElement) {
		getGui().addFollowupLogicAction(context -> {
			GuiElement oldPopupElement = getPopupElement();
			if (oldPopupElement == null) {
				if (newPopupElement == null) {
					// nothing to do
				} else {
					getStack().addElement(newPopupElement);
				}
			} else {
				if (newPopupElement == null) {
					getStack().removeElement(3);
				} else {
					getStack().replaceElement(3, newPopupElement);
				}
			}
			FillColor fillColor = (FillColor) getStack().getWrappedElements().get(2);
			fillColor.setColor(newPopupElement == null ? Color.TRANSPARENT : DARK_OVERLAY);
		});
	}

	private OverlayStack getStack() {
		return (OverlayStack) getControlRootElement();
	}

	@Override
	public final void handleLogicFrame(GuiLogicFrameContext context) {
		try {
			if (!attached && getGuiOrNull() != null) {
				attached = true;
				onAttach();
			}
			getBackgroundElement().handleLogicFrame(context);
			GuiElement popupElement = getPopupElement();
			if (popupElement == null) {
				handlePageLogic(context);
				getMainElement().handleLogicFrame(context);
			} else {
				popupElement.handleLogicFrame(context);
			}
		} catch (Throwable t) {
			onException(t);
		}
	}

	/**
	 * Implements logic for the page itself. This can implement hotkeys, for example.
	 * <p>
	 * When overriding this method, make sure to call
	 * <pre>{@code
	 *     super.handlePageLogic(context);
	 * }</pre>
	 * and not
	 * <pre>{@code
	 *     super.handlePageLogic(context);
	 * }</pre>
	 * as that would create an infinite loop.
	 * <p>
	 * The default implementation only calls <code>super.handlePageLogic(context)</code>.
	 * <p>
	 * Exceptions that occur in this method are passed to {@link #onException(Throwable)} as usual.
	 */
	protected void handlePageLogic(GuiLogicFrameContext context) {
	}

	/**
	 * This method gets invoked when one of the elements inside the page throw an exception.
	 */
	protected void onException(Throwable t) {
		System.err.println("exception during GUI event handling: " + t);
	}

	/**
	 * This method gets fired once when the GUI is available to this page.
	 */
	protected void onAttach() {
	}

}
