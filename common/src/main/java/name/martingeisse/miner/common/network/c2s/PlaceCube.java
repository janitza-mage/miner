package name.martingeisse.miner.common.network.c2s;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;
import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.logic.EquipmentSlot;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

/**
 * Places the currently equipped inventory item for {@link EquipmentSlot#HAND} as a cube in the world.
 */
public final class PlaceCube extends Message {

	private final Vector3i position;
	private final AxisAlignedDirection direction;

	public PlaceCube(Vector3i position, AxisAlignedDirection direction) {
		if (direction.getAxis() == 1) {
			throw new IllegalArgumentException("PlaceCube must specify a horizontal direction");
		}
		this.position = position;
		this.direction = direction;
	}

	public Vector3i getPosition() {
		return position;
	}

	public AxisAlignedDirection getDirection() {
		return direction;
	}

	@Override
	protected int getExpectedBodySize() {
		return Vector3i.ENCODED_SIZE + 1;
	}

	@Override
	protected void encodeBody(ByteBuf buffer) {
		position.encode(buffer);
		buffer.writeByte((byte) direction.ordinal());
	}

	public static PlaceCube decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new PlaceCube(Vector3i.decode(buffer), AxisAlignedDirection.values()[buffer.readByte()]);
	}

}
