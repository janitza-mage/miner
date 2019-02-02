/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.Faction;
import name.martingeisse.miner.common.network.BufferUtil;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;
import name.martingeisse.miner.common.network.c2s.LoginRequest;

/**
 * A response to {@link LoginRequest}. Contains the players for this user account. Not to be confused with
 * {@link PlayerListUpdate} which contains the list of other players currently playing.
 */
public final class LoginResponse extends Message {

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
	protected void encodeBody(ByteBuf buffer) {
		BufferUtil.encodeList(elements, Element::encode, buffer);
	}

	public static LoginResponse decodeBody(ByteBuf buffer) throws MessageDecodingException {
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

		public void encode(ByteBuf buffer) {
			buffer.writeLong(id);
			BufferUtil.encodeString(name, buffer);
			buffer.writeInt(faction.ordinal());
			buffer.writeLong(coins);
		}

		public static Element decode(ByteBuf buffer) {
			return new Element(buffer.readLong(), BufferUtil.decodeString(buffer), Faction.values()[buffer.readInt()], buffer.readLong());
		}

	}

}
