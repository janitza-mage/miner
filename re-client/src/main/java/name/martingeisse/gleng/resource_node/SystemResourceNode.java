/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gleng.resource_node;

import java.util.HashSet;
import java.util.Set;

/**
 * This class keeps a set of system resources. It also refers to a parent node and child nodes in a hierarchy that can
 * be used to dispose of multiple resources at once.
 */
public final class SystemResourceNode {

	private final SystemResourceNode parent;
	private final Set<SystemResourceNode> children;
	private final Set<ISystemResource> resources;
	private boolean disposed;

	/**
	 * Constructor for nodes without a parent.
	 */
	public SystemResourceNode() {
		this(null);
	}

	/**
	 * Constructor for nodes with a parent.
	 */
	public SystemResourceNode(SystemResourceNode parent) {
		this.parent = parent;
		this.children = new HashSet<>();
		this.resources = new HashSet<>();
		this.disposed = false;
		if (parent != null) {
			parent.children.add(this);
		}
	}

	public Set<ISystemResource> getResources() {
		ensureNotDisposed();
		return resources;
	}

	/**
	 * Adds a resource that will be disposed of when this node is being disposed of. This is a convenience method for
	 * {@link #getResources()}.add(resource).
	 */
	public void addResource(ISystemResource resource) {
		ensureNotDisposed();
		resources.add(resource);
	}

	/**
	 * Disposes of this node, all its resources, and all its descendants and their resources recursively.
	 */
	public void dispose() {
		ensureNotDisposed();
		for (ISystemResource resource : resources) {
			resource.dispose();
		}
		for (SystemResourceNode child : children) {
			child.dispose();
		}
		resources.clear();
		children.clear();
		if (parent != null) {
			parent.children.remove(this);
		}
		disposed = true;
	}

	private void ensureNotDisposed() {
		if (disposed) {
			throw new IllegalStateException("this node has already been disposed of");
		}
	}

}
