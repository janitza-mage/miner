/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.gui;

import name.martingeisse.launcher.assets.LauncherAssets;
import name.martingeisse.miner.client.util.frame.BreakFrameLoopException;
import name.martingeisse.miner.client.util.frame.HandlerList;
import name.martingeisse.miner.client.util.gui.GuiFrameHandler;
import name.martingeisse.miner.client.util.lwjgl.FixedWidthFont;
import name.martingeisse.miner.client.util.lwjgl.ResourceLoader;

/**
 * The handler for the in-game GUI.
 */
public class GameMenuHandler extends HandlerList {

	/**
	 * This variable can be used to quit the program.
	 */
	public static boolean programmaticExit;

	/**
	 * Constructor.
	 */
	public GameMenuHandler() {
		// TODO share resources properly
		GuiFrameHandler guiFrameHandler = new GuiFrameHandler();
		guiFrameHandler.getGui().setDefaultFont(new FixedWidthFont(ResourceLoader.loadAwtImage(LauncherAssets.class, "font.png"), 8, 16));
		guiFrameHandler.getGui().setRootElement(new MainMenuPage());
		add(guiFrameHandler);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.frame.handlers.HandlerList#handleStep()
	 */
	@Override
	public void handleStep() throws BreakFrameLoopException {
		super.handleStep();
		if (programmaticExit) {
			throw new BreakFrameLoopException();
		}
	}
	
}
