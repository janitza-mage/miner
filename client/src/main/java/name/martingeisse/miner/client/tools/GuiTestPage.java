/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.tools;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.control.Page;
import name.martingeisse.miner.client.util.gui.element.*;
import name.martingeisse.miner.client.util.gui.element.fill.FillColor;
import name.martingeisse.miner.client.util.gui.element.text.TextLine;
import name.martingeisse.miner.client.util.gui.util.Color;

/**
 *
 */
public class GuiTestPage extends Page {

	public static final Color OFF_WHITE = new Color(240, 240, 240);
	public static final Color OFF_BLACK = new Color(40, 40, 40);

	/**
	 * Constructor.
	 */
	public GuiTestPage() {

		/*
		TextLine textLine = new TextLine();
		textLine.setText("Hello World!");
		textLine.setColor(OFF_BLACK);

		Box box = new Box(textLine);
		box.setBorderColor(Color.RED);

		box.getMargin().set(Gui.GRID * 10);
		box.getPadding().set(Gui.GRID * 5);
		box.getBorder().set(Gui.GRID * 2);

		// box.getMargin().setRight(Gui.GRID * 10);

		ThinBorder mainElement = new ThinBorder(box);
		mainElement.setColor(new Color(128, 128, 128));
		*/

		VerticalLayout content = new VerticalLayout();
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));
		content.addElement(textLine("ewogihjewoihweiouhjg"));
		content.addElement(textLine("gregregerg"));
		content.addElement(textLine("hreehrhreherhre"));

		ScrollContainer mainElement = new ScrollContainer();
		mainElement.setWrappedElement(content);

		initializePage(new FillColor(OFF_WHITE), new Margin(new ThinBorder(mainElement), 40 * Gui.GRID, 40 * Gui.GRID));
	}

	private static TextLine textLine(String text) {
		return new TextLine().setText(text).setColor(OFF_BLACK);
	}

}
