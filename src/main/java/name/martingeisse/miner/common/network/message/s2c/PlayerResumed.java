package name.martingeisse.miner.common.network.message.s2c;

import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.angle.ReadableEulerAngles;
import name.martingeisse.miner.common.geometry.vector.ReadableVector3d;
import name.martingeisse.miner.common.geometry.vector.Vector3d;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageCodes;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;

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
	public StackdPacket encodePacket() {
		StackdPacket packet = new StackdPacket(MessageCodes.S2C_PLAYER_RESUMED, Vector3d.ENCODED_SIZE + EulerAngles.ENCODED_SIZE);
		ChannelBuffer buffer = packet.getBuffer();
		position.encode(buffer);
		orientation.encode(buffer);
		return packet;
	}

	public static PlayerResumed decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new PlayerResumed(Vector3d.decode(buffer), EulerAngles.decode(buffer));
	}
}
