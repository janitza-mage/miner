/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.hud;

import name.martingeisse.miner.client.MinerResources;
import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.client.util.glworker.GlWorkUnit;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;
import name.martingeisse.miner.client.util.ray_action.Font;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glWindowPos2i;

/**
 * Displays the current frames per second on the screen.
 */
public final class FpsPanel extends AbstractFrameHandler {

	/**
	 * the lastSamplingTime
	 */
	private long lastSamplingTime;

	/**
	 * the countedFrames
	 */
	private int countedFrames;

	/**
	 * the fps
	 */
	private int fps;

	/**
	 * the glWorkUnit
	 */
	private final GlWorkUnit glWorkUnit = new GlWorkUnit() {
		@Override
		public void execute() {

			// update FPS (in the drawing thread, so we don't count skipped frames!)
			countedFrames++;
			long now = System.currentTimeMillis();
			if ((now - lastSamplingTime) >= 1000) {
				fps = countedFrames;
				countedFrames = 0;
				lastSamplingTime = now;
			}

			// draw the FPS panel
			String fpsText = Float.toString(fps);
			glBindTexture(GL_TEXTURE_2D, 0);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glWindowPos2i(Display.getWidth(), Display.getHeight());
			GL11.glPixelTransferf(GL11.GL_RED_BIAS, 1.0f);
			GL11.glPixelTransferf(GL11.GL_GREEN_BIAS, 1.0f);
			GL11.glPixelTransferf(GL11.GL_BLUE_BIAS, 1.0f);
			MinerResources.getInstance().getFont().drawText(fpsText, 2, Font.ALIGN_RIGHT, Font.ALIGN_TOP);
			GL11.glPixelTransferf(GL11.GL_RED_BIAS, 0.0f);
			GL11.glPixelTransferf(GL11.GL_GREEN_BIAS, 0.0f);
			GL11.glPixelTransferf(GL11.GL_BLUE_BIAS, 0.0f);

		}
	};

	/**
	 * Constructor.
	 */
	public FpsPanel() {
		this.lastSamplingTime = System.currentTimeMillis();
		this.countedFrames = 0;
		this.fps = 0;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.frame.AbstractFrameHandler#draw(name.martingeisse.glworker.GlWorkerLoop)
	 */
	@Override
	public void draw(GlWorkerLoop glWorkerLoop) {
		glWorkerLoop.schedule(glWorkUnit);
	}

}
