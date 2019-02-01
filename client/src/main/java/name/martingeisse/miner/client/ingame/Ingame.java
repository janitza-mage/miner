/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame;

import name.martingeisse.miner.client.Main;
import name.martingeisse.miner.client.MinerResources;
import name.martingeisse.miner.client.ingame.hud.FlashMessageHandler;
import name.martingeisse.miner.client.ingame.hud.FpsPanel;
import name.martingeisse.miner.client.ingame.hud.SelectedCubeHud;
import name.martingeisse.miner.client.ingame.network.SectionGridLoader;
import name.martingeisse.miner.client.ingame.network.SendPositionToServerHandler;
import name.martingeisse.miner.client.ingame.network.StackdProtocolClient;
import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.startmenu.AccountApiClient;
import name.martingeisse.miner.client.util.frame.HandlerList;
import name.martingeisse.miner.client.util.frame.SwappableHandler;
import name.martingeisse.miner.client.util.gui.GuiFrameHandler;
import name.martingeisse.miner.client.util.gui.control.Page;
import name.martingeisse.miner.client.util.lwjgl.MouseUtil;
import name.martingeisse.miner.common.network.c2s.ResumePlayer;

import java.nio.charset.StandardCharsets;

/**
 * The class wraps all in-game objects.
 */
public final class Ingame {

	private static Ingame INSTANCE = null;

	public static void create() throws Exception {
		if (INSTANCE != null) {
			throw new IllegalStateException("ingame already exists");
		}
		INSTANCE = new Ingame();
		INSTANCE.onCreate();
	}

	public static void destroy() {
		ensureExists();
		INSTANCE.onDestroy();
		INSTANCE = null;
	}

	public static Ingame get() {
		ensureExists();
		return INSTANCE;
	}

	public static void ensureExists() {
		if (INSTANCE == null) {
			throw new IllegalStateException("ingame does not exist");
		}
	}

	//
	// ---------------------------------------------------------------------------------------------------
	//

	private FlashMessageHandler flashMessageHandler;
	private StackdProtocolClient protocolClient;
	private CubeWorldHelper cubeWorldHelper;
	private GuiFrameHandler gameMenuHandler;
	private SwappableHandler gameMenuHandlerWrapper;

	private HandlerList handlerList;

	public Ingame() throws Exception {

		flashMessageHandler = new FlashMessageHandler();
		protocolClient = new StackdProtocolClient();
		protocolClient.setFlashMessageHandler(flashMessageHandler);
		cubeWorldHelper = new CubeWorldHelper(Main.screenWidth, Main.screenHeight);

		// TODO: implement better checking for connection problems: only stall when surrounding sections
		// are missing AND the player is in that half of the current section. currently using collider
		// radius 2 to avoid "connection problems" when crossing a section boundary
		protocolClient.setSectionGridLoader(new SectionGridLoader(cubeWorldHelper.getWorkingSet(), 3, 2));

		// the in-game menu
		gameMenuHandler = new GuiFrameHandler();
		gameMenuHandler.getGui().setDefaultFont(MinerResources.getInstance().getFont());
		gameMenuHandlerWrapper = new SwappableHandler();

	}

	private void onCreate() {

		// The connection to the server (which we started creating early) must be established before the game
		// can run, so if we're still not connected, wait for it. We also want to route network messages to the
		// in-game logic now.
		try {
			ClientEndpoint.INSTANCE.waitUntilConnected();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		ClientEndpoint.INSTANCE.setMessageConsumer(protocolClient);

		// TODO this will disappear anyway when the account API uses the ClientEndpoint, so don't worry about
		// performance or about blocking the game intil resumed for now
		ClientEndpoint.INSTANCE.send(new ResumePlayer(AccountApiClient.getInstance().getPlayerAccessToken().getBytes(StandardCharsets.UTF_8)));

		// Switch the frame handler to the in-game handler list
		handlerList = new HandlerList();
		handlerList.add(new CubeWorldHandler());
		handlerList.add(new SendPositionToServerHandler(Ingame.get().getCubeWorldHelper().getPlayer()));
		handlerList.add(new MessageFrameHandler());
		handlerList.add(new SelectedCubeHud(
			Ingame.get().getCubeWorldHelper().getResources().getCubeTextures(),
			Ingame.get().getCubeWorldHelper()::getCurrentCubeType
		));
		handlerList.add(flashMessageHandler);
		handlerList.add(new FpsPanel());
		handlerList.add(gameMenuHandlerWrapper);
		Main.frameLoop.getRootHandler().setWrappedHandler(handlerList);
	}

	private void onDestroy() {

		// remove from the frame loop
		if (Main.frameLoop.getRootHandler().getWrappedHandler() == handlerList) {
			Main.frameLoop.getRootHandler().setWrappedHandler(null);
		}
		handlerList = null;

	}

	public FlashMessageHandler getFlashMessageHandler() {
		return flashMessageHandler;
	}

	public void setFlashMessageHandler(FlashMessageHandler flashMessageHandler) {
		this.flashMessageHandler = flashMessageHandler;
	}

	public StackdProtocolClient getProtocolClient() {
		return protocolClient;
	}

	public void setProtocolClient(StackdProtocolClient protocolClient) {
		this.protocolClient = protocolClient;
	}

	public CubeWorldHelper getCubeWorldHelper() {
		return cubeWorldHelper;
	}

	public void setCubeWorldHelper(CubeWorldHelper cubeWorldHelper) {
		this.cubeWorldHelper = cubeWorldHelper;
	}

	public void openGui(Page page) {
		gameMenuHandlerWrapper.setWrappedHandler(gameMenuHandler);
		gameMenuHandler.getGui().setRootElement(page);
		MouseUtil.ungrab();
	}

	public void closeGui() {
		gameMenuHandlerWrapper.setWrappedHandler(null);
		MouseUtil.grab();
		CubeWorldHelper.disableLeftMouseButtonBecauseWeJustClosedTheGui = true;
	}

	public boolean isGuiOpen() {
		return gameMenuHandlerWrapper.getWrappedHandler() != null;
	}

}
