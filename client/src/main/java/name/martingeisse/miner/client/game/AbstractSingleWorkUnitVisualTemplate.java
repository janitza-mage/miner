/**
 * Copyright (c) 2013 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.game;

import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;

/**
 * Base implementation for {@link SingleWorkUnitVisualTemplate}.
 */
public abstract class AbstractSingleWorkUnitVisualTemplate<T> implements SingleWorkUnitVisualTemplate<T> {

	@Override
	public final void render(T subject, GraphicsFrameContext context) {
		context.schedule(createWorkUnit(subject));
	}

	@Override
	public final GlWorkUnit createWorkUnit(final T subject) {
		return new GlWorkUnit() {
			@Override
			public void execute() {
				renderEmbedded(subject);
			}
		};
	}

}
