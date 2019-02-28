/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.gui;

import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.ingame.logic.Inventory;
import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.control.Button;
import name.martingeisse.miner.client.util.gui.control.ListView;
import name.martingeisse.miner.client.util.gui.element.*;
import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.logic.CraftingFormula;
import name.martingeisse.miner.common.network.c2s.ApplyFormula;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The "crafting" menu page.
 */
public class CraftingPage extends AbstractGameGuiPage implements InventoryDependentPage {

	private final ListView<Pair<CraftingFormula, Integer>> formulaListView;

	/**
	 * Constructor.
	 */
	public CraftingPage(CubeType craftingStation) {
		final VerticalLayout menu = new VerticalLayout();

		formulaListView = new ListView<Pair<CraftingFormula, Integer>>(() -> Inventory.INSTANCE.getApplicableFormulasFor(craftingStation)) {
			@Override
			protected GuiElement createGuiElement(Pair<CraftingFormula, Integer> dataElement) {
				String text = dataElement.getLeft().name() + " (" + dataElement.getRight() + ")";
				Button button = new GameGuiButton(text) {

					@Override
					protected void onClick() {
						ClientEndpoint.INSTANCE.send(new ApplyFormula(dataElement.getLeft()));
					}

				};
				return new Margin(button, 3 * Gui.MINIGRID, 0);
			}
		};
		menu.addElement(new Sizer(new ScrollContainer(new Margin(formulaListView, 2 * Gui.MINIGRID)), -1, 50 * Gui.GRID));

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
		formulaListView.update();
	}

}
