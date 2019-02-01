/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.network;

import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.network.MessageConsumer;
import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.c2s.DigNotification;
import name.martingeisse.miner.common.network.s2c.*;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class handles the connection to the server. Applications are
 * free to create subclasses that add application-specific message
 * types. TODO rename to IngameMessageRouter when refactoring is done.
 */
public class StackdProtocolClient implements MessageConsumer {

	private static Logger logger = Logger.getLogger(StackdProtocolClient.class);

	private SectionGridLoader sectionGridLoader;

	// TODO move everything to this queue that must be consumed by the game thread
	private final ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<>();

	/**
	 * Constructor.
	 */
	public StackdProtocolClient() {
	}

	public ConcurrentLinkedQueue<Message> getMessages() {
		return messages;
	}

	/**
	 * Getter method for the sectionGridLoader.
	 *
	 * @return the sectionGridLoader
	 */
	public SectionGridLoader getSectionGridLoader() {
		return sectionGridLoader;
	}

	/**
	 * Setter method for the sectionGridLoader.
	 *
	 * @param sectionGridLoader the sectionGridLoader to set
	 */
	public void setSectionGridLoader(SectionGridLoader sectionGridLoader) {
		this.sectionGridLoader = sectionGridLoader;
	}

	public final void send(Message message) {
		ClientEndpoint.INSTANCE.send(message);
	}

	/**
	 * This method gets invoked when receiving an application packet from the server.
	 * The default implementation does nothing.
	 * <p>
	 * NOTE: This method gets invoked by the Netty thread, asynchronous
	 * to the main game thread!
	 */
	public void consume(Message untypedMessage) {
		if (untypedMessage instanceof FlashMessage) {

			FlashMessage message = (FlashMessage) untypedMessage;
			Ingame.get().showFlashMessage(message.getText());

		} else if (untypedMessage instanceof InteractiveSectionDataResponse) {

			if (sectionGridLoader != null) {
				InteractiveSectionDataResponse message = (InteractiveSectionDataResponse) untypedMessage;
				sectionGridLoader.handleInteractiveSectionImage(message);
			} else {
				logger.error("received interactive section image but no sectionGridLoader is set in the StackdProtoclClient!");
			}

		} else if (untypedMessage instanceof SingleSectionModificationEvent) {

			if (sectionGridLoader != null) {
				SingleSectionModificationEvent message = (SingleSectionModificationEvent) untypedMessage;
				sectionGridLoader.handleModificationEvent(message);
			} else {
				logger.error("received section modification event but no sectionGridLoader is set in the StackdProtoclClient!");
			}

		} else if (untypedMessage instanceof PlayerListUpdate) {
			messages.add(untypedMessage);
		} else if (untypedMessage instanceof PlayerResumed) {
			messages.add(untypedMessage);
		} else if (untypedMessage instanceof UpdateCoins) {
			messages.add(untypedMessage);
		} else if (untypedMessage instanceof UpdateInventory) {
			messages.add(untypedMessage);
		} else {
			logger.error("client received unexpected message: " + untypedMessage);
		}
	}

	/**
	 * Sends a notification about the fact that this player has dug away a cube.
	 *
	 * @param x the x position of the cube
	 * @param y the y position of the cube
	 * @param z the z position of the cube
	 */
	public void sendDigNotification(int x, int y, int z) {
		send(new DigNotification(new Vector3i(x, y, z)));
	}

}
