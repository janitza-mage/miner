/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.Main;
import name.martingeisse.miner.client.MinerResources;
import name.martingeisse.miner.client.ingame.hud.FlashMessageHandler;
import name.martingeisse.miner.client.ingame.hud.FpsPanel;
import name.martingeisse.miner.client.ingame.hud.SelectedCubeHud;
import name.martingeisse.miner.client.ingame.logic.Inventory;
import name.martingeisse.miner.client.ingame.logic.InventorySlot;
import name.martingeisse.miner.client.ingame.network.PlayerResumedMessage;
import name.martingeisse.miner.client.ingame.network.SendPositionToServerHandler;
import name.martingeisse.miner.client.ingame.network.StackdProtocolClient;
import name.martingeisse.miner.client.ingame.player.PlayerProxy;
import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.startmenu.AccountApiClient;
import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.client.util.frame.BreakFrameLoopException;
import name.martingeisse.miner.client.util.frame.HandlerList;
import name.martingeisse.miner.client.util.frame.SwappableHandler;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;
import name.martingeisse.miner.client.util.gui.GuiFrameHandler;
import name.martingeisse.miner.client.util.gui.control.Page;
import name.martingeisse.miner.client.util.lwjgl.MouseUtil;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.c2s.ResumePlayer;
import name.martingeisse.miner.common.network.s2c.UpdateInventory;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The in-game frame handler
 */
public class IngameHandler extends HandlerList {

	private static Logger logger = Logger.getLogger(IngameHandler.class);

	/**
	 * the cubeWorldHandler
	 */
	public static CubeWorldHandler cubeWorldHandler;

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
	private static GuiFrameHandler gameMenuHandler;

	/**
	 * the ingameMenuHandlerWrapper
	 */
	private static SwappableHandler gameMenuHandlerWrapper;

	/**
	 * Constructor.
	 * @throws Exception on errors
	 */
	public IngameHandler() throws Exception {

		// connect to the server
		MinerResources resources = MinerResources.getInstance();
		flashMessageHandler = new FlashMessageHandler(resources.getFont());
		protocolClient = new StackdProtocolClient();
		protocolClient.setFlashMessageHandler(flashMessageHandler);

		// game handlers
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
			public void draw(GlWorkerLoop glWorkerLoop) {
				cubeWorldHandler.draw(glWorkerLoop);
			}

		});

		// network logic handlers
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
				final ConcurrentLinkedQueue<Message> messages = protocolClient.getMessages();
				while (true) {
					Message untypedMessage = messages.poll();
					if (untypedMessage == null) {
						break;
					} else if (untypedMessage instanceof UpdateInventory) {

						System.out.println("*** updating inventory");

						UpdateInventory message = (UpdateInventory)untypedMessage;
						List<InventorySlot> slots = new ArrayList<>();
						for (UpdateInventory.Element element : message.getElements()) {
							slots.add(new InventorySlot(element.getId() + ": " + element.getName() + " (" + element.getQuantity() + ")"));
						}
						Inventory.INSTANCE.setSlots(ImmutableList.copyOf(slots));

					} else {
						logger.error("client received unexpected message: " + untypedMessage);
					}

				}

			}
		});

		// HUD handlers
		add(flashMessageHandler);
		add(new FpsPanel(resources.getFont()));
		final SelectedCubeHud selectedCubeHud = new SelectedCubeHud(
			cubeWorldHandler.getResources().getCubeTextures(),
			cubeWorldHandler::getCurrentCubeType
		);
		add(selectedCubeHud);

		// the in-game menu
		gameMenuHandler = new GuiFrameHandler();
		gameMenuHandler.getGui().setDefaultFont(MinerResources.getInstance().getFont());
		gameMenuHandlerWrapper = new SwappableHandler();
		add(gameMenuHandlerWrapper);

		// Finally, the connected to the server (which we started creating early) must be established before the game
		// can run, so if we're still not connected, wait for it. We also want to route network messages to the
		// in-game logic now.
		ClientEndpoint.INSTANCE.waitUntilConnected();
		ClientEndpoint.INSTANCE.setMessageConsumer(protocolClient);

		// TODO this will disappear anyway when the account API uses the ClientEndpoint, so don't worry about
		// performance or about blocking the game intil resumed for now
		ClientEndpoint.INSTANCE.send(new ResumePlayer(AccountApiClient.getInstance().getPlayerAccessToken().getBytes(StandardCharsets.UTF_8)));
	}

	public static void openGui(Page page) {
		gameMenuHandlerWrapper.setWrappedHandler(gameMenuHandler);
		gameMenuHandler.getGui().setRootElement(page);
		MouseUtil.ungrab();
	}

	public static void closeGui() {
		gameMenuHandlerWrapper.setWrappedHandler(null);
		MouseUtil.grab();
		CubeWorldHandler.disableLeftMouseButtonBecauseWeJustClosedTheGui = true;
	}

	public static boolean isGuiOpen() {
		return gameMenuHandlerWrapper.getWrappedHandler() != null;
	}

}
