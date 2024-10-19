/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.hud;

import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.client.util.glworker.GlWorkUnit;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;
import name.martingeisse.miner.client.util.ray_action.StackdTexture;
import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;

import static org.lwjgl.opengl.GL11.*;

/**
 * Draws a HUD element that shows a "selected" cube, whatever that
 * means -- the cube to display is simply set in this object.
 */
public final class SelectedCubeHud extends AbstractFrameHandler {

	private final StackdTexture[] textures;
	private final SelectedCubeTypeGetter selectedCubeTypeGetter;

	/**
	 * the glWorkUnit
	 */
	private final GlWorkUnit glWorkUnit = new GlWorkUnit() {
		@Override
		public void execute() {

			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glEnable(GL_TEXTURE_2D);

			double w = 1.0, h = 1.6, d = 0.5;
			double x = -0.8, y = 0.8;
			double scale = 0.10;
			w *= scale;
			h *= scale;
			d *= scale;

			CubeType cubeType = selectedCubeTypeGetter.getSelectedCubeType();
			if (cubeType == null) {
				return;
			}

			textures[cubeType.getCubeFaceTextureIndex(AxisAlignedDirection.POSITIVE_Z)].glBindTexture();
			glBegin(GL_QUADS);
			glTexCoord2f(0.0f, 0.0f);
			glVertex2d(x, y - h);
			glTexCoord2f(0.0f, 1.0f);
			glVertex2d(x, y);
			glTexCoord2f(1.0f, 1.0f);
			glVertex2d(x - w, y + d);
			glTexCoord2f(1.0f, 0.0f);
			glVertex2d(x - w, y - h + d);
			glEnd();

			textures[cubeType.getCubeFaceTextureIndex(AxisAlignedDirection.POSITIVE_X)].glBindTexture();
			glBegin(GL_QUADS);
			glTexCoord2f(0.0f, 0.0f);
			glVertex2d(x, y);
			glTexCoord2f(0.0f, 1.0f);
			glVertex2d(x, y - h);
			glTexCoord2f(1.0f, 1.0f);
			glVertex2d(x + w, y - h + d);
			glTexCoord2f(1.0f, 0.0f);
			glVertex2d(x + w, y + d);
			glEnd();

			textures[cubeType.getCubeFaceTextureIndex(AxisAlignedDirection.POSITIVE_Y)].glBindTexture();
			glBegin(GL_QUADS);
			glTexCoord2f(0.0f, 0.0f);
			glVertex2d(x, y);
			glTexCoord2f(0.0f, 1.0f);
			glVertex2d(x + w, y + d);
			glTexCoord2f(1.0f, 1.0f);
			glVertex2d(x, y + d + d);
			glTexCoord2f(1.0f, 0.0f);
			glVertex2d(x - w, y + d);
			glEnd();

		}
	};

	public SelectedCubeHud(StackdTexture[] textures, SelectedCubeTypeGetter selectedCubeTypeGetter) {
		this.textures = textures;
		this.selectedCubeTypeGetter = selectedCubeTypeGetter;
	}

	/**
	 * Getter method for the textures.
	 *
	 * @return the textures
	 */
	public StackdTexture[] getTextures() {
		return textures;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.frame.AbstractFrameHandler#draw(name.martingeisse.glworker.GlWorkerLoop)
	 */
	@Override
	public void draw(GlWorkerLoop glWorkerLoop) {
		glWorkerLoop.schedule(glWorkUnit);
	}

	public interface SelectedCubeTypeGetter {
		CubeType getSelectedCubeType();
	}

}
