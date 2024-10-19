/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gleng.resource_node;

import name.martingeisse.gleng.GlWorkUnit;

/**
 * A system resource that can be disposed of. System resources can be put into a {@link SystemResourceNode} for
 * management. Note that all management and disposal is done from the application thread, even if the resource
 * object represents an OpenGL-side resource -- in the latter case, dispose() gets called from the application thread
 * and schedules a {@link GlWorkUnit} for the actual disposal (possibly waiting for it to complete, depending on the
 * resource).
 */
public interface ISystemResource {

	/**
	 * Disposes of this system resource.
	 * @throws IllegalStateException if already disposed
	 */
	void dispose() throws IllegalStateException;

}
