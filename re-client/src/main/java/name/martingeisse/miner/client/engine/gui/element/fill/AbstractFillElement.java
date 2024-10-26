/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine.gui.element.fill;

import name.martingeisse.gleng.GlWorkUnit;
import name.martingeisse.miner.client.engine.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.engine.gui.util.LeafElement;

/**
 * Base class for elements that do not have any children and just fill their available area with some graphic effect.
 * <p>
 * The base class stores a single cached work unit for this which gets invalidated on position and size changes;
 * subclasses should also invalidate it when needed due to other changes.
 */
public abstract class AbstractFillElement extends LeafElement {

	private GlWorkUnit cachedWorkUnit;

	protected final void invalidateWorkUnit() {
		cachedWorkUnit = null;
	}

	@Override
	public void handleLogicFrame(GuiLogicFrameContext context) {
	}

	@Override
	public final void requestSize(int width, int height) {
		invalidateWorkUnit();
		setSize(width, height);
	}

	@Override
	protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
		invalidateWorkUnit();
	}

	protected abstract GlWorkUnit createWorkUnit();

	@Override
	public void handleGraphicsFrame() {
		if (shouldDraw()) {
			if (cachedWorkUnit == null) {
				cachedWorkUnit = createWorkUnit();
			}
			cachedWorkUnit.schedule();
		}
	}

	protected boolean shouldDraw() {
		return true;
	}

}
