/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.network;

import name.martingeisse.miner.common.network.ProtocolEndpoint;
import name.martingeisse.miner.common.network.ProtocolChannelInitializer;

/**
 *
 */
public final class ClientChannelInitializer extends ProtocolChannelInitializer {

	private final MessageConsumer messageConsumer;

	public ClientChannelInitializer(MessageConsumer messageConsumer) {
		this.messageConsumer = messageConsumer;
	}

	@Override
	protected ProtocolEndpoint createProtocolEndpoint() {
		return new ClientEndpoint(messageConsumer);
	}

}
