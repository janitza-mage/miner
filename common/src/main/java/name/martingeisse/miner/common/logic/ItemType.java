/**
 * Copyright (c) 2013 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Inventory item types.
 */
public enum ItemType {

	STONE,
	DIRT,
	COAL,
	SAND,
	BOOTS(EquipmentSlot.FOOT);

	private final String displayName;
	private final EquipmentSlot equipmentSlot;

	private ItemType() {
		this(null, EquipmentSlot.HAND);
	}

	private ItemType(EquipmentSlot equipmentSlot) {
		this(null, equipmentSlot);
	}

	private ItemType(String displayName) {
		this(displayName, EquipmentSlot.HAND);
	}

	private ItemType(String displayName, EquipmentSlot equipmentSlot) {
		if (displayName == null) {
			this.displayName = generateDisplayName(name());
		} else {
			this.displayName = displayName;
		}
		this.equipmentSlot = equipmentSlot;
	}

	/**
	 * 
	 */
	private static String generateDisplayName(String name) {
		// TODO
		return name;
	}

	/**
	 * Getter method for the displayName.
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	public EquipmentSlot getEquipmentSlot() {
		return equipmentSlot;
	}

}
