/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.wrapper;

import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;
import org.lwjgl.opengl.GL11;

/**
 * This element uses the size determined from its surroundings, ignoring the size of its wrapped element. The wrapped
 * element gets requested the same size (or alternatively, a specified fixed size), but its resulting actual size is
 * ignored. The wrapped element can be shifted around relative to the peephole using an (x, y) displacement, with
 * (0, 0) meaning that the top-left corners of both elements match.
 * <p>
 * TODO current clipping state should be handled by the GL worker system to allow different work units to cooperate
 */
public final class Peephole extends AbstractWrapperElement {

	private int innerWidthRequest = -1;
	private int innerHeightRequest = -1;
	private int displacementX = 0;
	private int displacementY = 0;
	private boolean clip = true;

	private static final class WorkUnitSharedState {
		// these variables are solely used by the OpenGL thread
		int previousClipX;
		int previousClipY;
		int previousClipW;
		int previousClipH;
	}
	private final WorkUnitSharedState workUnitSharedState = new WorkUnitSharedState();

	private static final class MyPreWorkUnit extends GlWorkUnit {

		private final WorkUnitSharedState workUnitSharedState;
		private final int x;
		private final int y;
		private final int w;
		private final int h;

		public MyPreWorkUnit(WorkUnitSharedState workUnitSharedState, int x, int y, int w, int h) {
			this.workUnitSharedState = workUnitSharedState;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		@Override
		public void execute() {
			workUnitSharedState.previousClipX = currentClipX;
			workUnitSharedState.previousClipY = currentClipY;
			workUnitSharedState.previousClipW = currentClipW;
			workUnitSharedState.previousClipH = currentClipH;
			currentClipX = getGui().unitsToPixelsInt(getAbsoluteX());
			currentClipY = getGui().unitsToPixelsInt(Gui.HEIGHT_UNITS - getAbsoluteY() - getHeight());
			currentClipW = getGui().unitsToPixelsInt(getWidth());
			currentClipH = getGui().unitsToPixelsInt(getHeight());
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GL11.glScissor(currentClipX, currentClipY, currentClipW, currentClipH);
		}
	};

	private static final class MyPostWorkUnit extends GlWorkUnit {

		private final WorkUnitSharedState workUnitSharedState;
		private final int x;
		private final int y;
		private final int w;
		private final int h;

		public MyPostWorkUnit(WorkUnitSharedState workUnitSharedState, int x, int y, int w, int h) {
			this.workUnitSharedState = workUnitSharedState;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		@Override
		public void execute() {
			currentClipX = previousClipX;
			currentClipY = previousClipY;
			currentClipW = previousClipW;
			currentClipH = previousClipH;
			if (currentClipX < 0) {
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
			} else {
				GL11.glScissor(currentClipX, currentClipY, currentClipW, currentClipH);
			}
		}
	};

	public Peephole() {
	}

	public Peephole(GuiElement wrappedElement) {
		super(wrappedElement);
	}

	public int getInnerWidthRequest() {
		return innerWidthRequest;
	}

	public void setInnerWidthRequest(int innerWidthRequest) {
		this.innerWidthRequest = innerWidthRequest;
		requestLayout();
		invalidateCachedWorkUnits();
	}

	public int getInnerHeightRequest() {
		return innerHeightRequest;
	}

	public void setInnerHeightRequest(int innerHeightRequest) {
		this.innerHeightRequest = innerHeightRequest;
		requestLayout();
		invalidateCachedWorkUnits();
	}

	public int getDisplacementX() {
		return displacementX;
	}

	public void setDisplacementX(int displacementX) {
		this.displacementX = displacementX;
		requestLayout();
		invalidateCachedWorkUnits();
	}

	public int getDisplacementY() {
		return displacementY;
	}

	public void setDisplacementY(int displacementY) {
		this.displacementY = displacementY;
		requestLayout();
		invalidateCachedWorkUnits();
	}

	public void setDisplacement(int displacementX, int displacementY) {
		this.displacementX = displacementX;
		this.displacementY = displacementY;
		requestLayout();
		invalidateCachedWorkUnits();
	}

	public boolean isClip() {
		return clip;
	}

	public void setClip(boolean clip) {
		this.clip = clip;
		invalidateCachedWorkUnits();
	}

	@Override
	public void requestSize(final int width, final int height) {
		invalidateCachedWorkUnits();
		final GuiElement wrappedElement = getWrappedElement();
		wrappedElement.requestSize(innerWidthRequest < 0 ? width : innerWidthRequest, innerHeightRequest < 0 ? height : innerHeightRequest);
		setSize(width, height);
	}

	@Override
	protected void onAbsolutePositionChanged(final int absoluteX, final int absoluteY) {
		invalidateCachedWorkUnits();
		getWrappedElement().setAbsolutePosition(absoluteX + displacementX, absoluteY + displacementY);
	}

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
		if (clip) {
			super.handleLogicFrame(GuiLogicFrameContext.from(context, isMouseInside(context)));
		} else {
			super.handleLogicFrame(context);
		}
	}

	@Override
	protected GlWorkUnit createPreWorkUnit() {
		return clip
				? new MyPreWorkUnit(workUnitSharedState, getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight())
				: GlWorkUnit.NOP_WORK_UNIT;
	}

	@Override
	protected GlWorkUnit createPostWorkUnit() {
		return clip
				? new MyPostWorkUnit(workUnitSharedState, getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight())
				: GlWorkUnit.NOP_WORK_UNIT;
	}

}
