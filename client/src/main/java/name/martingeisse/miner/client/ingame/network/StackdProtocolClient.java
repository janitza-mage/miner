/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.network;

import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.network.MessageConsumer;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.s2c.InteractiveSectionDataResponse;
import name.martingeisse.miner.common.network.s2c.SingleSectionModificationEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class handles the connection to the server. Applications are
 * free to create subclasses that add application-specific message
 * types. TODO rename to IngameMessageRouter when refactoring is done.
 */
public class StackdProtocolClient implements MessageConsumer {

	private final ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<>();

	public ConcurrentLinkedQueue<Message> getMessages() {
		return messages;
	}

	/**
	 * This method gets invoked by a Netty thread when a message has been received from the server. Most messages are
	 * routed to the ingame message queue, but a few are handled directly to improve performance.
	 */
	public void consume(Message untypedMessage) {
		if (untypedMessage instanceof InteractiveSectionDataResponse) {
			InteractiveSectionDataResponse message = (InteractiveSectionDataResponse) untypedMessage;
			Ingame.get().getSectionGridLoader().handleInteractiveSectionImage(message);
		} else if (untypedMessage instanceof SingleSectionModificationEvent) {
			SingleSectionModificationEvent message = (SingleSectionModificationEvent) untypedMessage;
			Ingame.get().getSectionGridLoader().handleModificationEvent(message);
		} else {
			messages.add(untypedMessage);
		}
	}

}
