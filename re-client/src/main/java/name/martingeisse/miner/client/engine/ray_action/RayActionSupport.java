/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine.ray_action;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class captures the information that is needed for ray actions.
 * <p>
 * Usage: Invoke gl__Capture() from within the OpenGL thread during the render process to capture a target, then invoke
 * execute() from the application thread to execute the action. Make sure to call gl__Capture() only when needed, and
 * only once, since it is very expensive.
 * <p>
 * TODO: consider doing all calculations purely on in the application thread, without reading from the depth buffer,
 * as this is much cheaper (even when doing the math in the application thread), avoids thread synchronization *and*
 * solves the problem that gluUnProject is not available in LWJGL3.
 */
public class RayActionSupport {

	private final int framebufferWidth;
	private final int framebufferHeight;
	private final FloatBuffer depthValueBuffer;
	private final IntBuffer viewport;
	private final FloatBuffer modelviewTransform;
	private final FloatBuffer projectionTransform;
	private final FloatBuffer objectPosition;
	private double impactX;
	private double impactY;
	private double impactZ;
	private boolean captured;

	/**
	 * Constructor.
	 * <p>
	 * Note: The source position of the ray could actually be obtained from the
	 * modelview matrix, just like all other projection properties are.
	 * However, this involves *inverting* the modelview matrix, so I try
	 * to avoid it. It is basically (MV^-1)*(0, 0, 0, 1), transforming the
	 * origin in view space back to world space (the model transformation is
	 * the identity).
	 *
	 * @param framebufferWidth the width of the frame buffer
	 * @param framebufferHeight the height of the frame buffer
	 */
	public RayActionSupport(int framebufferWidth, int framebufferHeight) {
		this.framebufferWidth = framebufferWidth;
		this.framebufferHeight = framebufferHeight;
		this.depthValueBuffer = BufferUtils.createFloatBuffer(1);
		this.viewport = BufferUtils.createIntBuffer(16);
		this.modelviewTransform = BufferUtils.createFloatBuffer(16);
		this.projectionTransform = BufferUtils.createFloatBuffer(16);
		this.objectPosition = BufferUtils.createFloatBuffer(3);
		this.captured = false;
	}

	/**
	 * This method captures the current position from the modelview transform
	 * and the ray impact distance from the depth buffer. It must be invoked
	 * after the world has been drawn, but before the HUD is drawn (potentially
	 * destroying the world's pixels) and before swapping buffers.
	 *
	 * Performance-wise, it is advised to call this method only if a ray action
	 * is actually being used in this frame, because it might stall and flush
	 * the render pipeline.
	 */
	public void gl__Capture() {

		// read the distance from the depth buffer
		glReadPixels(framebufferWidth >> 1, framebufferHeight >> 1, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, depthValueBuffer);
		float depthBufferValue = depthValueBuffer.get(0);

		// read the viewport and transform values
		glGetIntegerv(GL_VIEWPORT, viewport);
		glGetFloatv(GL_MODELVIEW_MATRIX, modelviewTransform);
		glGetFloatv(GL_PROJECTION_MATRIX, projectionTransform);

		// compute the impact position
		/*
		TODO gluUnProject is not available
		gluUnProject(width >> 1, height >> 1, depthBufferValue, modelviewTransform, projectionTransform, viewport, objectPosition);
		impactX = objectPosition.get(0);
		impactY = objectPosition.get(1);
		impactZ = objectPosition.get(2);
		captured = true;
		 */

	}

	/**
	 * Releases the captured values.
	 */
	public void release() {
		captured = false;
	}

	/**
	 * Executes the specified ray action. This method only works when capture() has
	 * been called during the render process. The captured impact position, together
	 * with the source position specified here as well as the "forward" flag determine
	 * which cube is affected by the ray action.
	 *
	 * @param sourceX the x position of the ray source
	 * @param sourceY the y position of the ray source
	 * @param sourceZ the z position of the ray source
	 * @param action the action to execute
	 */
	public void execute(double sourceX, double sourceY, double sourceZ, RayAction action) {
		if (!captured) {
			return;
		}
		double dx = (impactX - sourceX);
		double dy = (impactY - sourceY);
		double dz = (impactZ - sourceZ);
		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		double factor = ((action.isForward() ? 0.01 : -0.01) / distance);
		dx *= factor;
		dy *= factor;
		dz *= factor;
		int baseX = (int) Math.floor(impactX - dx * 5);
		int baseY = (int) Math.floor(impactY - dy * 5);
		int baseZ = (int) Math.floor(impactZ - dz * 5);
		for (int i = 0; i < 10; i++) {
			int currentX = (int) Math.floor(impactX);
			int currentY = (int) Math.floor(impactY);
			int currentZ = (int) Math.floor(impactZ);
			if (currentX != baseX || currentY != baseY || currentZ != baseZ) {
				action.handleImpact(currentX, currentY, currentZ, distance);
				return;
			}
			impactX += dx;
			impactY += dy;
			impactZ += dz;
		}
	}

}
