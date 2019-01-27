/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.gui;

import name.martingeisse.miner.client.ingame.IngameHandler;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.element.Margin;
import name.martingeisse.miner.client.util.gui.element.VerticalLayout;

/**
 * The "login" menu page.
 */
public class InventoryPage extends AbstractGameGuiPage {

	/**
	 * Constructor.
	 */
	public InventoryPage() {
		final VerticalLayout menu = new VerticalLayout();
		menu.addElement(new GameGuiButton("Resume Game") {
			@Override
			protected void onClick() {
				IngameHandler.closeGui();
			}
		});
		initializePage(null, new Margin(menu, 30 * Gui.GRID, 30 * Gui.GRID));
	}

}
