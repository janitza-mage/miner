/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s;

import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

import java.nio.ByteBuffer;

/**
 *
 */
public final class EquipMessage extends Message {

	private final long inventorySlotId;
	private final boolean unequip;

	public EquipMessage(long inventorySlotId, boolean unequip) {
		this.inventorySlotId = inventorySlotId;
		this.unequip = unequip;
	}

	public long getInventorySlotId() {
		return inventorySlotId;
	}

	public boolean isUnequip() {
		return unequip;
	}

	@Override
	protected int getExpectedBodySize() {
		return 9;
	}

	@Override
	protected void encodeBody(ByteBuffer buffer) {
		buffer.putLong(inventorySlotId);
		buffer.putBoolean(unequip);
	}

	public static EquipMessage decodeBody(ByteBuffer buffer) throws MessageDecodingException {
		return new EquipMessage(buffer.getLong(), buffer.getBoolean());
	}

}
