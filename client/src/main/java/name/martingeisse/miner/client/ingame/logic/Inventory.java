/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame.logic;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.ingame.gui.InventoryPage;

/**
 *
 */
public final class Inventory {

	public static final Inventory INSTANCE = new Inventory();

	private ImmutableList<InventorySlot> slots = ImmutableList.of(
		new InventorySlot("Eins"),
		new InventorySlot("Zwei"),
		new InventorySlot("Drei")
	);

	public ImmutableList<InventorySlot> getSlots() {
		return slots;
	}

	public void setSlots(ImmutableList<InventorySlot> slots) {
		if (slots == null) {
			throw new IllegalArgumentException("slots cannot be null");
		}
		this.slots = slots;
		if (Ingame.get().isGuiOpen() && Ingame.get().getGui().getRootElement() instanceof InventoryPage) {
			Ingame.get().getGui().requestLayout();
		}
	}

}
