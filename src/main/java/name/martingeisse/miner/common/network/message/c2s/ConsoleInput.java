package name.martingeisse.miner.common.network.message.c2s;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.BufferUtil;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageCodes;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * This packet contains a console command to be executed by the server, encoded as a sequence of strings.
 */
public final class ConsoleInput extends Message {

	private final ImmutableList<String> segments;

	public ConsoleInput(ImmutableList<String> segments) {
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
		return new StackdPacket(MessageCodes.C2S_CONSOLE_INPUT, buffer, false);
	}

	public static ConsoleInput decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new ConsoleInput(BufferUtil.decodeList(BufferUtil::decodeString, buffer));
	}

}
