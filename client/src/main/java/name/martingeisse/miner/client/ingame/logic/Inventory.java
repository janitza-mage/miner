/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame.logic;

import com.google.common.collect.ImmutableList;

/**
 *
 */
public final class Inventory {

	public static final Inventory INSTANCE = new Inventory();

	private ImmutableList<Item> items = ImmutableList.of(
		new Item("Eins"),
		new Item("Zwei"),
		new Item("Drei")
	);

	public ImmutableList<Item> getItems() {
		return items;
	}

	public void setItems(ImmutableList<Item> items) {
		if (items == null) {
			throw new IllegalArgumentException("items cannot be null");
		}
		this.items = items;
	}

}
