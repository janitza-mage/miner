package name.martingeisse.miner.common.network.message.c2s;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.geometry.vector.Vector3i;
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
public final class CubeModification extends Message {

	private final ImmutableList<Element> elements;

	public CubeModification(ImmutableList<Element> elements) {
		this.elements = elements;
	}

	public ImmutableList<Element> getElements() {
		return elements;
	}

	@Override
	public StackdPacket encodePacket() {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeZero(StackdPacket.HEADER_SIZE);
		BufferUtil.encodeImplicitSizeList(elements, Element::encode, buffer);
		return new StackdPacket(MessageCodes.C2S_CUBE_MODIFICATION, buffer, false);
	}

	public static CubeModification decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new CubeModification(BufferUtil.decodeImplicitSizeList(Element::decode, buffer));
	}

	public static final class Element {

		private final Vector3i position;
		private final byte cubeType;

		public Element(Vector3i position, byte cubeType) {
			this.position = position;
			this.cubeType = cubeType;
		}

		public Vector3i getPosition() {
			return position;
		}

		public byte getCubeType() {
			return cubeType;
		}

		public void encode(ChannelBuffer buffer) {
			position.encode(buffer);
			buffer.writeByte(cubeType);
		}

		public static Element decode(ChannelBuffer buffer) throws MessageDecodingException {
			return new Element(Vector3i.decode(buffer), buffer.readByte());
		}

	}

	public static class Builder {

		private final List<Element> elements = new ArrayList<>();

		public void add(int x, int y, int z, byte cubeType) {
			add(new Vector3i(x, y, z), cubeType);
		}

		public void add(Vector3i position, byte cubeType) {
			add(new Element(position, cubeType));
		}

		public void add(Element element) {
			elements.add(element);
		}

		public CubeModification build() {
			return new CubeModification(ImmutableList.copyOf(elements));
		}

	}

}
