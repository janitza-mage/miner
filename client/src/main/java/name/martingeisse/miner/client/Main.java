/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client;

import name.martingeisse.miner.client.ingame.IngameHandler;
import name.martingeisse.miner.client.startmenu.AccountApiClient;
import name.martingeisse.miner.client.startmenu.LoginPage;
import name.martingeisse.miner.client.util.frame.FrameLoop;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;
import name.martingeisse.miner.client.util.gui.GuiFrameHandler;
import name.martingeisse.miner.client.util.lwjgl.LwjglNativeLibraryHelper;
import name.martingeisse.miner.client.util.lwjgl.MouseUtil;
import name.martingeisse.miner.common.task.Task;
import name.martingeisse.miner.common.task.TaskBarrier;
import name.martingeisse.miner.common.task.TaskSystem;
import org.apache.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

/**
 * Test Main.
 */
public class Main {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(Main.class);

	/**
	 * the screenWidth
	 */
	public static int screenWidth = 800;

	/**
	 * the screenHeight
	 */
	public static int screenHeight = 600;

	/**
	 * the frameLoop
	 */
	public static FrameLoop frameLoop;

	/**
	 * @param args ...
	 * @throws Exception ...
	 */
	public static void main(final String[] args) throws Exception {
		logger.info("Miner client started");

		// set LWJGL switches
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

		try {

			// initialize task system so we can start up in parallel tasks
			Thread.currentThread().setName("Startup (later OpenGL)");
			TaskSystem.initialize();
			final TaskBarrier barrier = new TaskBarrier(1);

			// TODO development
			new Task() {
				@Override
				public void run() {
					try {
						autologinRequest("martin", "foobar", 1);
					} catch (Exception e) {
						logger.error("could not auto-login", e);
					}
					barrier.run();
				}
			}.schedule();

			// parse command-line options
			logger.trace("parsing command line options...");
			boolean fullscreen = false;
			for (final String arg : args) {
				if (arg.equals("-fs")) {
					fullscreen = true;
				} else if (arg.equals("-6")) {
					screenWidth = 640;
					screenHeight = 480;
				} else if (arg.equals("-8")) {
					screenWidth = 800;
					screenHeight = 600;
				} else if (arg.equals("-1")) {
					screenWidth = 1024;
					screenHeight = 768;
				} else if (arg.equals("-12")) {
					screenWidth = 1280;
					screenHeight = 720;
				} else if (arg.equals("-16")) {
					screenWidth = 1680;
					screenHeight = 1050;
				}
			}
			logger.trace("command line options parsed");

			// create a worker loop -- we need to access this in closures
			logger.trace("initializing OpenGL worker loop...");
			GlWorkerLoop glWorkerLoop = new GlWorkerLoop();
			logger.trace("OpenGL worker loop initialized");

			// prepare native libraries
			logger.trace("preparing native libraries...");
			LwjglNativeLibraryHelper.prepareNativeLibraries();
			logger.trace("native libraries prepared");

			// configure the display
			logger.trace("finding optimal display mode...");
			DisplayMode bestMode = null;
			int bestModeFrequency = -1;
			for (DisplayMode mode : Display.getAvailableDisplayModes()) {
				if (mode.getWidth() == screenWidth && mode.getHeight() == screenHeight && (mode.isFullscreenCapable() || !fullscreen)) {
					if (mode.getFrequency() > bestModeFrequency) {
						bestMode = mode;
						bestModeFrequency = mode.getFrequency();
					}
				}
			}
			if (bestMode == null) {
				bestMode = new DisplayMode(screenWidth, screenHeight);
			}
			logger.trace("setting intended display mode...");
			Display.setDisplayMode(bestMode);
			if (fullscreen) {
				Display.setFullscreen(true);
			}
			logger.trace("switching display mode...");
			Display.create(new PixelFormat(0, 24, 0));
			logger.trace("display initialized");

			// initialize LWJGL
			logger.trace("preparing mouse...");
			Mouse.create();
			Mouse.poll();
			logger.trace("mouse prepared");

			// load images and sounds
			logger.trace("loading resources...");
			MinerResources.initializeInstance();
			logger.trace("resources loaded...");

			// build the frame loop
			frameLoop = new FrameLoop(glWorkerLoop);

			// add the start menu as a handler
			{
				GuiFrameHandler startmenuHandler = new GuiFrameHandler();
				startmenuHandler.getGui().setDefaultFont(MinerResources.getInstance().getFont());
				startmenuHandler.getGui().setRootElement(new LoginPage());
				frameLoop.getRootHandler().setWrappedHandler(startmenuHandler);
			}

			// TODO remove, used for development
			logger.info("auto-login...");
			barrier.await();
			// autologinComplete();
			logger.info("auto-login successful");

			// run the game logic in a different thread, then run the OpenGL worker in the main thread
			new Thread("Application") {
				@Override
				public void run() {
					frameLoop.executeLoop(null);
					glWorkerLoop.scheduleStop();
				}

				;
			}.start();
			logger.debug("startup thread becoming the OpenGL thread now");
			Thread.currentThread().setName("OpenGL");
			glWorkerLoop.workAndWait();

			// clean up
			Display.destroy();

		} catch (final Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * Helper method for development.
	 */
	private static void autologinRequest(String username, String password, long playerId) throws Exception {
		AccountApiClient.getInstance().login(username, password);
		AccountApiClient.getInstance().accessPlayer(playerId);
	}

	/**
	 * Helper method for development. Must be called from the OpenGL thread.
	 */
	@SuppressWarnings("unused")
	private static void autologinComplete() throws Exception {
		Main.frameLoop.getRootHandler().setWrappedHandler(new IngameHandler());
		MouseUtil.grab();
	}

}
