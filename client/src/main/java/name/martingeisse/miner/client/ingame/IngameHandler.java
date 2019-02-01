/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.ingame.hud.FlashMessageHandler;
import name.martingeisse.miner.client.ingame.hud.FpsPanel;
import name.martingeisse.miner.client.ingame.hud.SelectedCubeHud;
import name.martingeisse.miner.client.ingame.logic.Inventory;
import name.martingeisse.miner.client.ingame.logic.InventorySlot;
import name.martingeisse.miner.client.ingame.network.PlayerResumedMessage;
import name.martingeisse.miner.client.ingame.network.SendPositionToServerHandler;
import name.martingeisse.miner.client.ingame.network.StackdProtocolClient;
import name.martingeisse.miner.client.ingame.player.PlayerProxy;
import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.client.util.frame.BreakFrameLoopException;
import name.martingeisse.miner.client.util.frame.HandlerList;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.s2c.UpdateInventory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The in-game frame handler
 */
public class IngameHandler extends HandlerList {

	private static Logger logger = Logger.getLogger(IngameHandler.class);

	/**
	 * the cubeWorldHelper
	 */
	public static CubeWorldHelper cubeWorldHelper;

	/**
	 * the protocolClient
	 */
	public static StackdProtocolClient protocolClient;

	/**
	 * the flashMessageHandler
	 */
	public static FlashMessageHandler flashMessageHandler;

	/**
	 * Constructor.
	 *
	 * @throws Exception on errors
	 */
	public IngameHandler() throws Exception {

		// game handlers
		add(new CubeWorldHandler());

		// network logic handlers
		add(new SendPositionToServerHandler(cubeWorldHelper.getPlayer()));
		add(new AbstractFrameHandler() {
			@Override
			public void handleStep() throws BreakFrameLoopException {

				// TODO race condition: should not start the game until the player has been resumed,
				// would be wrong and also load wrong sections

				final List<PlayerProxy> updatedPlayerProxies = protocolClient.fetchUpdatedPlayerProxies();
				if (updatedPlayerProxies != null) {
					cubeWorldHelper.setPlayerProxies(updatedPlayerProxies);
				}
				final PlayerResumedMessage playerResumedMessage = protocolClient.fetchPlayerResumedMessage();
				if (playerResumedMessage != null) {
					cubeWorldHelper.getPlayer().getPosition().copyFrom(playerResumedMessage.getPosition());
					cubeWorldHelper.getPlayer().getOrientation().copyFrom(playerResumedMessage.getOrientation());
					protocolClient.getSectionGridLoader().setViewerPosition(cubeWorldHelper.getPlayer().getSectionId());
				}
				final ConcurrentLinkedQueue<Message> messages = protocolClient.getMessages();
				while (true) {
					Message untypedMessage = messages.poll();
					if (untypedMessage == null) {
						break;
					} else if (untypedMessage instanceof UpdateInventory) {

						System.out.println("*** updating inventory");

						UpdateInventory message = (UpdateInventory) untypedMessage;
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
		add(new FpsPanel());
		final SelectedCubeHud selectedCubeHud = new SelectedCubeHud(
			cubeWorldHelper.getResources().getCubeTextures(),
			cubeWorldHelper::getCurrentCubeType
		);
		add(selectedCubeHud);

	}

}
