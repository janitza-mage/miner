/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame;

import name.martingeisse.launcher.assets.LauncherAssets;
import name.martingeisse.miner.client.Main;
import name.martingeisse.miner.client.ingame.frame.FlashMessageHandler;
import name.martingeisse.miner.client.ingame.frame.FpsPanel;
import name.martingeisse.miner.client.ingame.frame.SelectedCubeHud;
import name.martingeisse.miner.client.ingame.gui.MainMenuPage;
import name.martingeisse.miner.client.ingame.network.PlayerResumedMessage;
import name.martingeisse.miner.client.ingame.network.SendPositionToServerHandler;
import name.martingeisse.miner.client.ingame.player.PlayerProxy;
import name.martingeisse.miner.client.network.StackdProtocolClient;
import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.client.util.frame.BreakFrameLoopException;
import name.martingeisse.miner.client.util.frame.HandlerList;
import name.martingeisse.miner.client.util.frame.SwappableHandler;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;
import name.martingeisse.miner.client.util.gui.GuiFrameHandler;
import name.martingeisse.miner.client.util.lwjgl.FixedWidthFont;
import name.martingeisse.miner.client.util.lwjgl.ResourceLoader;

import java.util.List;

/**
 * The in-game frame handler
 */
public class IngameHandler extends HandlerList {

	/**
	 * the cubeWorldHandler
	 */
	public static CubeWorldHandler cubeWorldHandler;

	/**
	 * the serverBaseUrl
	 */
	public static String serverBaseUrl;

	/**
	 * the serverName
	 */
	public static String serverName;

	/**
	 * the enableTexGen
	 */
	public static boolean enableTexGen = true;

	/**
	 * the enableTexturing
	 */
	public static boolean enableTexturing = true;

	/**
	 * the protocolClient
	 */
	public static StackdProtocolClient protocolClient;

	/**
	 * the flashMessageHandler
	 */
	public static FlashMessageHandler flashMessageHandler;

	/**
	 * the gameMenuHandler
	 */
	public static GuiFrameHandler gameMenuHandler;

	/**
	 * the ingameMenuHandlerWrapper
	 */
	public static SwappableHandler gameMenuHandlerWrapper;

	/**
	 * Constructor.
	 * @throws Exception on errors
	 */
	public IngameHandler() throws Exception {

		// determine server base URL (old HTTP protocol)
		serverBaseUrl = System.getProperty("name.martingeisse.miner.serverBaseUrl");
		if (serverBaseUrl == null) {
			serverBaseUrl = "http://localhost:8080";
		} else if (serverBaseUrl.equals("LIVE")) {
			serverBaseUrl = "http://vshg03.mni.fh-giessen.de:8080";
		}

		// determine server name (new binary protocol)
		serverName = System.getProperty("name.martingeisse.miner.serverName");
		if (serverName == null) {
			serverName = "localhost";
		} else if (serverName.equals("LIVE")) {
			serverName = "vshg03.mni.fh-giessen.de";
		}

		// connect to the server
		MinerResources resources = MinerResources.getInstance();
		flashMessageHandler = new FlashMessageHandler(resources.getFont());
		protocolClient = new StackdProtocolClient();
		protocolClient.setFlashMessageHandler(flashMessageHandler);

		// build the cube world handler
		cubeWorldHandler = new CubeWorldHandler(Main.screenWidth, Main.screenHeight, resources);
		add(new AbstractFrameHandler() {

			/* (non-Javadoc)
			 * @see name.martingeisse.stackd.frame.AbstractFrameHandler#handleStep()
			 */
			@Override
			public void handleStep() throws BreakFrameLoopException {
				cubeWorldHandler.step();

				// TODO avoid filling up the render queue, should detect when the logic thread is running too fast
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				}

			}

			@Override
			public void onAfterHandleStep() {
				cubeWorldHandler.purge();
			}

			@Override
			public void draw(GlWorkerLoop glWorkerLoop) {
				cubeWorldHandler.draw(glWorkerLoop);
			}

		});
		add(new FpsPanel(resources.getFont()));
		final SelectedCubeHud selectedCubeHud = new SelectedCubeHud(cubeWorldHandler.getResources().getCubeTextures(), cubeWorldHandler.getWorkingSet().getEngineParameters().getCubeTypes());
		add(selectedCubeHud);
		add(new AbstractFrameHandler() {
			@Override
			public void onBeforeDraw(GlWorkerLoop glWorkerLoop) {
				selectedCubeHud.setCubeTypeIndex(cubeWorldHandler.getCurrentCubeType());
			}
		});
		add(new SendPositionToServerHandler(cubeWorldHandler.getPlayer()));
		add(new AbstractFrameHandler() {
			@Override
			public void handleStep() throws BreakFrameLoopException {

				// TODO race condition: should not start the game until the player has been resumed,
				// would be wrong and also load wrong sections

				final List<PlayerProxy> updatedPlayerProxies = protocolClient.fetchUpdatedPlayerProxies();
				if (updatedPlayerProxies != null) {
					cubeWorldHandler.setPlayerProxies(updatedPlayerProxies);
				}
				final PlayerResumedMessage playerResumedMessage = protocolClient.fetchPlayerResumedMessage();
				if (playerResumedMessage != null) {
					cubeWorldHandler.getPlayer().getPosition().copyFrom(playerResumedMessage.getPosition());
					cubeWorldHandler.getPlayer().getOrientation().copyFrom(playerResumedMessage.getOrientation());
					protocolClient.getSectionGridLoader().setViewerPosition(cubeWorldHandler.getPlayer().getSectionId());
				}
			}
		});
		add(flashMessageHandler);
		
		// the in-game menu
		{
			// TODO share resources properly
			gameMenuHandler = new GuiFrameHandler();
			gameMenuHandler.getGui().setDefaultFont(new FixedWidthFont(ResourceLoader.loadAwtImage(LauncherAssets.class, "font.png"), 8, 16));
			gameMenuHandler.getGui().setRootElement(new MainMenuPage());
		}
		gameMenuHandlerWrapper = new SwappableHandler();
		add(gameMenuHandlerWrapper);

		// prepare running the game
		protocolClient.setFlashMessageHandler(flashMessageHandler);
		protocolClient.waitUntilReady();

	}

}
