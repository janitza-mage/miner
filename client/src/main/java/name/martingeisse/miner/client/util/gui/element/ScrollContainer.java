package name.martingeisse.miner.client.util.gui.element;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiEvent;
import name.martingeisse.miner.client.util.gui.util.Color;

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

	private final GuiElement root;
	private final Peephole contentWrapper;
	private final Peephole scrollBar;

	public ScrollContainer() {

		contentWrapper = new Peephole(new FillColor(Color.RED));
		scrollBar = createScrollBar();

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.addElement(contentWrapper);
		horizontalLayout.addElement(scrollBar);
		root = horizontalLayout;
		root.notifyNewParent(this);

	}

	private static Peephole createScrollBar() {
		OverlayStack stack = new OverlayStack();
		stack.addElement(new FillColor(new Color(128, 128, 128)));
		stack.addElement(new Sizer(new FillColor(Color.WHITE), SCROLL_BAR_WIDTH, SCROLL_KNOB_SIZE));
		return new Peephole(stack);
	}

	public GuiElement getWrappedElement() {
		return contentWrapper.getWrappedElement();
	}

	public void setWrappedElement(GuiElement wrappedElement) {
		contentWrapper.setWrappedElement(wrappedElement);
	}

	@Override
	public void requestSize(int width, int height) {
		contentWrapper.requestSize(width - SCROLL_BAR_WIDTH, height);
		scrollBar.requestSize(SCROLL_BAR_WIDTH, height);
		root.requestSize(width, height);
		setSize(width, height);
	}

	@Override
	protected void setChildrenLayoutPosition(int absoluteX, int absoluteY) {
		root.setAbsolutePosition(absoluteX, absoluteY);
	}

	@Override
	public void handleEvent(GuiEvent event) {
		root.handleEvent(event);
	}

}
