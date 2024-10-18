/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.tools;

import name.martingeisse.gleng.Gleng;
import name.martingeisse.gleng.GlengCallbacks;
import name.martingeisse.gleng.GlengParameters;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Opens a window, lets you hit keys on the keyboard, and prints the
 * LWJGL keyboard code for each.
 */
public final class KeyboardCodePrinter {

	private static volatile boolean shutdown = false;

	public static void main(String[] args) throws Exception {
		GlengParameters glengParameters = GlengParameters.from("KeyboardCodePrinter", 800, 600, false, args);
		GlengCallbacks glengCallbacks = new GlengCallbacks() {

			@Override
			public void onKeyEvent(int key, int scancode, int action, int mods) {
				if (action == GLFW_PRESS) {
					System.out.println(key);
					if (key == GLFW_KEY_ESCAPE) {
						shutdown = true;
					}
				}
			}

			@Override
			public void onMousePositionEvent(double x, double y) {
			}

			@Override
			public void onMouseButtonEvent(int button, int action, int mods) {
			}

		};
		Gleng.run(glengParameters, glengCallbacks, () -> {
			while (!shutdown) {
				glfwPollEvents();
			}
		});
	}

}
