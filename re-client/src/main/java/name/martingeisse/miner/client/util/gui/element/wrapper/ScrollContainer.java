package name.martingeisse.miner.client.util.gui.element.wrapper;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.util.gui.element.collection.OverlayStack;
import name.martingeisse.miner.client.util.gui.element.fill.FillColor;
import name.martingeisse.miner.client.util.gui.util.Color;
import org.lwjgl.glfw.GLFW;

/**
 * This container always uses the size requested from outside. The same size, minus the space needed for
 * scroll bars, is requested from the wrapped element. Scroll bars are then made visible if the actual
 * size of the wrapped element exceeds that space.
 * <p>
 * Scroll bars can be enabled and disabled individually. For example, of the horizontal scroll bar is disabled,
 * then no space is reserved for it, and no scrolling is possible if the element is wider than its available
 * space. This is typically used when the wrapped element is able to stay within the requested size; in the
 * example, horizontal scrolling would be disabled if the wrapped element is a long text that is able to stay
 * within its width and instead grows vertically by line wrapping.
 * <p>
 * TODO currently this class is hardcoded to vertical scrolling only
 */
public class ScrollContainer extends GuiElement {

	public static final int SCROLL_BAR_WIDTH = 2 * Gui.GRID;
	public static final int SCROLL_KNOB_SIZE = 3 * Gui.GRID;

	private final Peephole contentWrapper;
	private final Peephole scrollBar;
	private final Peephole knobRail;
	private final GuiElement knob;
	private boolean knobGrabbed = false;

	public ScrollContainer() {
		contentWrapper = new Peephole(new FillColor(Color.RED));
		contentWrapper.notifyAddedToParent(this);

		knob = new FillColor(Color.WHITE);
		knobRail = new Peephole(knob);
		knobRail.setInnerHeightRequest(SCROLL_KNOB_SIZE);

		OverlayStack scrollBarStack = new OverlayStack();
		scrollBarStack.addElement(new FillColor(new Color(128, 128, 128)));
		scrollBarStack.addElement(knobRail);
		scrollBar = new Peephole(scrollBarStack);
		scrollBar.notifyAddedToParent(this);
	}

	public ScrollContainer(GuiElement wrappedElement) {
		this();
		setWrappedElement(wrappedElement);
	}

	public GuiElement getWrappedElement() {
		return contentWrapper.getWrappedElement();
	}

	public void setWrappedElement(GuiElement wrappedElement) {
		contentWrapper.setWrappedElement(wrappedElement);
		requestLayout();
	}

	@Override
	public void requestSize(int width, int height) {
		contentWrapper.requestSize(width - SCROLL_BAR_WIDTH, height);
		scrollBar.requestSize(SCROLL_BAR_WIDTH, height);
		setSize(width, height);
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		contentWrapper.setAbsolutePosition(absoluteX, absoluteY);
		scrollBar.setAbsolutePosition(absoluteX + getWidth() - SCROLL_BAR_WIDTH, absoluteY);
	}

	@Override
	public void handleInput(GuiLogicFrameContext context) {
		contentWrapper.handleInput(context);
		scrollBar.handleInput(context);
		if (context.isMouseButtonNewlyDown(GLFW.GLFW_MOUSE_BUTTON_LEFT) && knob.isMouseInside(context)) {
			knobGrabbed = true;
		}
		if (context.isMouseButtonNewlyUp(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			knobGrabbed = false;
		}
		if (knobGrabbed) {
			// TODO should actually measure the displacement compared to the original grabbing position
			int height = getHeight();
			int contentHeight = contentWrapper.getWrappedElement().getHeight();

			// handle knob
			int knobDisplacement = (int)getGui().pixelsToUnitsDouble(context.getMouseY()) - (SCROLL_KNOB_SIZE / 2) - getAbsoluteY();
			if (knobDisplacement < 0) {
				knobDisplacement = 0;
			}
			if (knobDisplacement + SCROLL_KNOB_SIZE > height) {
				knobDisplacement = height - SCROLL_KNOB_SIZE;
			}
			knobRail.setDisplacement(0, knobDisplacement);

			// handle content
			int maxKnobDisplacement = height - SCROLL_KNOB_SIZE;
			int maxContentDisplacement = contentHeight - height;
			if (maxContentDisplacement > 0) {
				// scrollable
				int contentDisplacement = (int) (knobDisplacement * (long) maxContentDisplacement / maxKnobDisplacement);
				contentWrapper.setDisplacement(0, -contentDisplacement);
			} else {
				// content is less than a single page -> not scrollable
				contentWrapper.setDisplacement(0, 0);
			}

			requestLayout();
		}
	}

	@Override
	public ImmutableList<GuiElement> getChildren() {
		return ImmutableList.of(contentWrapper, scrollBar);
	}

	@Override
	public void draw(GraphicsFrameContext context) {
		// nothing to do here -- the parts will draw themselves
	}

}
