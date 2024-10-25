/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.wrapper;

import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.util.Color;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * A CSS-like combination of margin, border and padding. Unlike CSS boxes, the box content takes part in sizing the
 * box.
 */
public final class Box extends AbstractWrapperElement {

	private final MutableSides margin = new MutableSides();
	private final MutableSides border = new MutableSides();
	private final MutableSides padding = new MutableSides();
	private Color borderColor = Color.BLACK;
	private Color backgroundColor = null;

	private static final class MyPreWorkUnit extends GlWorkUnit {

		private final int x;
		private final int y;
		private final int w;
		private final int h;
		private final Sides margin;
		private final Sides border;
		private final Color borderColor;
		private final Color backgroundColor;

		public MyPreWorkUnit(int x, int y, int w, int h, Sides margin, Sides border, Color borderColor, Color backgroundColor) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.margin = margin;
			this.border = border;
			this.borderColor = borderColor;
			this.backgroundColor = backgroundColor;
		}

		@Override
		public void execute() {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			// draw border
			if (borderColor != null && border.hasAny()) {
				int x1a = x + margin.left();
				int x1b = x1a + border.left();
				int y1a = y + margin.top();
				int y1b = y1a + border.top();
				int x2a = x + w - margin.right();
				int x2b = x2a - border.right();
				int y2a = y + h - margin.bottom();
				int y2b = y2a - border.bottom();

				borderColor.glColor();
				GL11.glBegin(GL11.GL_TRIANGLES);
				if (border.top() > 0) {
					quad(x1a, y1a, x2a, y1a, x2b, y1b, x1b, y1b);
				}
				if (border.left() > 0) {
					quad(x1a, y1a, x1b, y1b, x1b, y2b, x1a, y2a);
				}
				if (border.right() > 0) {
					quad(x2a, y1a, x2a, y2a, x2b, y2b, x2b, y1b);
				}
				if (border.bottom() > 0) {
					quad(x1a, y2a, x1b, y2b, x2b, y2b, x2a, y2a);
				}
				GL11.glEnd();
			}

			// draw background
			if (backgroundColor != null) {
				backgroundColor.glColor();
				GL11.glBegin(GL11.GL_TRIANGLES);
				rectangle(
					x + margin.left() + border.left(),
					y + margin.top() + border.top(),
					x + w - margin.right() - border.right(),
					y + h - margin.bottom() - border.bottom()
				);
				GL11.glEnd();
			}

		}

		private void rectangle(int x1, int y1, int x2, int y2) {
			quad(x1, y1, x2, y1, x2, y2, x1, y2);
		}

		private void quad(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
			triangle(x1, y1, x2, y2, x3, y3);
			triangle(x1, y1, x3, y3, x4, y4);
		}

		private void triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
			GL11.glVertex2i(x1, y1);
			GL11.glVertex2i(x2, y2);
			GL11.glVertex2i(x3, y3);
		}

	}

	/**
	 * Constructor.
	 */
	public Box() {
		this(null);
	}

	/**
	 * Constructor.
	 * @param wrappedElement the wrapped element
	 */
	public Box(GuiElement wrappedElement) {
		super(wrappedElement);
	}

	public MutableSides getMargin() {
		return margin;
	}

	public MutableSides getBorder() {
		return border;
	}

	public MutableSides getPadding() {
		return padding;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		invalidateCachedWorkUnits();
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		invalidateCachedWorkUnits();
	}

	@Override
	public void requestSize(int width, int height) {
		invalidateCachedWorkUnits();
		int horizontalExtra = margin.getLeft() + margin.getRight() + border.getLeft() + border.getRight() + padding.getLeft() + padding.getRight();
		int verticalExtra = margin.getTop() + margin.getBottom() + border.getTop() + border.getBottom() + padding.getTop() + padding.getBottom();
		int remainingWidth = width - horizontalExtra;
		int remainingHeight = height - verticalExtra;
		GuiElement wrappedElement = getWrappedElement();
		wrappedElement.requestSize(Math.max(remainingWidth, 0), Math.max(remainingHeight, 0));
		setSize(wrappedElement.getWidth() + horizontalExtra, wrappedElement.getHeight() + verticalExtra);
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		invalidateCachedWorkUnits();
		getWrappedElement().setAbsolutePosition(
			absoluteX + margin.getLeft() + border.getLeft() + padding.getLeft(),
			absoluteY + margin.getTop() + border.getTop() + padding.getTop()
		);
	}

	@Override
	protected GlWorkUnit createPreWorkUnit() {
		return new MyPreWorkUnit(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(),
				margin.getImmutable(), border.getImmutable(), borderColor, backgroundColor);
	}

	public final class MutableSides {

		private int top, bottom, left, right;

		public int getTop() {
			return top;
		}

		public void setTop(int top) {
			this.top = top;
			onChange();
		}

		public int getBottom() {
			return bottom;
		}

		public void setBottom(int bottom) {
			this.bottom = bottom;
			onChange();
		}

		public int getLeft() {
			return left;
		}

		public void setLeft(int left) {
			this.left = left;
			onChange();
		}

		public int getRight() {
			return right;
		}

		public void setRight(int right) {
			this.right = right;
			onChange();
		}

		public void setVertical(int vertical) {
			this.top = vertical;
			this.bottom = vertical;
			onChange();
		}

		public void setHorizontal(int horizontal) {
			this.left = horizontal;
			this.right = horizontal;
			onChange();
		}

		public void set(int size) {
			this.top = size;
			this.bottom = size;
			this.left = size;
			this.right = size;
			onChange();
		}

		public void set(int vertical, int horizontal) {
			this.top = vertical;
			this.bottom = vertical;
			this.left = horizontal;
			this.right = horizontal;
			onChange();
		}

		public void set(int top, int horizontal, int bottom) {
			this.top = top;
			this.bottom = bottom;
			this.left = horizontal;
			this.right = horizontal;
			onChange();
		}

		public void set(int top, int right, int bottom, int left) {
			this.top = top;
			this.right = right;
			this.bottom = bottom;
			this.left = left;
			onChange();
		}

		public boolean hasAny() {
			return top > 0 || bottom > 0 || left > 0 || right > 0;
		}

		private void onChange() {
			invalidateCachedWorkUnits();
			requestLayout();
		}

		public Sides getImmutable() {
			return new Sides(top, right, bottom, left);
		}

	}

	public record Sides(int top, int right, int bottom, int left) {

		public boolean hasAny() {
			return top > 0 || bottom > 0 || left > 0 || right > 0;
		}

	}

}
