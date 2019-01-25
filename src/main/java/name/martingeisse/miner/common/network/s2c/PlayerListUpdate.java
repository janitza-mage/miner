/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.vector.Vector3d;
import name.martingeisse.miner.common.network.BufferUtil;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

/**
 *
 */
public final class PlayerListUpdate extends Message {

	private final ImmutableList<Element> elements;

	public PlayerListUpdate(ImmutableList<Element> elements) {
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

	public static PlayerListUpdate decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new PlayerListUpdate(BufferUtil.decodeList(Element::decode, buffer));
	}

	public static final class Element {

		private final int id;
		private final Vector3d position;
		private final EulerAngles angles;
		private final String name;

		public Element(int id, Vector3d position, EulerAngles angles, String name) {
			this.id = id;
			this.position = position;
			this.angles = angles;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public Vector3d getPosition() {
			return position;
		}

		public EulerAngles getAngles() {
			return angles;
		}

		public String getName() {
			return name;
		}

		public void encode(ByteBuf buffer) {
			buffer.writeInt(id);
			position.encode(buffer);
			angles.encode(buffer);
			BufferUtil.encodeString(name, buffer);
		}

		public static Element decode(ByteBuf buffer) {
			return new Element(buffer.readInt(), Vector3d.decode(buffer), EulerAngles.decode(buffer), BufferUtil.decodeString(buffer));
		}

	}

}
