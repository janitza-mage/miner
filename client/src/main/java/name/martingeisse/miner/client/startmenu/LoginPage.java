/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.startmenu;

import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.element.Spacer;
import name.martingeisse.miner.client.util.gui.element.VerticalLayout;
import name.martingeisse.miner.common.network.c2s.request.LoginRequest;
import name.martingeisse.miner.common.network.s2c.response.LoginResponse;
import name.martingeisse.miner.common.util.UserVisibleMessageException;

import java.util.prefs.Preferences;

/**
 * The "login" menu page.
 */
public class LoginPage extends AbstractStartmenuPage {

	/**
	 * the username
	 */
	private final LabeledTextField username;

	/**
	 * the password
	 */
	private final LabeledTextField password;

	/**
	 * Constructor.
	 */
	public LoginPage() {

		Preferences preferences = Preferences.userNodeForPackage(StartmenuState.class);
		String defaultUsername = preferences.get("username", null);
		if (defaultUsername == null) {
			defaultUsername = "";
		}

		username = new LabeledTextField("Username");
		password = new LabeledTextField("Password");
		username.getTextField().setNextFocusableElement(password.getTextField()).setValue(defaultUsername).moveCursorToEnd();
		password.getTextField().setNextFocusableElement(username.getTextField());
		password.getTextField().setPasswordCharacter('*');

		final VerticalLayout menu = new VerticalLayout();
		menu.addElement(username);
		menu.addElement(new Spacer(2 * Gui.GRID));
		menu.addElement(password);
		menu.addElement(new Spacer(2 * Gui.GRID));
		menu.addElement(new StartmenuButton("Log in") {
			@Override
			protected void onClick() {
				// TODO show a loading indicator
				getGui().addFollowupLogicAction(new Runnable() {
					@Override
					public void run() {
						try {
							login();
						} catch (UserVisibleMessageException e) {
							onException(e);
						}
					}
				});
			}
		});
		menu.addElement(new Spacer(2 * Gui.GRID));
		menu.addElement(EXIT_BUTTON);

//		menu.addElement(new Spacer(Gui.GRID));
//		menu.addElement(new Grid(10, 5) {
//			@Override
//			protected GuiElement newChild(final int x, final int y) {
//				Button button = new Button() {
//					@Override
//					protected void onClick() {
//						System.out.println("* " + x + ", " + y);
//
//					}
//				};
//				button.getTextLine().setText("foo");
//				return button;
//			}
//		}.initialize());

		initializeStartmenuPage(menu);

	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.control.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		LabeledTextField initialFocus = (username.getTextField().getValue().isEmpty() ? username : password);
		getGui().setFocus(initialFocus.getTextField());
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.miner.startmenu.AbstractStartmenuPage#onEnterPressed()
	 */
	@Override
	protected void onEnterPressed() {
		login();
	}

	/**
	 *
	 */
	private void login() {
		String username = this.username.getTextField().getValue();
		String password = this.password.getTextField().getValue();

		// This is the first time we actually need a network connection, so wait until connected.
		try {
			ClientEndpoint.INSTANCE.waitUntilConnected();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		// route network messages to the startmenu logic
		ClientEndpoint.INSTANCE.setMessageConsumer(StartmenuNetworkClient.INSTANCE);

		// log in
		LoginResponse response = StartmenuNetworkClient.INSTANCE.requestAndWait(new LoginRequest(username, password), LoginResponse.class);
		StartmenuState.INSTANCE.setPlayers(response.getElements());
		StartmenuState.INSTANCE.setSelectedPlayer(response.getElements().get(0));


		getGui().setRootElement(new ChooseCharacterPage());
		Preferences.userNodeForPackage(StartmenuState.class).put("username", username);
	}

}
