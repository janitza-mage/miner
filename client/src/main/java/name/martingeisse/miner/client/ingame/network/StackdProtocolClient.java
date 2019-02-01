/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.network;

import name.martingeisse.miner.client.network.MessageConsumer;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.s2c.InteractiveSectionDataResponse;
import name.martingeisse.miner.common.network.s2c.SingleSectionModificationEvent;
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

	/**
	 * This method gets invoked by a Netty thread when a message has been received from the server. Most messages are
	 * routed to the ingame message queue, but a few are handled directly to improve performance.
	 */
	public void consume(Message untypedMessage) {
		if (untypedMessage instanceof InteractiveSectionDataResponse) {

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

		} else {
			messages.add(untypedMessage);
		}
	}

}
