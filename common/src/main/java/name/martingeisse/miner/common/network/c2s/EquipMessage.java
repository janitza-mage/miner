/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

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
	protected void encodeBody(ByteBuf buffer) {
		buffer.writeLong(inventorySlotId);
		buffer.writeBoolean(unequip);
	}

	public static EquipMessage decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new EquipMessage(buffer.readLong(), buffer.readBoolean());
	}

}
