package name.martingeisse.miner.client.util.gui.element.wrapper;

import name.martingeisse.miner.client.util.gui.GuiElement;

/**
 * This class just wraps another element to make it exchangeable at runtime.
 */
public final class Wrapper extends AbstractWrapperElement {

	public Wrapper() {
	}

	public Wrapper(GuiElement wrappedElement) {
		super(wrappedElement);
	}

	@Override
	public void requestSize(int width, int height) {
		requireWrappedElement();
		getWrappedElement().requestSize(width, height);
	}

}
