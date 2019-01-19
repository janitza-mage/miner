/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.console;

import name.martingeisse.miner.server.network.StackdSession;

/**
 * Ignores all commands.
 */
public final class NullConsoleCommandHandler implements IConsoleCommandHandler {

	public void handleCommand(StackdSession session, String command, String[] args) {
	}

}
