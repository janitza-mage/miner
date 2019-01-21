/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.network;

import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.protocol.ProtocolEndpoint;
import org.apache.log4j.Logger;

/**
 *
 */
final class ClientEndpoint extends ProtocolEndpoint {

	private final StackdProtocolClient protocolClient;

	public ClientEndpoint(StackdProtocolClient protocolClient) {
		this.protocolClient = protocolClient;
	}

	@Override
	protected void onConnect() {
		// NOP
	}

	@Override
	protected void onDisconnect() {
		// NOP
	}

	@Override
	protected void onDisconnectAfterException(Throwable t) {
		protocolClient.onException(t);
	}

	@Override
	protected void onMessage(Message message) {
		protocolClient.onMessageReceived(message);
	}

}
