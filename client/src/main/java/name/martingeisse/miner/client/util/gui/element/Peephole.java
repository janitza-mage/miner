/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element;

import name.martingeisse.miner.client.util.gui.GuiElement;

/**
 * This element uses the size determined from its surroundings, ignoring the size
 * of its wrapped element. The wrapped element gets requested the same size, but its
 * resulting actual size is ignored. The wrapped element can be shifted around
 * relative to the peephole using an (x, y) displacement, with (0, 0) meaning
 * that the top-left corners of both elements match.
 *
 * TODO clipping
 */
public final class Peephole extends AbstractWrapperElement {

	private int displacementX = 0;
	private int displacementY = 0;

	public Peephole() {
	}

	public Peephole(GuiElement wrappedElement) {
		super(wrappedElement);
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

	@Override
	public void requestSize(final int width, final int height) {
		requireWrappedElement();
		final GuiElement wrappedElement = getWrappedElement();
		wrappedElement.requestSize(width, height);
		setSize(width, height);
	}

	@Override
	protected void setChildrenLayoutPosition(final int absoluteX, final int absoluteY) {
		requireWrappedElement();
		getWrappedElement().setAbsolutePosition(absoluteX + displacementX, absoluteY + displacementY);
	}

}
