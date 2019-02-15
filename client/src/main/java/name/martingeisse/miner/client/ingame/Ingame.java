/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame;

import name.martingeisse.miner.client.Main;
import name.martingeisse.miner.client.MinerResources;
import name.martingeisse.miner.client.ingame.hud.EquippedItemHud;
import name.martingeisse.miner.client.ingame.hud.FlashMessageHandler;
import name.martingeisse.miner.client.ingame.hud.FpsPanel;
import name.martingeisse.miner.client.ingame.hud.SelectedCubeHud;
import name.martingeisse.miner.client.ingame.logic.Inventory;
import name.martingeisse.miner.client.ingame.logic.InventorySlot;
import name.martingeisse.miner.client.ingame.network.IngameMessageRouter;
import name.martingeisse.miner.client.ingame.network.SectionGridLoader;
import name.martingeisse.miner.client.ingame.network.SendPositionToServerHandler;
import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.startmenu.StartmenuState;
import name.martingeisse.miner.client.util.frame.HandlerList;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiFrameHandler;
import name.martingeisse.miner.client.util.gui.control.Page;
import name.martingeisse.miner.client.util.lwjgl.MouseUtil;
import name.martingeisse.miner.common.logic.EquipmentSlot;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.c2s.ResumePlayer;
import org.lwjgl.opengl.Display;

import java.util.concurrent.ConcurrentLinkedQueue;

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

	private final FlashMessageHandler flashMessageHandler;
	private final CubeWorldHandler cubeWorldHandler;
	private final GuiFrameHandler gameMenuHandler;
	private final SectionGridLoader sectionGridLoader;
	private final ConcurrentLinkedQueue<Message> inboundMessageQueue = new ConcurrentLinkedQueue<>();

	private HandlerList handlerList;
	private long coins = 0;

	public Ingame() throws Exception {

		flashMessageHandler = new FlashMessageHandler();
		cubeWorldHandler = new CubeWorldHandler(Display.getWidth(), Display.getHeight());

		// the in-game menu
		gameMenuHandler = new GuiFrameHandler();
		gameMenuHandler.getGui().setDefaultFont(MinerResources.getInstance().getFont());
		gameMenuHandler.setEnableGui(false);

		// TODO: implement better checking for connection problems: only stall when surrounding sections
		// are missing AND the player is in that half of the current section. currently using collider
		// radius 2 to avoid "connection problems" when crossing a section boundary
		sectionGridLoader = new SectionGridLoader(cubeWorldHandler.getWorkingSet(), 3, 2);

	}

	private void onCreate() {

		// Route network messages to the in-game logic from now.
		ClientEndpoint.INSTANCE.setMessageConsumer(new IngameMessageRouter());

		// resume the game with the player selected in the start menu
		ClientEndpoint.INSTANCE.send(new ResumePlayer(StartmenuState.INSTANCE.getSelectedPlayer().getId()));

		// Switch the frame handler to the in-game handler list
		handlerList = new HandlerList();
		handlerList.add(cubeWorldHandler);
		handlerList.add(new SendPositionToServerHandler(cubeWorldHandler.getPlayer()));
		handlerList.add(new MessageFrameHandler());
		// handlerList.add(new SelectedCubeHud(cubeWorldHandler.getResources().getCubeTextures(), cubeWorldHandler::getCurrentCubeType));
		handlerList.add(new SelectedCubeHud(MinerResources.getInstance().getCubeTextures(), () -> {
			InventorySlot slot = Inventory.INSTANCE.getEquippedItems().get(EquipmentSlot.HAND);
			return (slot == null ? null : slot.getType());
		}));
		handlerList.add(new EquippedItemHud());
		handlerList.add(flashMessageHandler);
		handlerList.add(new FpsPanel());
		handlerList.add(gameMenuHandler);
		Main.frameLoop.getRootHandler().setWrappedHandler(handlerList);
	}

	private void onDestroy() {

		// remove from the frame loop
		if (Main.frameLoop.getRootHandler().getWrappedHandler() == handlerList) {
			Main.frameLoop.getRootHandler().setWrappedHandler(null);
		}
		handlerList = null;

	}

	public CubeWorldHandler getCubeWorldHandler() {
		return cubeWorldHandler;
	}

	public SectionGridLoader getSectionGridLoader() {
		return sectionGridLoader;
	}

	public void openGui(Page page) {
		gameMenuHandler.getGui().setRootElement(page);
		gameMenuHandler.setEnableGui(true);
		MouseUtil.ungrab();
	}

	public void closeGui() {
		gameMenuHandler.setEnableGui(false);
		MouseUtil.grab();
		CubeWorldHandler.disableLeftMouseButtonBecauseWeJustClosedTheGui = true;
	}

	public boolean isGuiOpen() {
		return gameMenuHandler.isEnableGui();
	}

	public Gui getGui() {
		return gameMenuHandler.getGui();
	}

	public void showFlashMessage(String text) {
		flashMessageHandler.addMessage(text);
	}

	public long getCoins() {
		return coins;
	}

	public void setCoins(long coins) {
		this.coins = coins;
	}

	public ConcurrentLinkedQueue<Message> getInboundMessageQueue() {
		return inboundMessageQueue;
	}

}
