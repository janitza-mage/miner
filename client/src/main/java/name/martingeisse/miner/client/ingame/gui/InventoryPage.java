/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.gui;

import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.ingame.logic.Inventory;
import name.martingeisse.miner.client.ingame.logic.InventorySlot;
import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.control.Button;
import name.martingeisse.miner.client.util.gui.control.ListView;
import name.martingeisse.miner.client.util.gui.element.*;
import name.martingeisse.miner.client.util.gui.element.fill.FillColor;
import name.martingeisse.miner.client.util.gui.util.Color;
import name.martingeisse.miner.client.util.gui.util.GuiDumper;
import name.martingeisse.miner.common.network.c2s.EquipMessage;

/**
 * The "login" menu page.
 *
 * TODO won't handle changes to the inventory well
 */
public class InventoryPage extends AbstractGameGuiPage {

	private final ListView<InventorySlot> slotListView;

	/**
	 * Constructor.
	 */
	public InventoryPage() {
		final VerticalLayout menu = new VerticalLayout();

		slotListView = new ListView<InventorySlot>(Inventory.INSTANCE::getSlots) {
			@Override
			protected GuiElement createGuiElement(InventorySlot dataElement) {
				Button button = new GameGuiButton(dataElement.getName() + " (" + dataElement.getQuantity() + ")") {
					@Override
					protected void onClick() {
						ClientEndpoint.INSTANCE.send(new EquipMessage(dataElement.getId(), false));
					}
				};
				if (dataElement.isEquipped()) {
					button.setBackgroundElement(new FillColor(Color.GREEN));
				}
				return new Margin(button, 3 * Gui.MINIGRID, 0);
			}
		};
		menu.addElement(new Sizer(new ScrollContainer(new Margin(slotListView, 2 * Gui.MINIGRID)), -1, 50 * Gui.GRID));

		menu.addElement(new Spacer(0, 3 * Gui.GRID));
		menu.addElement(new GameGuiButton("Resume Game") {
			@Override
			protected void onClick() {
				Ingame.get().closeGui();
			}
		});

		initializePage(null, new Margin(menu, 30 * Gui.GRID, 30 * Gui.GRID));
	}

	public void refreshInventory() {
		slotListView.update();
	}

}
