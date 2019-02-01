/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.ingame.logic.Inventory;
import name.martingeisse.miner.client.ingame.logic.InventorySlot;
import name.martingeisse.miner.client.ingame.network.PlayerResumedMessage;
import name.martingeisse.miner.client.ingame.network.StackdProtocolClient;
import name.martingeisse.miner.client.ingame.player.PlayerProxy;
import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.client.util.frame.BreakFrameLoopException;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.s2c.UpdateInventory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 */
public class MessageFrameHandler extends AbstractFrameHandler {

	private static Logger logger = Logger.getLogger(MessageFrameHandler.class);

	@Override
	public void handleStep() throws BreakFrameLoopException {

		// TODO race condition: should not start the game until the player has been resumed,
		// would be wrong and also load wrong sections

		CubeWorldHandler cubeWorldHandler = Ingame.get().getCubeWorldHandler();
		StackdProtocolClient protocolClient = Ingame.get().getProtocolClient();

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

}
