package name.martingeisse.miner.common.network.message;

import com.google.common.collect.ImmutableList;
import org.jboss.netty.buffer.ChannelBuffer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class BufferUtil {

	public static void encodeString(String s, ChannelBuffer buffer) {
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

	public static <T> void encodeList(Collection<T> list, Encoder<T> elementEncoder, ChannelBuffer buffer) {
		buffer.writeInt(list.size());
		for (T element : list) {
			elementEncoder.encode(element, buffer);
		}
	}

	public static <T> ImmutableList<T> decodeList(Decoder<T> elementDecoder, ChannelBuffer buffer) throws MessageDecodingException {
		List<T> list = new ArrayList<>();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			list.add(elementDecoder.decode(buffer));
		}
		return ImmutableList.copyOf(list);
	}

	public static int computeEncodedListSize(int elementSize, int elementCount) {
		return 4 + elementCount * elementSize;
	}

	public static int computeEncodedListSize(Collection<?> list, int elementCount) {
		return computeEncodedListSize(list.size(), elementCount);
	}

	public interface Encoder<T> {
		void encode(T object, ChannelBuffer buffer);
	}

	public interface Decoder<T> {
		T decode(ChannelBuffer buffer) throws MessageDecodingException;
	}

}
