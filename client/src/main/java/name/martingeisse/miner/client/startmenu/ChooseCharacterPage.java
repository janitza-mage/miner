/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.startmenu;

import name.martingeisse.miner.common.Faction;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.element.Spacer;
import name.martingeisse.miner.client.util.gui.element.VerticalLayout;
import name.martingeisse.miner.common.network.s2c.response.LoginResponse;

/**
 * The "choose your character" menu page.
 */
public class ChooseCharacterPage extends AbstractStartmenuPage {

	/**
	 * Constructor.
	 */
	public ChooseCharacterPage() {
		final VerticalLayout menu = new VerticalLayout();

		// fetch players
		for (final LoginResponse.Element element : StartmenuState.INSTANCE.getPlayers()) {
			menu.addElement(new StartmenuButton(element.getName() + " (" + element.getFaction().getDisplayName() + ")") {
				@Override
				protected void onClick() {
					StartmenuState.INSTANCE.setSelectedPlayer(element);
					getGui().setRootElement(new PlayerDetailsPage());
				}
			});
			menu.addElement(new Spacer(2 * Gui.GRID));
		}

		// build the remaining menu
		menu.addElement(new StartmenuButton("Create Character") {
			@Override
			protected void onClick() {
				getGui().setRootElement(new ChooseFactionPage());
			}
		});
		menu.addElement(new Spacer(2 * Gui.GRID));
		menu.addElement(EXIT_BUTTON);
		initializeStartmenuPage(menu);

	}

}
