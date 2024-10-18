/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui;

import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.engine.graphics.Font;
import name.martingeisse.miner.client.util.gui.element.fill.NullElement;
import name.martingeisse.miner.client.util.gui.util.GuiScale;
import name.martingeisse.miner.common.util.contract.ParameterUtil;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public final class Gui {

	private final int widthPixels;
	private final int heightPixels;
	private final GuiScale scale;
	private final int widthUnits;
	private GuiElement rootElement;
	private boolean layoutRequested;
	private int timeMilliseconds;
	private Font defaultFont;
	private IFocusableElement focus;
	private final Queue<Consumer<GuiLogicFrameContext>> followupLogicActions;

	private final GlWorkUnit initializeFrameWorkUnit = new GlWorkUnit() {
		@Override
		public void execute() {
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, widthUnits, GuiScale.HEIGHT_UNITS, 0, -1, 1);
		}
	};

	public Gui(int widthPixels, int heightPixels) {
		this.widthPixels = widthPixels;
		this.heightPixels = heightPixels;
		this.scale = new GuiScale(heightPixels);
		this.widthUnits = scale.pixelsToUnitsInt(widthPixels);
		this.rootElement = new NullElement();
		this.followupLogicActions = new LinkedList<>();
	}

	public int getWidthPixels() {
		return widthPixels;
	}

	public int getHeightPixels() {
		return heightPixels;
	}

	/**
	 * Note: there is no getHeightUnits method because the height is fixed at {@link GuiScale#HEIGHT_UNITS}.
	 */
	public int getWidthUnits() {
		return widthUnits;
	}

	public GuiScale getScale() {
		return scale;
	}

	public GuiElement getRootElement() {
		return rootElement;
	}

	/**
	 * Setter method for the rootElement.
	 * @param rootElement the rootElement to set
	 */
	public void setRootElement(final GuiElement rootElement) {
		ParameterUtil.ensureNotNull(rootElement, "rootElement");

		this.rootElement.notifyRemovedFromParent();
		this.rootElement = rootElement;
		this.layoutRequested = true;
		rootElement.notifyUsedAsRootElement(this);
	}

	/**
	 * Requests that elements get laid out before rendering the next time, but will not lay them out immediately
	 * to batch multiple layout requests.
	 */
	public void requestLayout() {
		layoutRequested = true;
	}

	public void handleLogicFrame(GuiLogicFrameContext context) {
		ParameterUtil.ensureNotNull(context, "context");

		timeMilliseconds = (int) System.currentTimeMillis();
		rootElement.handleLogicFrame(context);
	}

	public void handleGraphicsFrame(GraphicsFrameContext context) {
		ParameterUtil.ensureNotNull(context, "context");

		timeMilliseconds = (int) System.currentTimeMillis();
		if (layoutRequested) {
			rootElement.requestSize(widthUnits, GuiScale.HEIGHT_UNITS);
			rootElement.setAbsolutePosition(0, 0);
			layoutRequested = false;
		}
		context.schedule(initializeFrameWorkUnit);
		rootElement.handleGraphicsFrame(context);
	}

	/**
	 * Returns the current time (NOT delta time).
	 */
	public int getTimeMilliseconds() {
		return timeMilliseconds;
	}

	/**
	 * Note: the default font may be null.
	 */
	public Font getDefaultFont() {
		return defaultFont;
	}

	/**
	 * Setting the default font should not be done while the GUI is already being used -- the default font setting
	 * gets copied into GUI elements at undefined times and there is no proper propagation of a change in default
	 * font, so elements would use either the old or new default font with no easily predictable pattern.
	 */
	public void setDefaultFont(Font defaultFont) {
		this.defaultFont = defaultFont;
		requestLayout();
	}

	/**
	 * Note: the return value may be null if no element has focus.
	 */
	public IFocusableElement getFocus() {
		return focus;
	}

	public void setFocus(IFocusableElement focus) {
		if (focus != this.focus) {
			if (this.focus != null) {
				this.focus.notifyFocus(false);
			}
			this.focus = focus;
			if (this.focus != null) {
				this.focus.notifyFocus(true);
			}
		}
	}

	/**
	 * Adds an action to execute at the end of a logic frame.
	 * <p>
	 * For example, a text field that wants to change focus in reaction to the tab key
	 * should do so in a followup logic action. If it changed focus directly, then handling
	 * of the tab key would continue and might reach the newly focused element later on
	 * (depending on element order) and immediately switch focus again.
	 *
	 * @param followupLogicAction the followup logic action to add
	 */
	public void addFollowupLogicAction(Consumer<GuiLogicFrameContext> followupLogicAction) {
		ParameterUtil.ensureNotNull(followupLogicAction, "followupLogicAction");

		followupLogicActions.add(followupLogicAction);
	}

	/**
	 * Executes all pending followup logic actions.
	 */
	public void executeFollowupLogicActions(GuiLogicFrameContext context) {
		ParameterUtil.ensureNotNull(context, "context");

		while (true) {
			Consumer<GuiLogicFrameContext> action = followupLogicActions.poll();
			if (action == null) {
				break;
			}
			action.accept(context);
		}
	}

}
