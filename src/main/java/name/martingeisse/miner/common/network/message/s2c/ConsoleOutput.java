package name.martingeisse.miner.common.network.message.s2c;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.BufferUtil;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageCodes;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * This packet contains output lines to print on the console.
 * Only complete lines can be output this way.
 */
public final class ConsoleOutput extends Message {

	private final ImmutableList<String> segments;

	public ConsoleOutput(ImmutableList<String> segments) {
		this.segments = segments;
	}

	public ImmutableList<String> getSegments() {
		return segments;
	}

	@Override
	public StackdPacket encodePacket() {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeZero(StackdPacket.HEADER_SIZE);
		BufferUtil.encodeList(segments, BufferUtil::encodeString, buffer);
		return new StackdPacket(MessageCodes.S2C_CONSOLE_OUTPUT, buffer, false);
	}

	public static ConsoleOutput decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new ConsoleOutput(BufferUtil.decodeList(BufferUtil::decodeString, buffer));
	}

}
