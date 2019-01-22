package name.martingeisse.miner.common.network.message.s2c;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.BufferUtil;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;

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
	protected int getExpectedBodySize() {
		return -1;
	}

	@Override
	protected void encodeBody(ChannelBuffer buffer) {
		BufferUtil.encodeList(segments, BufferUtil::encodeString, buffer);
	}

	public static ConsoleOutput decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new ConsoleOutput(BufferUtil.decodeList(BufferUtil::decodeString, buffer));
	}

}
