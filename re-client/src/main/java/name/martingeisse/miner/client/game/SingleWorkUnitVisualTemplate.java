/**
 * Copyright (c) 2013 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.game;

import name.martingeisse.miner.client.engine.GlWorkUnit;

/**
 * Specialized visual template that uses a single work unit for rendering
 * and can therefore be rendered in an "embedded" way, i.e. as part of
 * another work unit, from the GL worker thread.
 * <p>
 * Note for implementations: Use {@link AbstractSingleWorkUnitVisualTemplate}
 * if possible.
 */
public interface SingleWorkUnitVisualTemplate<T> extends VisualTemplate<T> {

	/**
	 * Renders the specified subject using this template. This method does
	 * not product work units. Instead, rendering is embedded in the calling
	 * work unit.
	 * <p>
	 * This method must be called from the OpenGL thread.
	 *
	 * @param subject the subject to render
	 */
	void renderEmbedded(T subject);

	/**
	 * Creates a work unit for the specified subject.
	 *
	 * @param subject the subject to render
	 * @return the work unit
	 */
	GlWorkUnit createWorkUnit(T subject);

}
