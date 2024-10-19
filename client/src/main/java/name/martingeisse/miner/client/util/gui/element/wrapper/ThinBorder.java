/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.wrapper;

import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.util.Color;
import name.martingeisse.miner.common.util.contract.ParameterUtil;
import org.lwjgl.opengl.GL11;

/**
 * Adds a border around an element without displacing / shrinking that
 * element. Such borders are "thin" in that they are assumed to have no
 * thickness during layout. If the border is not actually thin then
 * it will be drawn over both the enclosing and wrapped elements.
 */
public final class ThinBorder extends AbstractWrapperElement {

	private volatile Color color;
	private volatile int thickness;

	private final GlWorkUnit workUnit = new GlWorkUnit() {
		@Override
		public void execute() {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			color.glColor();
			int x = getAbsoluteX(), y = getAbsoluteY(), w = getWidth(), h = getHeight();
			GL11.glLineWidth(thickness);
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + w, y);
			GL11.glVertex2i(x + w, y + h);
			GL11.glVertex2i(x, y + h);
			GL11.glVertex2i(x, y);
			GL11.glEnd();
		}
	};

	/**
	 * Constructor.
	 */
	public ThinBorder() {
		this(null);
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
		return this;
	}

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
		super.handleGraphicsFrame(context);
		context.schedule(workUnit);
	}

	@Override
	public void requestSize(int width, int height) {
		requireWrappedElement();
		getWrappedElement().requestSize(width, height);
		setSize(getWrappedElement().getWidth(), getWrappedElement().getHeight());
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		requireWrappedElement();
		getWrappedElement().setAbsolutePosition(absoluteX, absoluteY);
	}

}
