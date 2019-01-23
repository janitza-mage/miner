package name.martingeisse.miner.common.network.s2c;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.angle.ReadableEulerAngles;
import name.martingeisse.miner.common.geometry.vector.ReadableVector3d;
import name.martingeisse.miner.common.geometry.vector.Vector3d;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

/**
 *
 */
public final class PlayerResumed extends Message {

	private final Vector3d position;
	private final EulerAngles orientation;

	public PlayerResumed(ReadableVector3d position, ReadableEulerAngles orientation) {
		this.position = position.freeze();
		this.orientation = orientation.freeze();
	}

	public Vector3d getPosition() {
		return position;
	}

	public EulerAngles getOrientation() {
		return orientation;
	}

	@Override
	protected int getExpectedBodySize() {
		return Vector3d.ENCODED_SIZE + EulerAngles.ENCODED_SIZE;
	}

	@Override
	protected void encodeBody(ByteBuf buffer) {
		position.encode(buffer);
		orientation.encode(buffer);
	}

	public static PlayerResumed decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new PlayerResumed(Vector3d.decode(buffer), EulerAngles.decode(buffer));
	}
}
