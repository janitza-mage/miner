/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine.gui.element.wrapper;

import name.martingeisse.gleng.GlWorkUnit;
import name.martingeisse.miner.client.engine.gui.GuiElement;
import name.martingeisse.miner.client.engine.gui.element.fill.NullElement;
import name.martingeisse.miner.client.engine.gui.util.Color;
import name.martingeisse.miner.common.util.contract.ParameterUtil;
import org.lwjgl.opengl.GL11;

/**
 * Adds a border around an element that displaces that element and takes up space itself during layout.
 */
public final class Border extends AbstractWrapperElement {

	private Color color;
	private int thickness;

	private static final class MyWorkUnit extends GlWorkUnit {

		private final int x;
		private final int y;
		private final int w;
		private final int h;
		private final Color color;
		private final int thickness;

		public MyWorkUnit(int x, int y, int w, int h, Color color, int thickness) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.color = color;
			this.thickness = thickness;
		}

		@Override
		protected void gl__Execute() {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			color.glColor();
			GL11.glBegin(GL11.GL_QUADS);
			quad(x, y, x + w, y + thickness); // top
			quad(x, y + h - thickness, x + w, y + h); // bottom
			quad(x, y, x + thickness, y + h); // left
			quad(x + w - thickness, y, x + w, y + h); // right
			GL11.glEnd();
		}

		private void quad(int x1, int y1, int x2, int y2) {
			GL11.glVertex2i(x1, y1);
			GL11.glVertex2i(x2, y1);
			GL11.glVertex2i(x2, y2);
			GL11.glVertex2i(x1, y2);
		}
	}

	/**
	 * Constructor.
	 */
	public Border() {
		this(NullElement.instance);
	}

	/**
	 * Constructor.
	 * @param wrappedElement the wrapped element
	 */
	public Border(GuiElement wrappedElement) {
		super(wrappedElement);
		this.color = Color.WHITE;
		this.thickness = 1;
	}

	/**
	 * Getter method for the color.
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Setter method for the color.
	 * @param color the color to set
	 * @return this for chaining
	 */
	public Border setColor(Color color) {
		ParameterUtil.ensureNotNull(color, "color");
		this.color = color;
		invalidateCachedWorkUnits();
		return this;
	}

	/**
	 * Getter method for the thickness.
	 * @return the thickness
	 */
	public int getThickness() {
		return thickness;
	}

	/**
	 * Setter method for the thickness.
	 * @param thickness the thickness to set
	 * @return this for chaining
	 */
	public Border setThickness(int thickness) {
		this.thickness = thickness;
		requestLayout();
		invalidateCachedWorkUnits();
		return this;
	}

	@Override
	public void requestSize(int width, int height) {
		int borderSpace = 2 * thickness;
		getWrappedElement().requestSize(width - borderSpace, height - borderSpace);
		setSize(getWrappedElement().getWidth() + borderSpace, getWrappedElement().getHeight() + borderSpace);
		invalidateCachedWorkUnits();
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		getWrappedElement().setAbsolutePosition(absoluteX + thickness, absoluteY + thickness);
		invalidateCachedWorkUnits();
	}

	@Override
	protected GlWorkUnit createPostWorkUnit() {
		return new MyWorkUnit(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), color, thickness);
	}
}
