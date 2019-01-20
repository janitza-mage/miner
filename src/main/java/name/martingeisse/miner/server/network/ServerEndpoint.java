/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.network;

import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.s2c.Hello;
import name.martingeisse.miner.common.network.protocol.ProtocolEndpoint;
import org.apache.log4j.Logger;

/**
 *
 */
final class ServerEndpoint extends ProtocolEndpoint {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(ServerEndpoint.class);

	/**
	 * the server
	 */
	private final StackdServer server;

	/**
	 * the session
	 */
	private StackdSession session = null;

	/**
	 * Constructor.
	 * @param server the server
	 */
	public ServerEndpoint(final StackdServer server) {
		this.server = server;
	}

	@Override
	protected void onConnect() {
		session = server.createSession(this);
		session.send(new Hello(session.getId()));
		logger.info("client connected: " + session.getId());
		server.onClientConnected(session);
	}

	@Override
	protected void onDisconnect() {
		server.internalOnClientDisconnected(session);
	}

	@Override
	protected void onMessage(Message message) {
		// premature data packets will be ignored
		if (session != null) {
			server.onMessageReceived(session, message);
		}
	}

}
