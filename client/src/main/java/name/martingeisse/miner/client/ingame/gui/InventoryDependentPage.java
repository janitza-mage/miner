/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame.gui;

/**
 * Implemented by pages that must react to inventory changes.
 */
public interface InventoryDependentPage {

	void onInventoryChanged();

}
