package name.martingeisse.miner.common.network.c2s;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;
import name.martingeisse.miner.common.network.BufferUtil;
import org.jboss.netty.buffer.ChannelBuffer;

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
	protected int getExpectedBodySize() {
		return -1;
	}

	@Override
	protected void encodeBody(ChannelBuffer buffer) {
		BufferUtil.encodeImplicitSizeList(elements, Element::encode, buffer);
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
