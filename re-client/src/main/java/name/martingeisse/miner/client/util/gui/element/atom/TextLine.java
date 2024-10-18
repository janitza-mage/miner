/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.atom;

import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.engine.graphics.Font;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.util.gui.util.Color;
import name.martingeisse.miner.client.util.gui.util.LeafElement;
import name.martingeisse.miner.common.util.contract.ParameterUtil;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL14.glWindowPos2i;

/**
 * This element draws a line of text. Its size depends solely on the font and text and is not affected during layout.
 */
public final class TextLine extends LeafElement {

	private Font font;
	private Color color;
	private String text;
	private MyWorkUnit cachedWorkUnit;

	private static final class MyWorkUnit extends GlWorkUnit {

		private final Font font;
		private final Color color;
		private final String text;
		private final int windowPosX;
		private final int windowPosY;

		public MyWorkUnit(Font font, Color color, String text, int windowPosX, int windowPosY) {
			ParameterUtil.ensureNotNull(font, "font");
			ParameterUtil.ensureNotNull(color, "color");
			ParameterUtil.ensureNotNull(text, "text");

			this.font = font;
			this.color = color;
			this.text = text;
			this.windowPosX = windowPosX;
			this.windowPosY = windowPosY;
		}

		@Override
		public void execute() {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glPixelTransferf(GL11.GL_RED_SCALE, 0.0f);
			GL11.glPixelTransferf(GL11.GL_GREEN_SCALE, 0.0f);
			GL11.glPixelTransferf(GL11.GL_BLUE_SCALE, 0.0f);
			GL11.glPixelTransferf(GL11.GL_ALPHA_SCALE, 1.0f);
			GL11.glPixelTransferf(GL11.GL_RED_BIAS, color.getRed() / 255.0f);
			GL11.glPixelTransferf(GL11.GL_GREEN_BIAS, color.getGreen() / 255.0f);
			GL11.glPixelTransferf(GL11.GL_BLUE_BIAS, color.getBlue() / 255.0f);
			GL11.glPixelTransferf(GL11.GL_ALPHA_BIAS, 0.0f);
			// TODO scale font so text doesn't become smaller with higher resolution
			glWindowPos2i(windowPosX, windowPosY);
			font.drawText(text, 1.0f, Font.ALIGN_LEFT, Font.ALIGN_TOP);
		}
	}

	public TextLine() {
		this.font = null;
		this.color = Color.WHITE;
		this.text = "";
	}

	public Font getFont() {
		return font;
	}

	public TextLine setFont(final Font font) {
		ParameterUtil.ensureNotNull(font, "font");

		this.font = font;
		requestLayout();
		cachedWorkUnit = null;
		return this;
	}

	public Color getColor() {
		return color;
	}

	public TextLine setColor(Color color) {
		ParameterUtil.ensureNotNull(color, "color");

		this.color = color;
		cachedWorkUnit = null;
		return this;
	}

	public String getText() {
		return text;
	}

	public TextLine setText(final String text) {
		ParameterUtil.ensureNotNull(text, "text");

		this.text = text;
		requestLayout();
		cachedWorkUnit = null;
		return this;
	}

	public Font getEffectiveFont() {
		if (font == null) {
			final Gui gui = getGuiOrNull();
			return (gui == null ? null : gui.getDefaultFont());
		} else {
			return font;
		}
	}

	@Override
	public void requestSize(final int width, final int height) {
		cachedWorkUnit = null;
		final Font effectiveFont = getEffectiveFont();
		if (effectiveFont == null || text == null) {
			setSize(0, 0);
		} else {
			Gui gui = getGui();
			int textWidth = gui.pixelsToUnitsInt(effectiveFont.getStringWidth(text));
			int textHeight = gui.pixelsToUnitsInt(effectiveFont.getCharacterHeight());
			setSize(textWidth, textHeight);
		}
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		cachedWorkUnit = null;
	}

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
	}

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
		if (cachedWorkUnit == null) {
			Gui gui = getGui();
			int windowPosX = gui.unitsToPixelsInt(getAbsoluteX());
			int windowPosY = getGui().getHeightPixels() - gui.unitsToPixelsInt(getAbsoluteY());
			cachedWorkUnit = new MyWorkUnit(getEffectiveFont(), color, text, windowPosX, windowPosY);
		}
		context.schedule(cachedWorkUnit);
	}

}
