package name.martingeisse.miner.common.network.c2s;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

/**
 *
 */
public final class PlaceCube extends Message {

	private final Vector3i position;
	private final byte cubeType;

	public PlaceCube(Vector3i position, byte cubeType) {
		this.position = position;
		this.cubeType = cubeType;
	}

	public Vector3i getPosition() {
		return position;
	}

	public byte getCubeType() {
		return cubeType;
	}

	@Override
	protected int getExpectedBodySize() {
		return Vector3i.ENCODED_SIZE + 1;
	}

	@Override
	protected void encodeBody(ByteBuf buffer) {
		position.encode(buffer);
		buffer.writeByte(cubeType);
	}

	public static PlaceCube decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new PlaceCube(Vector3i.decode(buffer), buffer.readByte());
	}

}
