package name.martingeisse.miner.client.tools;

import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.GuiEvent;
import name.martingeisse.miner.client.util.gui.element.NullElement;
import name.martingeisse.miner.client.util.gui.element.Wrapper;

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

	private final Wrapper contentWrapper;

	public ScrollContainer() {
		contentWrapper = new Wrapper(new NullElement());
	}

	@Override
	public void requestSize(int width, int height) {
		setSize(width, height);
	}

	@Override
	protected void setChildrenLayoutPosition(int absoluteX, int absoluteY) {
		super.setChildrenLayoutPosition(absoluteX, absoluteY);
	}

	@Override
	public void handleEvent(GuiEvent event) {

	}

}
