/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame.logic;

import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.network.s2c.UpdateInventory;

/**
 *
 */
public final class InventorySlot {

	private final long id;
	private final CubeType type;
	private final int quantity;
	private boolean equipped;

	public InventorySlot(UpdateInventory.Element element) {
		this(element.getId(), element.getType(), element.getQuantity(), element.isEquipped());
	}

	public InventorySlot(long id, CubeType type, int quantity, boolean equipped) {
		this.id = id;
		this.type = type;
		this.quantity = quantity;
		this.equipped = equipped;
	}

	public long getId() {
		return id;
	}

	public CubeType getType() {
		return type;
	}

	public int getQuantity() {
		return quantity;
	}

	public boolean isEquipped() {
		return equipped;
	}

	public void setEquipped(boolean equipped) {
		this.equipped = equipped;
		Inventory.INSTANCE.updateDerived();
	}

}
