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

	private final StackdProtocolClient protocolClient;

	public ClientChannelInitializer(StackdProtocolClient protocolClient) {
		this.protocolClient = protocolClient;
	}

	@Override
	protected ProtocolEndpoint createProtocolEndpoint() {
		return new ClientEndpoint(protocolClient);
	}

}
