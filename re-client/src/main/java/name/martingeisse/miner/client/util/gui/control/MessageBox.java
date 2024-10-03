/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.control;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.element.collection.HorizontalLayout;
import name.martingeisse.miner.client.util.gui.element.collection.OverlayStack;
import name.martingeisse.miner.client.util.gui.element.collection.VerticalLayout;
import name.martingeisse.miner.client.util.gui.element.fill.FillColor;
import name.martingeisse.miner.client.util.gui.element.atom.TextParagraph;
import name.martingeisse.miner.client.util.gui.element.wrapper.Glue;
import name.martingeisse.miner.client.util.gui.element.wrapper.Margin;
import name.martingeisse.miner.client.util.gui.element.wrapper.Sizer;
import name.martingeisse.miner.client.util.gui.element.wrapper.ThinBorder;
import name.martingeisse.miner.client.util.gui.util.Color;
import name.martingeisse.miner.client.util.gui.util.HorizontalAlignment;

/**
 * A box with a text message and an OK button. This control can be used
 * as a popup element on a {@link Page}. When the user clicks the OK
 * button, the message box removes itself from any {@link Page} ancestor
 * control again and a subclass method gets invoked for custom logic.
 */
public class MessageBox extends Control {

	/**
	 * Button labels for "OK".
	 */
	public static final String[] OK = {"OK"};

	/**
	 * Button labels for "OK" / "Cancel".
	 */
	public static final String[] OK_CANCEL = {"OK", "Cancel"};

	/**
	 * Button labels for "Yes" / "No".
	 */
	public static final String[] YES_NO = {"Yes", "No"};

	/**
	 * Button labels for "Yes" / "No" / "Cancel".
	 */
	public static final String[] YES_NO_CANCEL = {"Yes", "No", "Cancel"};

	/**
	 * Constructor.
	 *
	 * The box will have buttons with the specified labels. The button
	 * used to close the box gets passed to the subclass's callback
	 * method. If no button labels are specified, a single button labeled
	 * "OK" is used.
	 *
	 * @param message the message to show
	 * @param buttonLabels the labels for the buttons that close the message box
	 */
	public MessageBox(final String message, String... buttonLabels) {
		if (buttonLabels == null || buttonLabels.length == 0) {
			buttonLabels = OK;
		}

		VerticalLayout verticalLayout = new VerticalLayout().setAlignment(HorizontalAlignment.RIGHT);
		TextParagraph textParagraph = new TextParagraph().setText(message);
		Margin textMargin = new Margin(new Sizer(textParagraph, 50 * Gui.GRID, 30 * Gui.GRID), 2 * Gui.GRID);
		verticalLayout.addElement(textMargin);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		int buttonIndex = 0;
		for (String buttonLabel : buttonLabels) {
			final int thisButtonIndex = buttonIndex;
			Button button = new Button() {
				@Override
				protected void onClick() {
					removeFromPages();
					onClose(thisButtonIndex);
				}
			};
			button.getTextLine().setText(buttonLabel);
			button.setBackgroundElement(new FillColor(Color.BLUE));
			button.addPulseEffect(new Color(255, 255, 255, 64));
			buttonLayout.addElement(new Margin(new Sizer(button, 10 * Gui.GRID, -1), Gui.GRID));
			buttonIndex++;
		}
		verticalLayout.addElement(buttonLayout);

		// add a background box
		OverlayStack stack = new OverlayStack();
		stack.addElement(new FillColor(new Color(128, 128, 128, 255)));
		stack.addElement(verticalLayout);
		ThinBorder boxBorder = new ThinBorder(stack).setColor(new Color(192, 192, 192, 256));
		setControlRootElement(new Glue(boxBorder));

	}

	/**
	 * Shows a message box on the page that contains the specified
	 * element. Such a page must exist, otherwise this method throws
	 * an {@link IllegalArgumentException}. If the element is contained
	 * in nested pages, the message box gets shown in the innermost one.
	 *
	 * @param element an element inside the page
	 */
	public final void show(GuiElement element) {
		while (element != null) {
			if (element instanceof Page) {
				show((Page) element);
				return;
			}
			element = element.getParent();
		}
		throw new IllegalArgumentException("argument element not inside a page");
	}

	/**
	 * Shows a message box on the specified page.
	 *
	 * @param page the page
	 */
	public final void show(Page page) {
		page.setPopupElement(this);
	}

	/**
	 *
	 */
	private void removeFromPages() {
		for (GuiElement element = getParent(); element != null; element = element.getParent()) {
			if (element instanceof Page) {
				Page page = (Page) element;
				if (page.getPopupElement() == this) {
					page.setPopupElement(null);
				}
			}
		}
	}

	/**
	 * This method gets invoked when the user presses one of the close buttons.
	 */
	protected void onClose(int buttonIndex) {
	}

}
