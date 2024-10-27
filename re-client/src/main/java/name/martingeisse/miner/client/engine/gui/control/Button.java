/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine.gui.control;

import name.martingeisse.miner.client.engine.gui.GuiElement;
import name.martingeisse.miner.client.engine.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.engine.gui.element.atom.TextLine;
import name.martingeisse.miner.client.engine.gui.element.collection.OverlayStack;
import name.martingeisse.miner.client.engine.gui.element.fill.NullElement;
import name.martingeisse.miner.client.engine.gui.element.fill.PulseFillColor;
import name.martingeisse.miner.client.engine.gui.element.wrapper.Border;
import name.martingeisse.miner.client.engine.gui.element.wrapper.Margin;
import name.martingeisse.miner.client.engine.gui.element.wrapper.MouseOverWrapper;
import name.martingeisse.miner.client.engine.gui.util.Color;
import name.martingeisse.miner.client.engine.gui.util.PulseFunction;
import org.lwjgl.glfw.GLFW;

/**
 * A button that can be clicked by the user.
 */
public abstract class Button extends Control {

	private final TextLine textLine;
	private final Margin margin;
	private final OverlayStack stack;
	private final Border border;

	public Button() {
		textLine = new TextLine();
		margin = new Margin(textLine, 1);
		stack = new OverlayStack();
		stack.addElement(NullElement.instance);
		stack.addElement(margin);
		border = new Border(stack);
		setControlRootElement(border);
	}

	public Button(String text) {
		this();
		setText(text);
	}

	public void setText(String text) {
		textLine.setText(text);
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
	public Border getBorder() {
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
	public Button setBackgroundElement(GuiElement backgroundElement) {
		stack.replaceElement(0, backgroundElement);
		return this;
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
