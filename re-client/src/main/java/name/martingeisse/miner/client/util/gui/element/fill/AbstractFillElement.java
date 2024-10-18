/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element.fill;

import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;
import name.martingeisse.miner.client.util.gui.GuiLogicFrameContext;
import name.martingeisse.miner.client.util.gui.util.LeafElement;

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
	public void handleGraphicsFrame(GraphicsFrameContext context) {
		if (cachedWorkUnit == null) {
			cachedWorkUnit = createWorkUnit();
		}
		context.schedule(cachedWorkUnit);
	}

}
