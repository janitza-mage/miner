/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.network;

import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.ProtocolEndpoint;

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
		protocolClient.setEndpoint(this);
	}

	@Override
	protected void onDisconnect() {
		protocolClient.setEndpoint(null);
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
