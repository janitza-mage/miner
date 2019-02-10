/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.hud;

import name.martingeisse.miner.client.MinerResources;
import name.martingeisse.miner.client.ingame.logic.Inventory;
import name.martingeisse.miner.client.ingame.logic.InventorySlot;
import name.martingeisse.miner.client.util.frame.AbstractFrameHandler;
import name.martingeisse.miner.client.util.glworker.GlWorkUnit;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;
import name.martingeisse.miner.client.util.lwjgl.Font;
import name.martingeisse.miner.client.util.lwjgl.StackdTexture;
import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.cubetype.CubeTypes;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;
import name.martingeisse.miner.common.logic.EquipmentSlot;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glWindowPos2i;

/**
 * Draws the equipped item with equipment slot HAND, if any.
 *
 * TODO does not change correctly when selecting another item
 */
public final class EquippedItemHud extends AbstractFrameHandler {

	private volatile String text;

	private final GlWorkUnit glWorkUnit = new GlWorkUnit() {
		@Override
		public void execute() {
			glBindTexture(GL_TEXTURE_2D, 0);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glWindowPos2i(0, Display.getHeight());
			GL11.glPixelTransferf(GL11.GL_RED_BIAS, 1.0f);
			GL11.glPixelTransferf(GL11.GL_GREEN_BIAS, 1.0f);
			GL11.glPixelTransferf(GL11.GL_BLUE_BIAS, 1.0f);
			MinerResources.getInstance().getFont().drawText(text, 2, Font.ALIGN_LEFT, Font.ALIGN_TOP);
			GL11.glPixelTransferf(GL11.GL_RED_BIAS, 0.0f);
			GL11.glPixelTransferf(GL11.GL_GREEN_BIAS, 0.0f);
			GL11.glPixelTransferf(GL11.GL_BLUE_BIAS, 0.0f);
		}
	};

	@Override
	public void draw(GlWorkerLoop glWorkerLoop) {
		InventorySlot slot = Inventory.INSTANCE.getEquippedItems().get(EquipmentSlot.HAND);
		this.text = (slot == null ? "" : (slot.getType() + " (" + slot.getQuantity() + ")"));
		glWorkerLoop.schedule(glWorkUnit);
	}

}
