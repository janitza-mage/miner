package name.martingeisse.miner.common.network.c2s;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;
import name.martingeisse.miner.common.network.BufferUtil;
import org.jboss.netty.buffer.ChannelBuffer;

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
	protected int getExpectedBodySize() {
		return -1;
	}

	@Override
	protected void encodeBody(ChannelBuffer buffer) {
		BufferUtil.encodeList(segments, BufferUtil::encodeString, buffer);
	}

	public static ConsoleInput decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new ConsoleInput(BufferUtil.decodeList(BufferUtil::decodeString, buffer));
	}

}
