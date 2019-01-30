/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.tools;

import name.martingeisse.launcher.assets.LauncherAssets;
import name.martingeisse.miner.client.util.frame.FrameLoop;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;
import name.martingeisse.miner.client.util.gui.GuiFrameHandler;
import name.martingeisse.miner.client.util.lwjgl.FixedWidthFont;
import name.martingeisse.miner.client.util.lwjgl.LwjglNativeLibraryHelper;
import name.martingeisse.miner.client.util.lwjgl.ResourceLoader;
import name.martingeisse.miner.common.task.TaskBarrier;
import name.martingeisse.miner.common.task.TaskSystem;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

/**
 * Test Main.
 */
public class GuiTestMain {

	public static void main(final String[] args) throws Exception {

		// set LWJGL switches
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

		try {

			// initialize task system so we can start up in parallel tasks
			Thread.currentThread().setName("Startup (later OpenGL)");
			TaskSystem.initialize();
			final TaskBarrier barrier = new TaskBarrier(1);

			// create a worker loop -- we need to access this in closures
			GlWorkerLoop glWorkerLoop = new GlWorkerLoop();

			// prepare native libraries
			LwjglNativeLibraryHelper.prepareNativeLibraries();

			// configure the display
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.create(new PixelFormat(0, 24, 0));

			// initialize LWJGL
			Mouse.create();
			Mouse.poll();

			// load images and sounds
			// MinerResources.initializeInstance();

			// build the frame loop
			FrameLoop frameLoop = new FrameLoop(glWorkerLoop);

			// add the start menu as a handler
			GuiFrameHandler startmenuHandler = new GuiFrameHandler();
			startmenuHandler.getGui().setDefaultFont(new FixedWidthFont(ResourceLoader.loadAwtImage(LauncherAssets.class, "font.png"), 8, 16));
			startmenuHandler.getGui().setRootElement(new GuiTestPage());
			frameLoop.getRootHandler().setWrappedHandler(startmenuHandler);

			// run the game logic in a different thread, then run the OpenGL worker in the main thread
			new Thread("Application") {
				@Override
				public void run() {
					frameLoop.executeLoop(null);
					glWorkerLoop.scheduleStop();
				}

				;
			}.start();
			Thread.currentThread().setName("OpenGL");
			glWorkerLoop.workAndWait();

			// clean up
			Display.destroy();

		} catch (final Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
