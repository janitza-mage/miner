/**
 * Copyright (c) 2013 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.visual;

import name.martingeisse.miner.client.glworker.AbstractSingleWorkUnitVisualTemplate;
import name.martingeisse.miner.client.ingame.MinerResources;
import name.martingeisse.miner.client.ingame.player.Player;
import name.martingeisse.miner.client.ingame.player.PlayerProxy;
import name.martingeisse.miner.client.util.GlUtil;
import org.lwjgl.opengl.GL11;

import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Represents another player (i.e. a {@link PlayerProxy}).
 */
public final class OtherPlayerVisualTemplate extends AbstractSingleWorkUnitVisualTemplate<PlayerProxy> {

	/**
	 * the resources
	 */
	private final MinerResources resources;

	/**
	 * the ownPlayer
	 */
	private final Player ownPlayer;

	/**
	 * Constructor.
	 * @param resources the resources
	 * @param ownPlayer this process's own player (used to compute the distance to
	 * the other player)
	 */
	public OtherPlayerVisualTemplate(MinerResources resources, Player ownPlayer) {
		this.resources = resources;
		this.ownPlayer = ownPlayer;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.glworker.SingleWorkUnitVisualTemplate#renderEmbedded(java.lang.Object)
	 */
	@Override
	public void renderEmbedded(PlayerProxy playerProxy) {

		// set a color that is computed from the player's session ID
		final Random random = new Random(playerProxy.getId());
		glColor4ub((byte) random.nextInt(255), (byte) random.nextInt(255), (byte) random.nextInt(255), (byte) 255);

		// Set up inverse modelview matrix, draw, then restore previous matrix.
		// Also set the raster position for drawing the name.
		glPushMatrix();
		glTranslated(playerProxy.getPosition().getX(), playerProxy.getPosition().getY(), playerProxy.getPosition().getZ());
		glRotatef((float) playerProxy.getOrientation().getHorizontalAngle(), 0, 1, 0);
		glBegin(GL_TRIANGLES);
		GlUtil.sendAxisAlignedBoxVertices(-0.2, -0.2, -0.2, 0.2, 0.2, 0.2);
		GlUtil.sendAxisAlignedBoxVertices(-0.35, -1.0, -0.3, 0.35, -0.25, 0.3);
		GlUtil.sendAxisAlignedBoxVertices(-0.3, -1.625, -0.2, -0.05, -1.0, 0.2);
		GlUtil.sendAxisAlignedBoxVertices(0.05, -1.625, -0.2, 0.3, -1.0, 0.2);
		GlUtil.sendAxisAlignedBoxVertices(-0.6, -1.0, -0.1, -0.4, -0.25, 0.1);
		GlUtil.sendAxisAlignedBoxVertices(0.4, -1.0, -0.1, 0.6, -0.25, 0.1);
		glEnd();
		glRasterPos3f(0f, 0.5f, 0f);
		glPopMatrix();

		// compute the distance between the players
		final double dx = ownPlayer.getPosition().getX() - playerProxy.getPosition().getX();
		final double dy = ownPlayer.getPosition().getY() - playerProxy.getPosition().getY();
		final double dz = ownPlayer.getPosition().getZ() - playerProxy.getPosition().getZ();
		final double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		final double zoom = 5.0 / (Math.sqrt(distance) + 0.5);

		// draw the player's name
		String name = playerProxy.getName();
		if (name == null) {
			name = "...";
		}
		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_BLEND);
		glDepthFunc(GL_ALWAYS);
		GL11.glPixelTransferf(GL11.GL_RED_BIAS, 1.0f);
		GL11.glPixelTransferf(GL11.GL_GREEN_BIAS, 1.0f);
		GL11.glPixelTransferf(GL11.GL_BLUE_BIAS, 1.0f);
		resources.drawText(name, (float) zoom);
		GL11.glPixelTransferf(GL11.GL_RED_BIAS, 0.0f);
		GL11.glPixelTransferf(GL11.GL_GREEN_BIAS, 0.0f);
		GL11.glPixelTransferf(GL11.GL_BLUE_BIAS, 0.0f);
		glDepthFunc(GL_LESS);

	}

}
