/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.atom;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.engine.graphics.Font;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.util.gui.util.Color;
import name.martingeisse.miner.common.util.contract.ParameterUtil;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL14.glWindowPos2i;

/**
 * This element draws a paragraph of text, breaking the text into lines of the width requested by the enclosing element.
 */
public final class TextParagraph extends GuiElement {

	private Font font;
	private Color color;
	private String text;
	private String[] cachedLines;
	private MyWorkUnit cachedWorkUnit;

	private static final class MyWorkUnit extends GlWorkUnit {

		private final Font font;
		private final Color color;
		private final String[] lines;
		private final int windowPosX;
		private final int windowPosY;

		public MyWorkUnit(Font font, Color color, String[] lines, int windowPosX, int windowPosY) {
			ParameterUtil.ensureNotNull(font, "font");
			ParameterUtil.ensureNotNull(color, "color");
			ParameterUtil.ensureNotNull(lines, "lines");

			this.font = font;
			this.color = color;
			this.lines = lines;
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
			final int lineHeight = font.getCharacterHeight();
			int i = 0;
			for (String line : lines) {
				glWindowPos2i(windowPosX, windowPosY - i * lineHeight);
				font.drawText(line, 1.0f, Font.ALIGN_LEFT, Font.ALIGN_TOP);
				i++;
			}
		}

	}

	/**
	 * Constructor.
	 */
	public TextParagraph() {
		this.font = null;
		this.color = Color.WHITE;
		this.text = "";
	}

	// ----------------------------------------------------------------------------------------------------------------
	// getters / setters
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Getter method for the font.
	 *
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Setter method for the font.
	 *
	 * @param font the font to set
	 * @return this for chaining
	 */
	public TextParagraph setFont(final Font font) {
		ParameterUtil.ensureNotNull(font, "font");

		this.font = font;
		requestLayout();
		cachedLines = null;
		cachedWorkUnit = null;
		return this;
	}

	/**
	 * Getter method for the color.
	 *
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Setter method for the color.
	 *
	 * @param color the color to set
	 * @return this for chaining
	 */
	public TextParagraph setColor(final Color color) {
		ParameterUtil.ensureNotNull(color, "color");

		this.color = color;
		// cached lines are still valid since line-breaking is not affected by color
		cachedWorkUnit = null;
		return this;
	}

	/**
	 * Getter method for the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Setter method for the text.
	 *
	 * @param text the text to set
	 * @return this for chaining
	 */
	public TextParagraph setText(final String text) {
		ParameterUtil.ensureNotNull(text, "text");

		this.text = text;
		requestLayout();
		cachedLines = null;
		cachedWorkUnit = null;
		return this;
	}

	private Font getEffectiveFont() {
		if (font != null) {
			return font;
		}
		var result = getGui().getDefaultFont();
		if (result == null) {
			throw new IllegalStateException("no element font set and no default font set");
		}
		return result;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// cached derived data
	// ----------------------------------------------------------------------------------------------------------------

	private String[] getLines(int width) {
		if (cachedLines == null) {
			Gui gui = getGui();
			Font effectiveFont = getEffectiveFont();
			List<String> lines = new ArrayList<>();
			if (effectiveFont != null && text != null) {
				StringBuilder lineBuilder = new StringBuilder();
				for (String word : StringUtils.split(text.trim())) {
					if (!lineBuilder.isEmpty()) {
						lineBuilder.append(' ');
					}
					int previousCharacterCount = lineBuilder.length();
					lineBuilder.append(word);
					int newSize = gui.pixelsToUnitsInt(effectiveFont.getStringWidth(lineBuilder.toString()));
					if (newSize > width) {
						lineBuilder.setLength(previousCharacterCount);
						lines.add(lineBuilder.toString());
						lineBuilder.setLength(0);
						lineBuilder.append(word);
					}
				}
				if (!lineBuilder.isEmpty()) {
					lines.add(lineBuilder.toString());
				}
			}
			cachedLines = lines.toArray(new String[0]);
		}
		return cachedLines;
	}

	// this may only be called after the absolute position has been set
	private MyWorkUnit getWorkUnit(int width) {
		if (cachedWorkUnit == null) {
			Gui gui = getGui();
			String[] lines = getLines(width);
			int windowPosX = gui.unitsToPixelsInt(getAbsoluteX());
			int windowPosY = getGui().getHeightPixels() - gui.unitsToPixelsInt(getAbsoluteY());
			cachedWorkUnit = new MyWorkUnit(getEffectiveFont(), color, lines, windowPosX, windowPosY);
		}
		return cachedWorkUnit;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// element logic
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void requestSize(final int width, final int height) {
		int lineHeight = getGui().pixelsToUnitsInt(getEffectiveFont().getCharacterHeight());
		String[] lines = getLines(width);
		setSize(width, lineHeight * lines.length);
	}

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
	}

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
		context.schedule(getWorkUnit(getWidth()));
	}

	@Override
	public ImmutableList<GuiElement> getChildren() {
		return ImmutableList.of();
	}

}
