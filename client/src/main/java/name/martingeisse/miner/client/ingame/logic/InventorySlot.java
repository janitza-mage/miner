/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame.logic;

/**
 *
 */
public final class InventorySlot {

	private final String name;
	private boolean equipped;

	public InventorySlot(String name, boolean equipped) {
		this.name = name;
		this.equipped = equipped;
	}

	public String getName() {
		return name;
	}

	public boolean isEquipped() {
		return equipped;
	}

}
