/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui;

import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.client.util.frame.BreakFrameLoopException;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;
import org.apache.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

/**
 * This handler draws the GUI and sends events to it.
 */
public final class GuiFrameHandler extends AbstractFrameHandler {

	private static Logger logger = Logger.getLogger(GuiFrameHandler.class);

	private final Gui gui;
	private boolean enableGui;

	/**
	 * Constructor.
	 */
	public GuiFrameHandler() {
		this.gui = new Gui(Display.getWidth(), Display.getHeight());
		this.enableGui = true;
	}

	public Gui getGui() {
		return gui;
	}

	public boolean isEnableGui() {
		return enableGui;
	}

	public void setEnableGui(boolean enableGui) {
		this.enableGui = enableGui;
	}

	@Override
	public void draw(final GlWorkerLoop glWorkerLoop) {
		if (enableGui) {
			try {
				gui.setGlWorkerLoop(glWorkerLoop);
				gui.fireEvent(GuiEvent.DRAW);
				gui.executeFollowupOpenglActions();
			} catch (Exception e) {
				logger.error("unexpected exception in GUI (drawing)", e);
			}
		}
	}

	@Override
	public synchronized void handleStep() throws BreakFrameLoopException {
		// TODO why is this method synchronized?
		if (enableGui) {
			try {

				// dispatch keyboard events
				while (Keyboard.next()) {
					gui.fireEvent(Keyboard.getEventKeyState() ? GuiEvent.KEY_PRESSED : GuiEvent.KEY_RELEASED);
				}

				// dispatch mouse events
				while (Mouse.next()) {
					if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0) {
						gui.fireEvent(GuiEvent.MOUSE_MOVED);
					}
					if (Mouse.getEventButton() != -1) {
						gui.fireEvent(Mouse.getEventButtonState() ? GuiEvent.MOUSE_BUTTON_PRESSED : GuiEvent.MOUSE_BUTTON_RELEASED);
					}
				}

				// handle pending followup actions
				gui.executeFollowupLogicActions();

			} catch (BreakFrameLoopException e) {
				throw e;
			} catch (Exception e) {
				logger.error("unexpected exception in GUI (logic)", e);
			}
		}
	}

}
