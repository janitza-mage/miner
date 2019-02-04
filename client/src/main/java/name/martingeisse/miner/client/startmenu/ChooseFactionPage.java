/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.startmenu;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.element.Spacer;
import name.martingeisse.miner.client.util.gui.element.VerticalLayout;
import name.martingeisse.miner.common.Faction;

/**
 * The "choose your faction" menu page.
 */
public class ChooseFactionPage extends AbstractStartmenuPage {

	/**
	 * Constructor.
	 */
	public ChooseFactionPage() {
		final VerticalLayout menu = new VerticalLayout();

		// add faction buttons
		for (final Faction faction : Faction.values()) {
			menu.addElement(new StartmenuButton(faction.getDisplayName()) {
				@Override
				protected void onClick() {
					StartmenuState.INSTANCE.setNewPlayerFaction(faction);
					getGui().setRootElement(new ChooseNamePage());
				}
			});
			menu.addElement(new Spacer(2 * Gui.GRID));
		}

		// build the remaining menu
		menu.addElement(new StartmenuButton("Back") {
			@Override
			protected void onClick() {
				getGui().setRootElement(new ChooseCharacterPage());
			}
		});
		initializeStartmenuPage(menu);

	}

}
