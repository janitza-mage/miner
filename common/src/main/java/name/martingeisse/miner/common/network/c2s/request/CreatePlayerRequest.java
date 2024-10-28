/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s.request;

import name.martingeisse.miner.common.Faction;
import name.martingeisse.miner.common.network.BufferUtil;
import name.martingeisse.miner.common.network.MessageDecodingException;

import java.nio.ByteBuffer;

/**
 *
 */
public final class CreatePlayerRequest extends Request {

	private final Faction faction;
	private final String name;

	public CreatePlayerRequest(Faction faction, String name) {
		this.faction = faction;
		this.name = name;
	}

	public Faction getFaction() {
		return faction;
	}

	public String getName() {
		return name;
	}

	@Override
	protected int getExpectedBodySize() {
		return -1;
	}

	@Override
	protected void encodeBody(ByteBuffer buffer) {
		buffer.putInt(faction.ordinal());
		BufferUtil.encodeString(name, buffer);
	}

	public static CreatePlayerRequest decodeBody(ByteBuffer buffer) throws MessageDecodingException {
		return new CreatePlayerRequest(Faction.values()[buffer.getInt()], BufferUtil.decodeString(buffer));
	}

}
