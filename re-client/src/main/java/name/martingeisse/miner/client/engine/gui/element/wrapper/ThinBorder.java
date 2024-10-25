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
 * Adds a border around an element without displacing / shrinking that element. Such borders are "thin" in that they
 * are assumed to be so thin that their thickness does not affect layout, and the border actually gets drawn over the
 * wrapped element.
 */
public final class ThinBorder extends AbstractWrapperElement {

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
			GL11.glLineWidth(thickness);
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + w, y);
			GL11.glVertex2i(x + w, y + h);
			GL11.glVertex2i(x, y + h);
			GL11.glVertex2i(x, y);
			GL11.glEnd();
		}
	}

	/**
	 * Constructor.
	 */
	public ThinBorder() {
		this(NullElement.instance);
	}

	/**
	 * Constructor.
	 * @param wrappedElement the wrapped element
	 */
	public ThinBorder(GuiElement wrappedElement) {
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
	public ThinBorder setColor(Color color) {
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
	public ThinBorder setThickness(int thickness) {
		this.thickness = thickness;
		invalidateCachedWorkUnits();
		return this;
	}

	@Override
	public void requestSize(int width, int height) {
		getWrappedElement().requestSize(width, height);
		setSize(getWrappedElement().getWidth(), getWrappedElement().getHeight());
		invalidateCachedWorkUnits();
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		getWrappedElement().setAbsolutePosition(absoluteX, absoluteY);
		invalidateCachedWorkUnits();
	}

	@Override
	protected GlWorkUnit createPostWorkUnit() {
		return new MyWorkUnit(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), color, thickness);
	}

}
