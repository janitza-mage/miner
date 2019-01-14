/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message.s2c;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.vector.Vector3d;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageCodes;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;

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
		StackdPacket packet = new StackdPacket(MessageCodes.S2C_PLAYER_NAMES_UPDATE, elements.size() * Element.ENCODED_SIZE);
		for (Element element : elements) {
			element.encode(packet.getBuffer());
		}
		return packet;
	}

	public static PlayerNamesUpdate decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		List<Element> elements = new ArrayList<>();
		while (buffer.readableBytes() >= Element.ENCODED_SIZE) {
			elements.add(Element.decode(buffer));
		}
		return new PlayerNamesUpdate(ImmutableList.copyOf(elements));
	}


	/*
				Map<Integer, String> updatedPlayerNames = new HashMap<Integer, String>();
			while (buffer.readableBytes() > 0) {
				int id = buffer.readInt();
				int length = buffer.readInt();
				byte[] data = new byte[2 * length];
				buffer.readBytes(data);
				try {
					updatedPlayerNames.put(id, new String(data, "UTF-16"));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

	 */

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
			position.encode(buffer);
			angles.encode(buffer);
		}

		public static Element decode(ChannelBuffer buffer) {
			return new Element(buffer.readInt(), foo);
		}

	}
}
