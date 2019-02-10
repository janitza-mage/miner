/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame.logic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.ingame.gui.InventoryPage;
import name.martingeisse.miner.common.logic.EquipmentSlot;
import name.martingeisse.miner.common.logic.ItemType;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class Inventory {

	public static final Inventory INSTANCE = new Inventory();

	private ImmutableList<InventorySlot> slots = ImmutableList.of();
	private ImmutableMap<EquipmentSlot, InventorySlot> equippedItems = ImmutableMap.of();

	public ImmutableList<InventorySlot> getSlots() {
		return slots;
	}

	public ImmutableMap<EquipmentSlot, InventorySlot> getEquippedItems() {
		return equippedItems;
	}

	public void setSlots(ImmutableList<InventorySlot> slots) {
		if (slots == null) {
			throw new IllegalArgumentException("slots cannot be null");
		}
		this.slots = slots;
		updateEquippedItems();
		if (Ingame.get().isGuiOpen() && Ingame.get().getGui().getRootElement() instanceof InventoryPage) {
			InventoryPage inventoryPage = (InventoryPage)Ingame.get().getGui().getRootElement();
			inventoryPage.refreshInventory();
		}
	}

	void updateEquippedItems() {
		Map<EquipmentSlot, InventorySlot> map = new HashMap<>();
		for (InventorySlot slot : slots) {
			map.put(ItemType.valueOf(slot.getType()).getEquipmentSlot(), slot);
		}
		this.equippedItems = ImmutableMap.copyOf(map);
	}

}
