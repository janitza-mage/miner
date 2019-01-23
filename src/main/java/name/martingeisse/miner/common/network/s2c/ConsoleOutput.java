package name.martingeisse.miner.common.network.s2c;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.BufferUtil;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

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
	protected void encodeBody(ByteBuf buffer) {
		BufferUtil.encodeList(segments, BufferUtil::encodeString, buffer);
	}

	public static ConsoleOutput decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new ConsoleOutput(BufferUtil.decodeList(BufferUtil::decodeString, buffer));
	}

}
