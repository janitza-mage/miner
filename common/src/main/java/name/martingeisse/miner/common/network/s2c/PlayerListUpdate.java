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
import name.martingeisse.miner.common.network.s2c.response.LoginResponse;

/**
 * Sends an update for the list of players currently playing. Not to be confused with {@link LoginResponse} which
 * sends a list of players for the current user account.
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

		private final Vector3d position;
		private final EulerAngles angles;
		private final String name;

		public Element(Vector3d position, EulerAngles angles, String name) {
			this.position = position;
			this.angles = angles;
			this.name = name;
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
			position.encode(buffer);
			angles.encode(buffer);
			BufferUtil.encodeString(name, buffer);
		}

		public static Element decode(ByteBuf buffer) {
			return new Element(Vector3d.decode(buffer), EulerAngles.decode(buffer), BufferUtil.decodeString(buffer));
		}

	}

}
