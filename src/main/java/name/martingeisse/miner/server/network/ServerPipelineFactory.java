/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.network;

import name.martingeisse.miner.common.network.protocol.ProtocolEndpoint;
import name.martingeisse.miner.common.network.protocol.ProtocolPipelineFactory;

/**
 *
 */
public final class ServerPipelineFactory extends ProtocolPipelineFactory {

	private final StackdServer server;

	public ServerPipelineFactory(StackdServer server) {
		this.server = server;
	}

	@Override
	protected ProtocolEndpoint createProtocolEndpoint() {
		return new ServerEndpoint(server);
	}

}
