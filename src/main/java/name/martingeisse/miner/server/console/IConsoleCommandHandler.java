/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.console;

import name.martingeisse.miner.server.network.StackdSession;

/**
 * The network code delegates to this object to handle incoming console commands.
 */
public interface IConsoleCommandHandler {

	/**
	 * Handles a command.
	 *
	 * @param session the session of the client sending the command
	 * @param command the command
	 * @param args arguments
	 */
	void handleCommand(StackdSession session, String command, String[] args);

}
