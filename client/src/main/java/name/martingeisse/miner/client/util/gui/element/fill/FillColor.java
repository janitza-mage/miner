/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.fill;

import name.martingeisse.common.util.ParameterUtil;
import name.martingeisse.miner.client.util.glworker.GlWorkUnit;
import name.martingeisse.miner.client.util.gui.util.Color;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * This element fills its area with an RGBA color.
 */
public final class FillColor extends AbstractFillElement {

	private Color color;

	private final GlWorkUnit workUnit = new GlWorkUnit() {
		@Override
		public void execute() {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			color.glColor();
			int x = getAbsoluteX(), y = getAbsoluteY(), w = getWidth(), h = getHeight();
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + w, y);
			GL11.glVertex2i(x + w, y + h);
			GL11.glVertex2i(x, y + h);
			GL11.glEnd();
		}
	};

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
		return this;
	}

	@Override
	protected void draw() {
		getGui().getGlWorkerLoop().schedule(workUnit);
	}
	
}
