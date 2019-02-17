/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.gui;

import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.control.Button;
import name.martingeisse.miner.client.util.gui.control.ListView;
import name.martingeisse.miner.client.util.gui.element.*;
import name.martingeisse.miner.common.logic.CraftingFormula;

/**
 * The "crafting" menu page.
 */
public class CraftingPage extends AbstractGameGuiPage {

	private final ListView<CraftingFormula> formulaListView;

	/**
	 * Constructor.
	 */
	public CraftingPage() {
		final VerticalLayout menu = new VerticalLayout();

		formulaListView = new ListView<CraftingFormula>(() -> CraftingFormula.ALL) {
			@Override
			protected GuiElement createGuiElement(CraftingFormula dataElement) {
				Button button = new GameGuiButton(dataElement.name()) {

					@Override
					protected void onClick() {
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

}
