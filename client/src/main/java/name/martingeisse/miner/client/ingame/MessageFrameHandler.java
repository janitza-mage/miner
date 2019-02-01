/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.ingame.logic.Inventory;
import name.martingeisse.miner.client.ingame.logic.InventorySlot;
import name.martingeisse.miner.client.ingame.network.StackdProtocolClient;
import name.martingeisse.miner.client.ingame.player.PlayerProxy;
import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.s2c.PlayerListUpdate;
import name.martingeisse.miner.common.network.s2c.PlayerResumed;
import name.martingeisse.miner.common.network.s2c.UpdateCoins;
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
	public void handleStep() {

		// TODO race condition: should not start the game until the player has been resumed,
		// would be wrong and also load wrong sections

		CubeWorldHandler cubeWorldHandler = Ingame.get().getCubeWorldHandler();
		StackdProtocolClient protocolClient = Ingame.get().getProtocolClient();

		final ConcurrentLinkedQueue<Message> messages = protocolClient.getMessages();
		while (true) {
			Message untypedMessage = messages.poll();
			if (untypedMessage == null) {
				break;
			} else if (untypedMessage instanceof UpdateInventory) {

				UpdateInventory message = (UpdateInventory) untypedMessage;
				List<InventorySlot> slots = new ArrayList<>();
				for (UpdateInventory.Element element : message.getElements()) {
					slots.add(new InventorySlot(element.getId() + ": " + element.getName() + " (" + element.getQuantity() + ")"));
				}
				Inventory.INSTANCE.setSlots(ImmutableList.copyOf(slots));

			} else if (untypedMessage instanceof PlayerResumed) {

				PlayerResumed message = (PlayerResumed) untypedMessage;
				cubeWorldHandler.getPlayer().getPosition().copyFrom(message.getPosition());
				cubeWorldHandler.getPlayer().getOrientation().copyFrom(message.getOrientation());
				protocolClient.getSectionGridLoader().setViewerPosition(cubeWorldHandler.getPlayer().getSectionId());

			} else if (untypedMessage instanceof PlayerListUpdate) {

				PlayerListUpdate message = (PlayerListUpdate) untypedMessage;
				List<PlayerProxy> updatedPlayerProxies = new ArrayList<PlayerProxy>();
				for (PlayerListUpdate.Element element : message.getElements()) {
					PlayerProxy proxy = new PlayerProxy();
					proxy.getPosition().copyFrom(element.getPosition());
					proxy.getOrientation().copyFrom(element.getAngles());
					proxy.setName(element.getName());
					updatedPlayerProxies.add(proxy);
				}
				cubeWorldHandler.setPlayerProxies(updatedPlayerProxies);

			} else if (untypedMessage instanceof UpdateCoins) {

				UpdateCoins message = (UpdateCoins) untypedMessage;
				Ingame.get().setCoins(message.getCoins());

			} else {
				logger.error("client received unexpected message: " + untypedMessage);
			}

		}

	}

}
