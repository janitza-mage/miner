/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.fill;

import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.util.gui.util.Color;
import name.martingeisse.miner.client.util.gui.util.PulseFunction;
import name.martingeisse.miner.common.util.contract.ParameterUtil;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * This element fills its area with a pulsing RGBA color.
 * <p>
 * This class does not store a pulse amplitude; use the color's
 * alpha value for that.
 */
public final class PulseFillColor extends AbstractFillElement {

	private volatile Color color;
	private volatile PulseFunction pulseFunction;
	private volatile int period;

	private final GlWorkUnit workUnit = new GlWorkUnit() {
		@Override
		public void execute() {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			color.glColorWithCombinedAlpha(pulseFunction.evaluate(getGui().getTimeMilliseconds(), period));
			final int x = getAbsoluteX(), y = getAbsoluteY(), w = getWidth(), h = getHeight();
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
	 */
	public PulseFillColor() {
		this.color = Color.WHITE;
		this.pulseFunction = PulseFunction.ABSOLUTE_SINE;
		this.period = 2000;
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
	public PulseFillColor setColor(final Color color) {
		ParameterUtil.ensureNotNull(color, "color");
		this.color = color;
		return this;
	}

	/**
	 * Getter method for the pulseFunction.
	 * @return the pulseFunction
	 */
	public PulseFunction getPulseFunction() {
		return pulseFunction;
	}

	/**
	 * Setter method for the pulseFunction.
	 * @param pulseFunction the pulseFunction to set
	 * @return this for chaining
	 */
	public PulseFillColor setPulseFunction(final PulseFunction pulseFunction) {
		ParameterUtil.ensureNotNull(pulseFunction, "pulseFunction");
		this.pulseFunction = pulseFunction;
		return this;
	}

	/**
	 * Getter method for the period.
	 * @return the period
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * Setter method for the period.
	 * @param period the period to set
	 * @return this for chaining
	 */
	public PulseFillColor setPeriod(final int period) {
		this.period = period;
		return this;
	}

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
		context.schedule(workUnit);
	}

}
