/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c.response;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.Faction;
import name.martingeisse.miner.common.network.BufferUtil;
import name.martingeisse.miner.common.network.MessageDecodingException;
import name.martingeisse.miner.common.network.c2s.request.LoginRequest;
import name.martingeisse.miner.common.network.s2c.PlayerListUpdate;

import java.nio.ByteBuffer;

/**
 * A response to {@link LoginRequest}. Contains the players for this user account. Not to be confused with
 * {@link PlayerListUpdate} which contains the list of other players currently playing.
 */
public final class LoginResponse extends Response {

	private final ImmutableList<Element> elements;

	public LoginResponse(ImmutableList<Element> elements) {
		this.elements = elements;
	}

	public ImmutableList<Element> getElements() {
		return elements;
	}

	@Override
	protected int getExpectedBodySize() {
		return -1;
	}

	@Override
	protected void encodeBody(ByteBuffer buffer) {
		BufferUtil.encodeList(elements, Element::encode, buffer);
	}

	public static LoginResponse decodeBody(ByteBuffer buffer) throws MessageDecodingException {
		return new LoginResponse(BufferUtil.decodeList(Element::decode, buffer));
	}

	public static final class Element {

		private final long id;
		private final String name;
		private final Faction faction;
		private final long coins;

		public Element(long id, String name, Faction faction, long coins) {
			this.id = id;
			this.name = name;
			this.faction = faction;
			this.coins = coins;
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Faction getFaction() {
			return faction;
		}

		public long getCoins() {
			return coins;
		}

		public void encode(ByteBuffer buffer) {
			buffer.putLong(id);
			BufferUtil.encodeString(name, buffer);
			buffer.putInt(faction.ordinal());
			buffer.putLong(coins);
		}

		public static Element decode(ByteBuffer buffer) {
			return new Element(buffer.getLong(), BufferUtil.decodeString(buffer), Faction.values()[buffer.getInt()], buffer.getLong());
		}

	}

}
