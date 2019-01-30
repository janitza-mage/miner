/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.network;

import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.ProtocolEndpoint;
import org.apache.log4j.Logger;

import java.nio.channels.ClosedChannelException;

/**
 *
 */
final class ClientEndpoint extends ProtocolEndpoint implements MessageSender {

	private static Logger logger = Logger.getLogger(ClientEndpoint.class);

	private final MessageConsumer messageConsumer;

	public ClientEndpoint(MessageConsumer messageConsumer) {
		this.messageConsumer = messageConsumer;
	}

	@Override
	protected void onConnect() {
		messageConsumer.setMessageSender(this);
	}

	@Override
	protected void onDisconnect() {
		messageConsumer.setMessageSender(null);
	}

	@Override
	protected void onDisconnectAfterException(Throwable originalException) {
		// should handle this more gracefully in the future
		Throwable t = originalException;
		while (true) {
			if (t instanceof ClosedChannelException) {
				logger.error("lost connection to server");
				System.exit(0);
			}
			if (t.getCause() == t || t.getCause() == null) {
				throw new RuntimeException(originalException);
			}
			t = t.getCause();
		}
	}

	@Override
	protected void onMessage(Message message) {
		messageConsumer.consume(message);
	}

}
