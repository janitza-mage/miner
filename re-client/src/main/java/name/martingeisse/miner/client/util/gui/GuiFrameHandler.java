/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui;

import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.engine.LogicFrameContext;
import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.common.util.contract.ParameterUtil;

public final class GuiFrameHandler extends AbstractFrameHandler {

	private final Gui gui;
	private boolean enableGui;

	public GuiFrameHandler(int width, int height) {
		this.gui = new Gui(width, height);
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
	public void handleGraphicsFrame(GraphicsFrameContext context) {
		ParameterUtil.ensureNotNull(context, "context");

		if (enableGui) {
			gui.handleGraphicsFrame(context);
			gui.executeFollowupOpenglActions(context);
		}
	}

	@Override
	public void handleLogicFrame(LogicFrameContext context) {
		ParameterUtil.ensureNotNull(context, "context");

		if (enableGui) {
			var myContext = GuiLogicFrameContext.from(context, false);
			gui.handleLogicFrame(myContext);
			gui.executeFollowupLogicActions(myContext);
		}
	}

}
