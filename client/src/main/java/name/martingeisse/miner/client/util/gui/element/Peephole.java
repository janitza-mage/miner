/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element;

import name.martingeisse.miner.client.util.glworker.GlWorkUnit;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiEvent;
import org.lwjgl.opengl.GL11;

/**
 * This element uses the size determined from its surroundings, ignoring the size of its wrapped element. The wrapped
 * element gets requested the same size (or alternatively, a specified fixed size), but its resulting actual size is
 * ignored. The wrapped element can be shifted around relative to the peephole using an (x, y) displacement, with
 * (0, 0) meaning that the top-left corners of both elements match.
 *
 * TODO current clipping state should be handled by the GL worker system to allow different work units to cooperate
 */
public final class Peephole extends AbstractWrapperElement {

	private static int currentClipX = -1;
	private static int currentClipY = -1;
	private static int currentClipW = -1;
	private static int currentClipH = -1;

	private int innerWidthRequest = -1;
	private int innerHeightRequest = -1;
	private int displacementX = 0;
	private int displacementY = 0;
	private boolean clip = true;

	private int previousClipX;
	private int previousClipY;
	private int previousClipW;
	private int previousClipH;

	private GlWorkUnit preClipWorkUnit = new GlWorkUnit() {
		@Override
		public void execute() {
			previousClipX = currentClipX;
			previousClipY = currentClipY;
			previousClipW = currentClipW;
			previousClipH = currentClipH;
			currentClipX = getGui().unitsToPixelsInt(getAbsoluteX());
			currentClipY = getGui().unitsToPixelsInt(getAbsoluteY());
			currentClipW = getGui().unitsToPixelsInt(getWidth());
			currentClipH = getGui().unitsToPixelsInt(getHeight());
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			// TODO might be upside-down
			GL11.glScissor(currentClipX, currentClipY, currentClipW, currentClipH);
		}
	};

	private GlWorkUnit postClipWorkUnit = new GlWorkUnit() {
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
	}

	public int getInnerHeightRequest() {
		return innerHeightRequest;
	}

	public void setInnerHeightRequest(int innerHeightRequest) {
		this.innerHeightRequest = innerHeightRequest;
	}

	public int getDisplacementX() {
		return displacementX;
	}

	public void setDisplacementX(int displacementX) {
		this.displacementX = displacementX;
	}

	public int getDisplacementY() {
		return displacementY;
	}

	public void setDisplacementY(int displacementY) {
		this.displacementY = displacementY;
	}

	public void setDisplacement(int displacementX, int displacementY) {
		this.displacementX = displacementX;
		this.displacementY = displacementY;
	}

	public boolean isClip() {
		return clip;
	}

	public void setClip(boolean clip) {
		this.clip = clip;
	}

	@Override
	public void requestSize(final int width, final int height) {
		requireWrappedElement();
		final GuiElement wrappedElement = getWrappedElement();
		wrappedElement.requestSize(innerWidthRequest < 0 ? width : innerWidthRequest, innerHeightRequest < 0 ? height : innerHeightRequest);
		setSize(width, height);
	}

	@Override
	protected void setChildrenLayoutPosition(final int absoluteX, final int absoluteY) {
		requireWrappedElement();
		getWrappedElement().setAbsolutePosition(absoluteX + displacementX, absoluteY + displacementY);
	}

	@Override
	public void handleEvent(GuiEvent event) {
		if (event == GuiEvent.DRAW && clip) {
			getGui().getGlWorkerLoop().schedule(preClipWorkUnit);
			super.handleEvent(event);
			getGui().getGlWorkerLoop().schedule(postClipWorkUnit);
		} else {
			super.handleEvent(event);
		}
	}

}
