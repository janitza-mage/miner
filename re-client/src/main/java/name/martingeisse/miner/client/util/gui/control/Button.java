/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.control;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.util.gui.element.collection.OverlayStack;
import name.martingeisse.miner.client.util.gui.element.fill.NullElement;
import name.martingeisse.miner.client.util.gui.element.fill.PulseFillColor;
import name.martingeisse.miner.client.util.gui.element.atom.TextLine;
import name.martingeisse.miner.client.util.gui.element.wrapper.Margin;
import name.martingeisse.miner.client.util.gui.element.wrapper.MouseOverWrapper;
import name.martingeisse.miner.client.util.gui.element.wrapper.ThinBorder;
import name.martingeisse.miner.client.util.gui.util.Color;
import name.martingeisse.miner.client.util.gui.util.PulseFunction;
import org.lwjgl.glfw.GLFW;

/**
 * A button that can be clicked by the user.
 */
public abstract class Button extends Control {

	/**
	 * the textLine
	 */
	private final TextLine textLine;

	/**
	 * the margin
	 */
	private final Margin margin;

	/**
	 * the stack
	 */
	private final OverlayStack stack;

	/**
	 * the border
	 */
	private final ThinBorder border;

	/**
	 * Constructor.
	 */
	public Button() {
		textLine = new TextLine();
		margin = new Margin(textLine, Gui.GRID);
		stack = new OverlayStack();
		stack.addElement(NullElement.instance);
		stack.addElement(margin);
		border = new ThinBorder(stack);
		setControlRootElement(border);
	}

	/**
	 * Getter method for the textLine.
	 * @return the textLine
	 */
	public TextLine getTextLine() {
		return textLine;
	}

	/**
	 * Getter method for the margin.
	 * @return the margin
	 */
	public Margin getMargin() {
		return margin;
	}

	/**
	 * Getter method for the border.
	 * @return the border
	 */
	public ThinBorder getBorder() {
		return border;
	}

	/**
	 * Getter method for the backgroundElement.
	 * @return the backgroundElement
	 */
	public GuiElement getBackgroundElement() {
		return stack.getWrappedElements().getFirst();
	}

	/**
	 * Setter method for the backgroundElement.
	 * @param backgroundElement the backgroundElement to set
	 */
	public void setBackgroundElement(GuiElement backgroundElement) {
		stack.replaceElement(0, backgroundElement);
	}

	/**
	 * Adds a pulse effect that is visible when the mouse is over the button.
	 * This method doesn't take a pulse amplitude; use the color's alpha
	 * channel for that.
	 *
	 * @param color the pulse color
	 * @return this button for chaining
	 */
	public Button addPulseEffect(Color color) {
		return addPulseEffect(color, PulseFunction.ABSOLUTE_SINE, 2000);
	}

	/**
	 * Adds a pulse effect that is visible when the mouse is over the button.
	 * This method doesn't take a pulse amplitude; use the color's alpha
	 * channel for that.
	 *
	 * @param color the pulse color
	 * @param function the pulse function
	 * @param period the pulse period
	 * @return this button for chaining
	 */
	public Button addPulseEffect(Color color, PulseFunction function, int period) {
		PulseFillColor fillColor = new PulseFillColor().setColor(color).setPulseFunction(function).setPeriod(period);
		stack.addElement(new MouseOverWrapper(fillColor));
		return this;
	}

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
		if (context.isMouseButtonNewlyDown(GLFW.GLFW_MOUSE_BUTTON_LEFT) && isMouseInside(context)) {
			onClick();
		}
	}

	/**
	 * This method gets called when the user clicks on the button.
	 */
	protected abstract void onClick();

}
