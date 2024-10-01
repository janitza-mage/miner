/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.tools;

import name.martingeisse.miner.client.engine.*;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;

/**
 * Opens a window, lets you hit keys on the keyboard, and prints the
 * LWJGL keyboard code for each.
 */
public final class KeyboardCodePrinter {


	/**
	 * The main method.
	 * @param args ignored
	 */
	public static void main(String[] args) throws Exception {
		EngineUserParameters engineUserParameters = EngineUserParameters.parseCommandLine(args,
				new EngineUserParameters(800, 600, false)
		);
		EngineParameters parameters = new EngineParameters("KeyboardCodePrinter", null, engineUserParameters);
		try (Engine engine = new Engine(parameters, args, new FrameHandler() {

			@Override
			public void handleLogicFrame(LogicFrameContext context) {
				for (int i = 0; i <= GLFW_KEY_LAST; i++) {
					if (context.isKeyNewlyDown(i)) {
						System.out.println(i);
					}
				}
				if (context.isKeyDown(GLFW_KEY_ESCAPE)) {
					context.shutdown();
				}
			}

			@Override
			public void handleGraphicsFrame(GraphicsFrameContext context) {
			}

		})) {
			engine.run();
		}
	}

}
