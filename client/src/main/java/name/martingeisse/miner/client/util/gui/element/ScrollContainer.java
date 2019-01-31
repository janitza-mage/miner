package name.martingeisse.miner.client.util.gui.element;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiEvent;
import name.martingeisse.miner.client.util.gui.util.AreaAlignment;
import name.martingeisse.miner.client.util.gui.util.Color;
import org.lwjgl.input.Mouse;

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
	private final GuiElement knob;
	private boolean knobGrabbed = false;

	public ScrollContainer() {
		contentWrapper = new Peephole(new FillColor(Color.RED));
		contentWrapper.notifyNewParent(this);

		knob = new Sizer(new FillColor(Color.WHITE), SCROLL_BAR_WIDTH, SCROLL_KNOB_SIZE);

		OverlayStack scrollBarStack = new OverlayStack();
		scrollBarStack.addElement(new FillColor(new Color(128, 128, 128)));
		scrollBarStack.addElement(knob);
		scrollBarStack.setAlignment(AreaAlignment.TOP_LEFT); // TODO does not work
		scrollBar = new Peephole(scrollBarStack);
		scrollBar.notifyNewParent(this);
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
	protected void setChildrenLayoutPosition(int absoluteX, int absoluteY) {
		contentWrapper.setAbsolutePosition(absoluteX, absoluteY);
		scrollBar.setAbsolutePosition(absoluteX + getWidth() - SCROLL_BAR_WIDTH, absoluteY);
	}

	@Override
	public void handleEvent(GuiEvent event) {
		contentWrapper.handleEvent(event);
		scrollBar.handleEvent(event);
		if (event == GuiEvent.MOUSE_BUTTON_PRESSED && knob.isMouseInside()) {
			knobGrabbed = true;
		}
		if (event == GuiEvent.MOUSE_BUTTON_RELEASED) {
			knobGrabbed = false;
		}
		if (event == GuiEvent.MOUSE_MOVED && knobGrabbed) {
			// TODO should actually measure the displacement compared to the original grabbing position
			int displacement = getGui().getMouseY() - (SCROLL_KNOB_SIZE / 2) - getAbsoluteY();
			if (displacement < 0) {
				displacement = 0;
			}
			if (displacement + SCROLL_KNOB_SIZE > getHeight()) {
				displacement = getHeight() - SCROLL_KNOB_SIZE;
			}
			scrollBar.setDisplacement(0, displacement);
			requestLayout();
		}
	}

}
