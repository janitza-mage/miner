/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.startmenu;

import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.control.MessageBox;
import name.martingeisse.miner.client.util.gui.element.*;
import name.martingeisse.miner.client.util.gui.element.fill.FillColor;
import name.martingeisse.miner.client.util.gui.element.text.TextParagraph;
import name.martingeisse.miner.client.util.gui.util.Color;
import name.martingeisse.miner.client.util.lwjgl.MouseUtil;
import name.martingeisse.miner.common.network.c2s.request.DeletePlayerRequest;
import name.martingeisse.miner.common.network.s2c.response.OkayResponse;

/**
 * The "character details" menu page.
 */
public class PlayerDetailsPage extends AbstractStartmenuPage {

	/**
	 * Constructor.
	 */
	public PlayerDetailsPage() {

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
							DeletePlayerRequest request = new DeletePlayerRequest(StartmenuState.INSTANCE.getSelectedPlayer().getId());
							StartmenuNetworkClient.INSTANCE.requestAndWait(request, OkayResponse.class);
							getGui().setRootElement(new ChooseCharacterPage());
						}
					}

					;
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
		verticalLayout.addElement(new TextParagraph().setText("--- " + StartmenuState.INSTANCE.getSelectedPlayer().getName() + " ---"));
		verticalLayout.addElement(new Spacer(Gui.GRID));
		verticalLayout.addElement(new TextParagraph().setText("Faction: " + StartmenuState.INSTANCE.getSelectedPlayer().getFaction().getDisplayName()));
		verticalLayout.addElement(new TextParagraph().setText("Coins: " + StartmenuState.INSTANCE.getSelectedPlayer().getCoins()));
		OverlayStack stack = new OverlayStack();
		stack.addElement(new FillColor(new Color(128, 128, 128, 255)));
		stack.addElement(new Margin(verticalLayout, Gui.GRID));
		return new ThinBorder(stack).setColor(new Color(192, 192, 192, 255));
	}

	/**
	 *
	 */
	private void play() {
		// Don't send ResumePlayer now, but start the client-side ingame state first. We need to switch to the ingame
		// message router before sending ResumePlayer to make sure we don't lose the response due to race conditions.
		getGui().addFollowupOpenglAction(() -> {
			try {
				Ingame.create();
				MouseUtil.grab();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

}
