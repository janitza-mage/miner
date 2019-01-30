/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.tools;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.control.Page;
import name.martingeisse.miner.client.util.gui.element.FillColor;
import name.martingeisse.miner.client.util.gui.element.Margin;
import name.martingeisse.miner.client.util.gui.element.TextLine;
import name.martingeisse.miner.client.util.gui.element.ThinBorder;
import name.martingeisse.miner.client.util.gui.util.Color;

/**
 *
 */
public class GuiTestPage extends Page {

	/**
	 * Constructor.
	 */
	public GuiTestPage() {

		TextLine box = new TextLine();
		box.setText("Hello World!");

		ThinBorder mainElement = new ThinBorder(box);
		mainElement.setColor(new Color(128, 128, 128));

		initializePage(new FillColor(Color.BLACK), new Margin(mainElement, 30 * Gui.GRID, 30 * Gui.GRID));
	}

}
