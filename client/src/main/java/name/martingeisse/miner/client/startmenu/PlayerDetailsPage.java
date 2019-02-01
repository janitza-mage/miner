/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.startmenu;

import name.martingeisse.common.javascript.analyze.JsonAnalyzer;
import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.control.MessageBox;
import name.martingeisse.miner.client.util.gui.element.*;
import name.martingeisse.miner.client.util.gui.element.fill.FillColor;
import name.martingeisse.miner.client.util.gui.element.text.TextParagraph;
import name.martingeisse.miner.client.util.gui.util.Color;
import name.martingeisse.miner.client.util.lwjgl.MouseUtil;
import name.martingeisse.miner.common.Faction;

/**
 * The "character details" menu page.
 */
public class PlayerDetailsPage extends AbstractStartmenuPage {

	/**
	 * the playerId
	 */
	private final long playerId;
	
	/**
	 * the faction
	 */
	private final Faction faction;
	
	/**
	 * the name
	 */
	private final String name;

	/**
	 * the coins
	 */
	private final long coins;
	
	/**
	 * Constructor.
	 * @param playerId the player ID
	 */
	public PlayerDetailsPage(long playerId) {
		
		// fetch player data
		this.playerId = playerId;
		JsonAnalyzer json = AccountApiClient.getInstance().fetchPlayerDetails(playerId);
		this.faction = Faction.values()[json.analyzeMapElement("faction").expectInteger()];
		this.name = json.analyzeMapElement("name").expectString();
		this.coins = json.analyzeMapElement("coins").expectLong();
		
		// build the layout
		final VerticalLayout menu = new VerticalLayout();
		menu.addElement(buildInfoBox());
		menu.addElement(new Spacer(2 * Gui.GRID));
		menu.addElement(new Sizer(new StartmenuButton("Play!") {
			@Override
			protected void onClick() {
				play();
			}
		}, -1, 10 * Gui.GRID));
		menu.addElement(new Spacer(2 * Gui.GRID));
		menu.addElement(new StartmenuButton("Delete Character") {
			@Override
			protected void onClick() {
				new MessageBox("Really delete this character?", MessageBox.YES_NO) {
					@Override
					protected void onClose(int buttonIndex) {
						if (buttonIndex == 0) {
							AccountApiClient.getInstance().deletePlayer(PlayerDetailsPage.this.playerId);
							getGui().setRootElement(new ChooseCharacterPage());
						}
					};
				}.show(PlayerDetailsPage.this);
			}
		});
		menu.addElement(new Spacer(2 * Gui.GRID));
		menu.addElement(new Sizer(new StartmenuButton("Back") {
			@Override
			protected void onClick() {
				getGui().setRootElement(new ChooseCharacterPage());
			}
		}, -1, 5 * Gui.GRID));
		initializeStartmenuPage(menu);
		
	}
	
	/**
	 * 
	 */
	private GuiElement buildInfoBox() {
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addElement(new TextParagraph().setText("--- " + name + " ---"));
		verticalLayout.addElement(new Spacer(Gui.GRID));
		verticalLayout.addElement(new TextParagraph().setText("Faction: " + faction.getDisplayName()));
		verticalLayout.addElement(new TextParagraph().setText("Coins: " + coins));
		OverlayStack stack = new OverlayStack();
		stack.addElement(new FillColor(new Color(128, 128, 128, 255)));
		stack.addElement(new Margin(verticalLayout, Gui.GRID));
		return new ThinBorder(stack).setColor(new Color(192, 192, 192, 255));
	}

	/**
	 * 
	 */
	private void play() {
		AccountApiClient.getInstance().accessPlayer(playerId);
		getGui().addFollowupOpenglAction(new Runnable() {
			@Override
			public void run() {
				try {
					Ingame.create();
					MouseUtil.grab();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

}
