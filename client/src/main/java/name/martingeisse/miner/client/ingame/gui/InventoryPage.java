/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
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
import name.martingeisse.miner.common.network.c2s.EquipMessage;
import org.lwjgl.input.Mouse;

/**
 * The "inventory" menu page.
 */
public class InventoryPage extends AbstractGameGuiPage implements InventoryDependentPage {

	private final ListView<InventorySlot> slotListView;

	/**
	 * Constructor.
	 */
	public InventoryPage() {
		final VerticalLayout menu = new VerticalLayout();

		slotListView = new ListView<InventorySlot>(Inventory.INSTANCE::getSlots) {
			@Override
			protected GuiElement createGuiElement(InventorySlot dataElement) {
				Button button = new GameGuiButton(dataElement.getType().getDisplayName() + " (" + dataElement.getQuantity() + ")") {

					@Override
					protected void onClick() {
						if (Mouse.getEventButton() == 0) {
							ClientEndpoint.INSTANCE.send(new EquipMessage(dataElement.getId(), false));
							// TODO mark the item equipped locally to hide the network latency. Uncomment this as soon
							// as I'm sure that server-side equipping works flawlessly.
							// dataElement.setEquipped(true);
							// otherDataElements.setEquipped(false);
							// setBackgroundElement(new FillColor(Color.GREEN));
						} else if (Mouse.getEventButton() == 1) {
							ClientEndpoint.INSTANCE.send(new EquipMessage(dataElement.getId(), true));
							// TODO same as above, locally unequip
						}
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

	@Override
	public void onInventoryChanged() {
		slotListView.update();
	}

}
