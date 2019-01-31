/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.startmenu;

import name.martingeisse.launcher.assets.LauncherAssets;
import name.martingeisse.miner.client.util.UserVisibleMessageException;
import name.martingeisse.miner.client.util.frame.BreakFrameLoopException;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiEvent;
import name.martingeisse.miner.client.util.gui.control.MessageBox;
import name.martingeisse.miner.client.util.gui.control.Page;
import name.martingeisse.miner.client.util.gui.element.fill.FillTexture;
import name.martingeisse.miner.client.util.gui.element.Margin;
import name.martingeisse.miner.client.util.lwjgl.StackdTexture;
import org.lwjgl.input.Keyboard;

/**
 * The base class for start menu pages.
 */
public class AbstractStartmenuPage extends Page {

	/**
	 * the EXIT_BUTTON
	 */
	protected static final StartmenuButton EXIT_BUTTON = new StartmenuButton("Quit") {
		@Override
		protected void onClick() {
			getGui().addFollowupLogicAction(() -> {
				throw new BreakFrameLoopException();
			});
		}
	};
	
	/**
	 * Constructor.
	 */
	public AbstractStartmenuPage() {
	}

	/**
	 * 
	 */
	protected final void initializeStartmenuPage(GuiElement mainElement) {
		StackdTexture backgroundTexture = new StackdTexture(LauncherAssets.class, "dirt.png", false);
		initializePage(new FillTexture(backgroundTexture), new Margin(mainElement, 30 * Gui.GRID, 30 * Gui.GRID));
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
		if (event == GuiEvent.KEY_PRESSED && Keyboard.getEventCharacter() == '\r') {
			onEnterPressed();
		}
	}

	/**
	 * Called when the user presses the "enter" key.
	 */
	protected void onEnterPressed() {
	}
	
}
