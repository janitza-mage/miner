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
import name.martingeisse.miner.common.util.contract.ParameterUtil;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public final class Gui {

	/**
	 * Total height of the screen in GUI units. This value is fixed to be resolution-independent, and the width is
	 * determined from the aspect ratio.
	 */
	public static final int HEIGHT_UNITS = 100000;

	/**
	 * The "normal" grid to align things. The total height is 100 grid clicks.
	 */
	public static final int GRID = 1000;

	/**
	 * The "mini" grid to align things. The total height is 1000 mini-grid clicks.
	 */
	public static final int MINI_GRID = 100;

	private final int widthPixels;
	private final int heightPixels;
	private final int widthUnits;
	private GuiElement rootElement;
	private boolean layoutRequested;
	private int timeMilliseconds;
	private Font defaultFont;
	private IFocusableElement focus;
	private final Queue<Consumer<GuiLogicFrameContext>> followupLogicActions;
	private final Queue<Consumer<GraphicsFrameContext>> followupOpenglActions;

	private final GlWorkUnit initializeFrameWorkUnit = new GlWorkUnit() {
		@Override
		public void execute() {
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, widthUnits, HEIGHT_UNITS, 0, -1, 1);
		}
	};

	public Gui(int widthPixels, int heightPixels) {
		this.widthPixels = widthPixels;
		this.heightPixels = heightPixels;
		this.widthUnits = pixelsToUnitsInt(widthPixels);
		this.rootElement = new NullElement();
		this.followupLogicActions = new LinkedList<>();
		this.followupOpenglActions = new LinkedList<>();
	}

	public int getWidthPixels() {
		return widthPixels;
	}

	public int getHeightPixels() {
		return heightPixels;
	}

	/**
	 * Note: there is no getHeightUnits method because the height is fixed at {@link #HEIGHT_UNITS}.
	 */
	public int getWidthUnits() {
		return widthUnits;
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

	public void handleInput(GuiLogicFrameContext context) {
		ParameterUtil.ensureNotNull(context, "context");

		timeMilliseconds = (int) System.currentTimeMillis();
		rootElement.handleInput(context);
	}

	public void draw(GraphicsFrameContext context) {
		ParameterUtil.ensureNotNull(context, "context");

		timeMilliseconds = (int) System.currentTimeMillis();
		if (layoutRequested) {
			rootElement.requestSize(widthUnits, HEIGHT_UNITS);
			rootElement.setAbsolutePosition(0, 0);
			layoutRequested = false;
		}
		context.schedule(initializeFrameWorkUnit);
		rootElement.draw(context);
	}

	/**
	 * Converts coordinate units to pixels.
	 */
	public int unitsToPixelsInt(int units) {
		return (int) (units * (long) heightPixels / HEIGHT_UNITS);
	}

	/**
	 * Converts pixels to coordinate units.
	 */
	public int pixelsToUnitsInt(int pixels) {
		return (int) (pixels * (long) HEIGHT_UNITS / heightPixels);
	}

	/**
	 * Converts coordinate units to pixels.
	 */
	public float unitsToPixelsFloat(float units) {
		return units * heightPixels / HEIGHT_UNITS;
	}

	/**
	 * Converts pixels to coordinate units.
	 */
	public float pixelsToUnitsFloat(float pixels) {
		return pixels * HEIGHT_UNITS / heightPixels;
	}

	/**
	 * Converts coordinate units to pixels.
	 */
	public double unitsToPixelsDouble(double units) {
		return units * heightPixels / HEIGHT_UNITS;
	}

	/**
	 * Converts pixels to coordinate units.
	 */
	public double pixelsToUnitsDouble(double pixels) {
		return pixels * HEIGHT_UNITS / heightPixels;
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
	 * Adds an action to execute as part of the "followup logic action loop".
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
	 * Executes all pending followup logic actions. This should generally be done after
	 * passing input events to the GUI.
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

	/**
	 * Adds an action to execute as part of the "followup OpenGL action loop". This is
	 * similar to followup logic actions, except they're executed in the OpenGL
	 * worker thread.
	 * <p>
	 * Followup OpenGL actions are needed for special cases during the interaction
	 * between GUI and the in-game OpenGL code. This comment cannot describe a
	 * typical use case because there is none.
	 *
	 * @param followupOpenglAction the followup OpenGL action to add
	 */
	public void addFollowupOpenglAction(Consumer<GraphicsFrameContext> followupOpenglAction) {
		ParameterUtil.ensureNotNull(followupOpenglAction, "followupOpenglAction");

		followupOpenglActions.add(followupOpenglAction);
	}

	/**
	 * Executes all pending followup OpenGL actions. This should generally be done after
	 * firing the DRAW event, from within the OpenGL thread.
	 */
	public void executeFollowupOpenglActions(GraphicsFrameContext context) {
		ParameterUtil.ensureNotNull(context, "context");

		while (true) {
			Consumer<GraphicsFrameContext> action = followupOpenglActions.poll();
			if (action == null) {
				break;
			}
			action.accept(context);
		}
	}

}
