/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.gui;

import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.util.gui.GuiEvent;
import name.martingeisse.miner.client.util.gui.control.MessageBox;
import name.martingeisse.miner.client.util.gui.control.Page;
import name.martingeisse.miner.common.util.UserVisibleMessageException;
import org.lwjgl.input.Keyboard;

/**
 * The base class for start menu pages.
 */
public class AbstractGameGuiPage extends Page {

	/**
	 * Constructor.
	 */
	public AbstractGameGuiPage() {
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.control.Page#onException(java.lang.Throwable)
	 */
	@Override
	protected void onException(Throwable t) {
		if (t instanceof UserVisibleMessageException) {
			new MessageBox(t.getMessage()).show(this);
		} else {
			super.onException(t);
			new MessageBox("An error has occurred.").show(this);
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.gui.control.Page#handlePageEvent(name.martingeisse.stackd.client.gui.GuiEvent)
	 */
	@Override
	protected void handlePageEvent(GuiEvent event) {
		super.handlePageEvent(event);
		if (event == GuiEvent.KEY_PRESSED) {
			switch (Keyboard.getEventKey()) {

				case Keyboard.KEY_RETURN:
					onEnterPressed();
					break;

				case Keyboard.KEY_I:
					if (this instanceof InventoryPage) {
						Ingame.get().closeGui();
					} else {
						Ingame.get().openGui(new InventoryPage());
					}
					break;

				case Keyboard.KEY_ESCAPE:
					Ingame.get().closeGui();
					break;

			}
		}
	}

	/**
	 * Called when the user presses the "enter" key.
	 */
	protected void onEnterPressed() {
	}
	
}
