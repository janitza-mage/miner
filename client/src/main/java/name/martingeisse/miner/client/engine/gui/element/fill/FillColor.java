/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.fill;

import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.util.gui.util.Color;
import name.martingeisse.miner.common.util.contract.ParameterUtil;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * This element fills its area with an RGBA color.
 */
public final class FillColor extends AbstractFillElement {

	private Color color;

	private static final class MyWorkUnit extends GlWorkUnit {

		private final int x;
		private final int y;
		private final int w;
		private final int h;
		private final Color color;

		public MyWorkUnit(int x, int y, int w, int h, Color color) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.color = color;
		}

		@Override
		public void execute() {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			color.glColor();
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + w, y);
			GL11.glVertex2i(x + w, y + h);
			GL11.glVertex2i(x, y + h);
			GL11.glEnd();
		}
	}

	/**
	 * Constructor.
	 * @param color the color to fill with
	 */
	public FillColor(Color color) {
		ParameterUtil.ensureNotNull(color, "color");
		setColor(color);
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
	public FillColor setColor(Color color) {
		ParameterUtil.ensureNotNull(color, "color");
		this.color = color;
		invalidateWorkUnit();
		return this;
	}

	@Override
	protected GlWorkUnit createWorkUnit() {
		return new MyWorkUnit(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), color);
	}

}
