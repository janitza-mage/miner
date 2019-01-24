/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.BufferUtil;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

/**
 * This message is used to tell the player's name to all clients. It is not really an *update* since the player's
 * name cannot change mid-game, and currently it sends all players' names redundantly in regular intervals.
 */
public final class PlayerNamesUpdate extends Message {

	private final ImmutableList<Element> elements;

	public PlayerNamesUpdate(ImmutableList<Element> elements) {
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

	public static PlayerNamesUpdate decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new PlayerNamesUpdate(BufferUtil.decodeList(Element::decode, buffer));
	}

	public static final class Element {

		private final int id;
		private final String name;

		public Element(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void encode(ByteBuf buffer) {
			buffer.writeInt(id);
			BufferUtil.encodeString(name, buffer);
		}

		public static Element decode(ByteBuf buffer) throws MessageDecodingException {
			return new Element(buffer.readInt(), BufferUtil.decodeString(buffer));
		}

	}
}
