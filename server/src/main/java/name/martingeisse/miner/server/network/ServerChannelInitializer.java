/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.network;

import name.martingeisse.miner.common.network.ProtocolChannelInitializer;
import name.martingeisse.miner.common.network.ProtocolEndpoint;

/**
 *
 */
public final class ServerChannelInitializer extends ProtocolChannelInitializer {

	private final StackdServer server;

	public ServerChannelInitializer(StackdServer server) {
		this.server = server;
	}

	@Override
	protected ProtocolEndpoint createProtocolEndpoint() {
		return new ServerEndpoint(server);
	}

}
