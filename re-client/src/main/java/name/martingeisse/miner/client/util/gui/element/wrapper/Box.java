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
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * A CSS-like combination of margin, border and padding. Unlike CSS boxes, the box content takes part in sizing the
 * box.
 */
public final class Box extends AbstractWrapperElement {

	private final Sides margin = new Sides(this::requestLayout);
	private final Sides border = new Sides(this::requestLayout);
	private final Sides padding = new Sides(this::requestLayout);
	private volatile Color borderColor = Color.BLACK;
	private volatile Color backgroundColor = null;

	private final GlWorkUnit workUnit = new GlWorkUnit() {

		@Override
		public void execute() {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			// make sure those won't get nulled in the middle of this method
			var borderColor = Box.this.borderColor;
			var backgroundColor = Box.this.backgroundColor;

			// draw border
			if (borderColor != null && border.hasAny()) {
				int x1a = getAbsoluteX() + margin.getLeft();
				int x1b = x1a + border.getLeft();
				int y1a = getAbsoluteY() + margin.getTop();
				int y1b = y1a + border.getTop();
				int x2a = getAbsoluteX() + getWidth() - margin.getRight();
				int x2b = x2a - border.getRight();
				int y2a = getAbsoluteY() + getHeight() - margin.getBottom();
				int y2b = y2a - border.getBottom();

				borderColor.glColor();
				GL11.glBegin(GL11.GL_TRIANGLES);
				if (border.getTop() > 0) {
					quad(x1a, y1a, x2a, y1a, x2b, y1b, x1b, y1b);
				}
				if (border.getLeft() > 0) {
					quad(x1a, y1a, x1b, y1b, x1b, y2b, x1a, y2a);
				}
				if (border.getRight() > 0) {
					quad(x2a, y1a, x2a, y2a, x2b, y2b, x2b, y1b);
				}
				if (border.getBottom() > 0) {
					quad(x1a, y2a, x1b, y2b, x2b, y2b, x2a, y2a);
				}
				GL11.glEnd();
			}

			// draw background
			if (backgroundColor != null) {
				int x = getAbsoluteX(), y = getAbsoluteY(), w = getWidth(), h = getHeight();
				backgroundColor.glColor();
				GL11.glBegin(GL11.GL_TRIANGLES);
				rectangle(
					x + margin.getLeft() + border.getLeft(),
					y + margin.getTop() + border.getTop(),
					x + w - margin.getRight() - border.getRight(),
					y + h - margin.getBottom() - border.getBottom()
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

	};

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

	public Sides getMargin() {
		return margin;
	}

	public Sides getBorder() {
		return border;
	}

	public Sides getPadding() {
		return padding;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public void handleGraphicsFrame(GraphicsFrameContext context) {
		context.schedule(workUnit);
		super.handleGraphicsFrame(context);
	}

	@Override
	public void requestSize(int width, int height) {
		int horizontalExtra = margin.getLeft() + margin.getRight() + border.getLeft() + border.getRight() + padding.getLeft() + padding.getRight();
		int verticalExtra = margin.getTop() + margin.getBottom() + border.getTop() + border.getBottom() + padding.getTop() + padding.getBottom();
		int remainingWidth = width - horizontalExtra;
		int remainingHeight = height - verticalExtra;
		GuiElement wrappedElement = requireWrappedElement();
		wrappedElement.requestSize(Math.max(remainingWidth, 0), Math.max(remainingHeight, 0));
		setSize(wrappedElement.getWidth() + horizontalExtra, wrappedElement.getHeight() + verticalExtra);
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		requireWrappedElement().setAbsolutePosition(
			absoluteX + margin.getLeft() + border.getLeft() + padding.getLeft(),
			absoluteY + margin.getTop() + border.getTop() + padding.getTop()
		);
	}

	public static final class Sides {

		private volatile int top, bottom, left, right;
		private final Runnable changeCallback;

		Sides(Runnable changeCallback) {
			this.changeCallback = changeCallback;
		}

		public int getTop() {
			return top;
		}

		public void setTop(int top) {
			this.top = top;
			changeCallback.run();
		}

		public int getBottom() {
			return bottom;
		}

		public void setBottom(int bottom) {
			this.bottom = bottom;
			changeCallback.run();
		}

		public int getLeft() {
			return left;
		}

		public void setLeft(int left) {
			this.left = left;
			changeCallback.run();
		}

		public int getRight() {
			return right;
		}

		public void setRight(int right) {
			this.right = right;
			changeCallback.run();
		}

		public void setVertical(int vertical) {
			this.top = vertical;
			this.bottom = vertical;
			changeCallback.run();
		}

		public void setHorizontal(int horizontal) {
			this.left = horizontal;
			this.right = horizontal;
			changeCallback.run();
		}

		public void set(int size) {
			this.top = size;
			this.bottom = size;
			this.left = size;
			this.right = size;
			changeCallback.run();
		}

		public void set(int vertical, int horizontal) {
			this.top = vertical;
			this.bottom = vertical;
			this.left = horizontal;
			this.right = horizontal;
			changeCallback.run();
		}

		public void set(int top, int horizontal, int bottom) {
			this.top = top;
			this.bottom = bottom;
			this.left = horizontal;
			this.right = horizontal;
			changeCallback.run();
		}

		public void set(int top, int left, int right, int bottom) {
			this.top = top;
			this.bottom = bottom;
			this.left = left;
			this.right = right;
			changeCallback.run();
		}

		public boolean hasAny() {
			return top > 0 || bottom > 0 || left > 0 || right > 0;
		}

	}

}
