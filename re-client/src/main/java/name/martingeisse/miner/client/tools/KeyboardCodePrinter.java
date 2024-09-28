/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.tools;

import name.martingeisse.miner.client.util.Keyboard;
import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.client.util.frame.BreakFrameLoopException;
import name.martingeisse.miner.client.util.frame.FrameLoop;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

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

		// initialize
		if (!glfwInit()) {
			throw new RuntimeException("could not initialize GLFW");
		}
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		long window = glfwCreateWindow(800, 600, "KeyboardCodePrinter", NULL, NULL);
		glfwMakeContextCurrent(window);
		createCapabilities();
		Keyboard.installCallback(window);
		Keyboard keyboard = new Keyboard();
		var glWorkerLoop = new GlWorkerLoop();
		var frameLoop = new FrameLoop(window, glWorkerLoop);
		frameLoop.getRootHandler().setWrappedHandler(new AbstractFrameHandler() {
			@Override
			public void handleStep() throws BreakFrameLoopException {
				keyboard.update();
				for (int i = 0; i <= GLFW_KEY_LAST; i++) {
					if (keyboard.isNewlyDown(i)) {
						System.out.println(i);
					}
				}
				if (keyboard.isDown(GLFW_KEY_ESCAPE) || glfwWindowShouldClose(window)) {
					throw new BreakFrameLoopException();
				}
			}
		});
		new Thread(() -> {
			frameLoop.executeLoop(null);
			glWorkerLoop.scheduleStop();
		}, "Application").start();
		Thread.currentThread().setName("OpenGL");
		glWorkerLoop.workAndWait();
		glfwTerminate();
	}

}
