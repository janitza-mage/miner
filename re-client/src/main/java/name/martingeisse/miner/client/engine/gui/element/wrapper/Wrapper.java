package name.martingeisse.miner.client.engine.gui.element.wrapper;

import name.martingeisse.miner.client.engine.gui.GuiElement;

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
		getWrappedElement().requestSize(width, height);
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		getWrappedElement().setAbsolutePosition(absoluteX, absoluteY);
	}

}
