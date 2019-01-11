/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.startmenu;

import name.martingeisse.miner.common.Faction;
import name.martingeisse.miner.client.gui.Gui;
import name.martingeisse.miner.client.gui.element.Spacer;
import name.martingeisse.miner.client.gui.element.VerticalLayout;

/**
 * The "choose your faction" menu page.
 */
public class ChooseFactionPage extends AbstractStartmenuPage {

	/**
	 * the selectedFaction
	 */
	public static Faction selectedFaction;
	
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
					selectedFaction = faction;
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
