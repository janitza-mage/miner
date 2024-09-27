package name.martingeisse.miner.client;

import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.startmenu.LoginPage;
import name.martingeisse.miner.client.util.frame.FrameLoop;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;
import name.martingeisse.miner.client.util.gui.GuiFrameHandler;
import name.martingeisse.miner.common.task.TaskSystem;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

/**
 *
 */
public final class ClientStartup {

	private final GlWorkerLoop glWorkerLoop;

	private int screenWidth = 800;
	private int screenHeight = 600;
	private boolean fullscreen = false;
	private FrameLoop frameLoop = null;

	public ClientStartup() {
		TaskSystem.initialize();
		glWorkerLoop = new GlWorkerLoop();
	}

	public void parseCommandLine(String[] args) {
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
	}

	public void openWindow() throws Exception {

		// configure LWJGL
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

		// initialize the display
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
		Display.setDisplayMode(bestMode);
		if (fullscreen) {
			Display.setFullscreen(true);
		}
		Display.create(new PixelFormat(0, 24, 0));

		// initialize keyboard
		Keyboard.create();
		Keyboard.poll();

		// initialize mouse
		Mouse.create();
		Mouse.poll();

		// load resources
		MinerResources.initializeInstance();

		// build the frame loop
		frameLoop = new FrameLoop(glWorkerLoop);

		// add the start menu as a handler
		GuiFrameHandler startmenuHandler = new GuiFrameHandler();
		startmenuHandler.getGui().setDefaultFont(MinerResources.getInstance().getFont());
		startmenuHandler.getGui().setRootElement(new LoginPage());
		frameLoop.getRootHandler().setWrappedHandler(startmenuHandler);

	}

	public FrameLoop getFrameLoop() {
		return frameLoop;
	}

	public void startConnectingToServer() {
		ClientEndpoint.INSTANCE.connect();
	}

	public void createApplicationThread() {
		new Thread(() -> {
			frameLoop.executeLoop(null);
			glWorkerLoop.scheduleStop();
		}, "Application").start();
	}

	public void becomeGlWorkerThread() throws InterruptedException {
		Thread.currentThread().setName("OpenGL");
		glWorkerLoop.workAndWait();
	}

}
