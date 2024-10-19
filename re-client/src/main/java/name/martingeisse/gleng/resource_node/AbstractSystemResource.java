/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gleng.resource_node;

/**
 * Base implementation for {@link ISystemResource} that prevents double disposal.
 */
public abstract class AbstractSystemResource implements ISystemResource {

	private boolean disposed;

	public AbstractSystemResource() {
		this.disposed = false;
	}

	@Override
	public final void dispose() {
		if (disposed) {
			throw new IllegalStateException("this resource has already been disposed of");
		}
		internalDispose();
		disposed = true;
	}

	/**
	 * Actually disposes of this resource. Only called if not yet disposed.
	 */
	protected abstract void internalDispose();

}
