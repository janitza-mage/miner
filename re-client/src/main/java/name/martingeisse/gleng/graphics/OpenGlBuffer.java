/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gleng.graphics;

import name.martingeisse.gleng.GlWorkUnit;
import name.martingeisse.gleng.resource_node.AbstractSystemResource;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

/**
 * Manages a OpenGL-server-side buffer.
 */
public class OpenGlBuffer extends AbstractSystemResource {

	private final int id;
	private final GlWorkUnit disposer = new GlWorkUnit() {
		@Override
		protected void gl__Execute() {
			glDeleteBuffers(id);
		}
	};

	public OpenGlBuffer() {
		this.id = glGenBuffers();
	}

	public int getId() {
		return id;
	}

	@Override
	protected void internalDispose() {
		disposer.schedule();
	}

}
