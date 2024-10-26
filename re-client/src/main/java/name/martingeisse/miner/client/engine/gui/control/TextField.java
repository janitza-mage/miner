/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine.gui.control;

import name.martingeisse.gleng.GlWorkUnit;
import name.martingeisse.gleng.graphics.Font;
import name.martingeisse.miner.client.engine.KeyboardEvent;
import name.martingeisse.miner.client.engine.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.engine.gui.IFocusableElement;
import name.martingeisse.miner.client.engine.gui.element.atom.TextLine;
import name.martingeisse.miner.client.engine.gui.element.collection.OverlayStack;
import name.martingeisse.miner.client.engine.gui.element.fill.AbstractDynamicFillElement;
import name.martingeisse.miner.client.engine.gui.element.fill.FillColor;
import name.martingeisse.miner.client.engine.gui.element.wrapper.Border;
import name.martingeisse.miner.client.engine.gui.element.wrapper.Margin;
import name.martingeisse.miner.client.engine.gui.util.AreaAlignment;
import name.martingeisse.miner.client.engine.gui.util.Color;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/**
 * A text input field.
 */
public final class TextField extends Control implements IFocusableElement {

	private final TextLine textLine;
    private final Border border;
	private String value;
	private char passwordCharacter;
	private IFocusableElement nextFocusableElement;
	private int cursorPosition;

	public TextField() {

		// text line and cursor
		this.textLine = new TextLine();
        OverlayStack innerOverlayStack = new OverlayStack().setAlignment(AreaAlignment.LEFT_CENTER);
		innerOverlayStack.addElement(textLine);
		innerOverlayStack.addElement(new AbstractDynamicFillElement() {
			@Override
			public void handleGraphicsFrame() {
				if (getGui().getFocus() == TextField.this) {
					String textBeforeCursor = textLine.getText().substring(0, cursorPosition);
					Font currentFont = textLine.getEffectiveFont();
					if (currentFont != null) {
						int x = getAbsoluteX() + currentFont.getStringWidth(textBeforeCursor);
						int y1 = getAbsoluteY();
						int y2 = y1 + getHeight();
						new LineWorkUnit(x, y1, y2).schedule();
					}
				}
			}
		});

		// embed into margin
        Margin margin = new Margin(innerOverlayStack, 1);
        OverlayStack outerOverlayStack = new OverlayStack().setAlignment(AreaAlignment.LEFT_CENTER);
		outerOverlayStack.addElement(new FillColor(new Color(0, 0, 64, 255)));
		outerOverlayStack.addElement(margin);

		// add border
		this.border = new Border(outerOverlayStack);

		// initialize fields
		this.value = "";
		this.passwordCharacter = (char) 0;
		setControlRootElement(border);

	}

	public String getValue() {
		return value;
	}

	public TextField setValue(final String value) {
		if (value == null) {
			throw new IllegalArgumentException("value is null");
		}
		this.value = value;
		if (passwordCharacter == 0) {
			textLine.setText(value);
		} else {
            textLine.setText(String.valueOf(passwordCharacter).repeat(value.length()));
		}
		if (cursorPosition > value.length()) {
			cursorPosition = value.length();
		}
		return this;
	}

	/**
	 * Typically the same as getValue(), unless a password character has been set.
	 */
	public String getDisplayedText() {
		return textLine.getText();
	}

	public char getPasswordCharacter() {
		return passwordCharacter;
	}

	public void setPasswordCharacter(final char passwordCharacter) {
		this.passwordCharacter = passwordCharacter;
		setValue(value); // update the displayed text
	}

	public IFocusableElement getNextFocusableElement() {
		return nextFocusableElement;
	}

	public TextField setNextFocusableElement(IFocusableElement nextFocusableElement) {
		this.nextFocusableElement = nextFocusableElement;
		return this;
	}

	public int getCursorPosition() {
		return cursorPosition;
	}

	public TextField setCursorPosition(int cursorPosition) {
		this.cursorPosition = Math.min(Math.max(cursorPosition, 0), value.length());
		return this;
	}

	/**
	 * Moves the cursor to the position after the last character.
	 */
	public TextField moveCursorToEnd() {
		this.cursorPosition = value.length();
		return this;
	}

	@Override
	public void notifyFocus(boolean focused) {
		border.setColor(focused ? new Color(128, 128, 255, 255) : Color.WHITE);
	}

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
		super.handleLogicFrame(context);

		// focus on click
		if (context.isMouseButtonNewlyDown(0) && isMouseInside(context)) {
			getGui().setFocus(this);
			Font currentFont = textLine.getEffectiveFont();
			if (currentFont != null) {
				int relativeX = (int)context.getMouseX() - getAbsoluteX();
				cursorPosition = currentFont.mapPosition(textLine.getText(), relativeX);
			}
		}

		// handle keyboard input
		if (getGui().getFocus() == this) {
			for (KeyboardEvent event : context.getKeyboardEvents()) {
				if (event.type() == KeyboardEvent.Type.KEY_DOWN || event.type() == KeyboardEvent.Type.KEY_REPEAT) {
					int key = event.codeOrCharacter();
					if (key == GLFW.GLFW_KEY_BACKSPACE) {
						if (cursorPosition > 0) {
							int newPosition = cursorPosition - 1;
							String prefix = value.substring(0, cursorPosition - 1);
							String suffix = value.substring(cursorPosition);
							setValue(prefix + suffix);
							cursorPosition = newPosition;
						}
					} else if (key == GLFW.GLFW_KEY_DELETE) {
						if (cursorPosition < value.length()) {
							String prefix = value.substring(0, cursorPosition);
							String suffix = value.substring(cursorPosition + 1);
							setValue(prefix + suffix);
						}
					} else if (key == GLFW.GLFW_KEY_LEFT) {
						if (cursorPosition > 0) {
							cursorPosition--;
						}
					} else if (key == GLFW.GLFW_KEY_RIGHT) {
						if (cursorPosition < value.length()) {
							cursorPosition++;
						}
					} else if (key == GLFW.GLFW_KEY_TAB) {
						if (nextFocusableElement != null) {
							getGui().addFollowupLogicAction(_context -> getGui().setFocus(nextFocusableElement));
						}
					}
				} else if (event.type() == KeyboardEvent.Type.CHARACTER) {
					String prefix = value.substring(0, cursorPosition);
					String suffix = value.substring(cursorPosition);
					setValue(prefix + ((char)event.codeOrCharacter()) + suffix);
					cursorPosition++;
				}
			}
		}
	}

	private static final class LineWorkUnit extends GlWorkUnit {

		private final int x, y1, y2;

		public LineWorkUnit(int x, int y1, int y2) {
			this.x = x;
			this.y1 = y1;
			this.y2 = y2;
		}

		@Override
		protected void gl__Execute() {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			Color.WHITE.glColor();
			GL11.glLineWidth(1);

			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2i(x, y1);
			GL11.glVertex2i(x, y2);
			GL11.glEnd();
		}
	}

}
