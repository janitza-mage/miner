/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.gui;

import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.util.frame.BreakFrameLoopException;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.element.Margin;
import name.martingeisse.miner.client.util.gui.element.Spacer;
import name.martingeisse.miner.client.util.gui.element.VerticalLayout;

/**
 * The "login" menu page.
 */
public class MainMenuPage extends AbstractGameGuiPage {

	/**
	 * Constructor.
	 */
	public MainMenuPage() {
		final VerticalLayout menu = new VerticalLayout();
		menu.addElement(new GameGuiButton("Resume Game") {
			@Override
			protected void onClick() {
				Ingame.get().closeGui();
			}
		});
		menu.addElement(new Spacer(2 * Gui.GRID));
		menu.addElement(new GameGuiButton("Quit") {
			@Override
			protected void onClick() {
				getGui().addFollowupLogicAction(() -> {
					throw new BreakFrameLoopException();
				});
			}
		});
		initializePage(null, new Margin(menu, 30 * Gui.GRID, 30 * Gui.GRID));
	}

}
