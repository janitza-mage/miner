/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.gui;

import name.martingeisse.miner.client.ingame.IngameHandler;
import name.martingeisse.miner.client.ingame.logic.Inventory;
import name.martingeisse.miner.client.ingame.logic.Item;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.control.Button;
import name.martingeisse.miner.client.util.gui.control.ListView;
import name.martingeisse.miner.client.util.gui.element.Margin;
import name.martingeisse.miner.client.util.gui.element.Spacer;
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
		ListView<Item> itemListView = new ListView<Item>(Inventory.INSTANCE::getItems) {
			@Override
			protected GuiElement createGuiElement(Item dataElement) {
				Button button = new GameGuiButton(dataElement.getName()) {
					@Override
					protected void onClick() {
					}
				};
				return new Margin(button, 3 * Gui.MINIGRID, 0);
			}
		};
		menu.addElement(itemListView);

		menu.addElement(new Spacer(0, 3 * Gui.GRID));
		menu.addElement(new GameGuiButton("Resume Game") {
			@Override
			protected void onClick() {
				IngameHandler.closeGui();
			}
		});

		initializePage(null, new Margin(menu, 30 * Gui.GRID, 30 * Gui.GRID));
	}

}
