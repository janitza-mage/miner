/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client;

import name.martingeisse.miner.client.util.frame.FrameLoop;

/**
 * Test Main.
 */
public class Main {

	public static FrameLoop frameLoop;

	public static void main(final String[] args) throws Exception {
		Thread.currentThread().setName("Startup (later OpenGL)");
		ClientStartup startup = new ClientStartup();
		startup.parseCommandLine(args);
		startup.openWindow();
		frameLoop = startup.getFrameLoop();
		startup.startConnectingToServer();
		startup.createApplicationThread();
		startup.becomeGlWorkerThread();
		System.exit(0);
	}

}
