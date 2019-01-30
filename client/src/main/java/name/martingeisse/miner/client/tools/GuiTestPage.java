/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.tools;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.control.Page;
import name.martingeisse.miner.client.util.gui.element.*;
import name.martingeisse.miner.client.util.gui.util.Color;

/**
 *
 */
public class GuiTestPage extends Page {

	/**
	 * Constructor.
	 */
	public GuiTestPage() {

		TextLine textLine = new TextLine();
		textLine.setText("Hello World!");

		Box box = new Box(textLine);
		box.setBorderColor(Color.RED);

		box.getMargin().set(Gui.GRID * 10);
		box.getPadding().set(Gui.GRID * 5);
		box.getBorder().set(Gui.GRID * 2);

		// box.getMargin().setRight(Gui.GRID * 10);

		ThinBorder mainElement = new ThinBorder(box);
		mainElement.setColor(new Color(128, 128, 128));

		initializePage(new FillColor(Color.BLACK), new Margin(mainElement, 30 * Gui.GRID, 30 * Gui.GRID));
	}

}
