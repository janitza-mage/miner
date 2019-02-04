/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.startmenu;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.element.Spacer;
import name.martingeisse.miner.client.util.gui.element.VerticalLayout;
import name.martingeisse.miner.common.Faction;
import name.martingeisse.miner.common.network.c2s.request.CreatePlayerRequest;
import name.martingeisse.miner.common.network.s2c.response.CreatePlayerResponse;

/**
 * The "choose your name" menu page.
 */
public class ChooseNamePage extends AbstractStartmenuPage {
	
	/**
	 * the username
	 */
	private final LabeledTextField name;

	/**
	 * Constructor.
	 */
	public ChooseNamePage() {
		final VerticalLayout menu = new VerticalLayout();
		this.name = new LabeledTextField("Name");
		menu.addElement(name);
		menu.addElement(new Spacer(2 * Gui.GRID));
		menu.addElement(new StartmenuButton("Create Character") {
			@Override
			protected void onClick() {
				createPlayer();
			}
		});
		menu.addElement(new Spacer(2 * Gui.GRID));
		menu.addElement(new StartmenuButton("Back") {
			@Override
			protected void onClick() {
				getGui().setRootElement(new ChooseFactionPage());
			}
		});
		initializeStartmenuPage(menu);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.control.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		getGui().setFocus(name.getTextField());
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.miner.startmenu.AbstractStartmenuPage#onEnterPressed()
	 */
	@Override
	protected void onEnterPressed() {
		createPlayer();
	}

	/**
	 * 
	 */
	private void createPlayer() {
		Faction faction = ChooseFactionPage.selectedFaction;
		String name = ChooseNamePage.this.name.getTextField().getValue();
		CreatePlayerRequest request = new CreatePlayerRequest(faction, name);
		CreatePlayerResponse response = StartmenuNetworkClient.INSTANCE.requestAndWait(request, CreatePlayerResponse.class);
		getGui().setRootElement(new PlayerDetailsPage(response.getPlayerData().getId()));
	}
	
}
