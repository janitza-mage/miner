/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message.s2c;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.BufferUtil;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageCodes;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.util.ArrayList;
import java.util.List;

/**
 *
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
	public StackdPacket encodePacket() {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeZero(StackdPacket.HEADER_SIZE);
		BufferUtil.encodeList(elements, Element::encode, buffer);
		return new StackdPacket(MessageCodes.S2C_PLAYER_NAMES_UPDATE, buffer, false);
	}

	public static PlayerNamesUpdate decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new PlayerNamesUpdate(BufferUtil.decodeList(Element::decode, buffer));
	}

	private static final class Element {

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

		public void encode(ChannelBuffer buffer) {
			buffer.writeInt(id);
			BufferUtil.encodeString(name, buffer);
		}

		public static Element decode(ChannelBuffer buffer) throws MessageDecodingException {
			return new Element(buffer.readInt(), BufferUtil.decodeString(buffer));
		}

	}
}
