package name.martingeisse.miner.common.network.message;

import com.google.common.collect.ImmutableList;
import org.jboss.netty.buffer.ChannelBuffer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public class BufferUtil {

	public static void encodeString(ChannelBuffer buffer, String s) {
		buffer.writeInt(s.length());
		for (int i = 0; i < s.length(); i++) {
			buffer.writeChar(s.charAt(i));
		}
	}

	public static String decodeString(ChannelBuffer buffer) {
		int length = buffer.readInt();
		byte[] data = new byte[2 * length];
		buffer.readBytes(data);
		return new String(data, StandardCharsets.UTF_16);
	}

	public static <T> void encodeList(ChannelBuffer buffer, Collection<T> list, BiConsumer<ChannelBuffer, T> elementEncoder) {
		buffer.writeInt(list.size());
		for (T element : list) {
			elementEncoder.accept(buffer, element);
		}
	}

	public static <T> ImmutableList<T> decodeList(ChannelBuffer buffer, Function<ChannelBuffer, T> elementDecoder) {
		List<T> list = new ArrayList<>();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			list.add(elementDecoder.apply(buffer));
		}
		return ImmutableList.copyOf(list);
	}

	public static int computeEncodedListSize(int elementSize, int elementCount) {
		return 4 + elementCount * elementSize;
	}

	/**
	 * Validates that the remaining bytes in the specified buffer are equal to a certain expected size.
	 */
	public static void validateSize(ChannelBuffer buffer, int expectedSizeInBytes) throws MessageDecodingException {
		if (buffer.readableBytes() != expectedSizeInBytes) {
			throw new MessageDecodingException("wrong packet size; expected " + expectedSizeInBytes + " but got " + buffer.readableBytes());
		}
	}

	/**
	 * Validates that the remaining bytes in the specified buffer are greater or equal to a certain expected size.
	 */
	public static void validateMinimumSize(ChannelBuffer buffer, int expectedMinimumSizeInBytes) throws MessageDecodingException {
		if (buffer.readableBytes() < expectedMinimumSizeInBytes) {
			throw new MessageDecodingException("wrong packet size; expected at least " + expectedMinimumSizeInBytes + " but got " + buffer.readableBytes());
		}
	}

}
