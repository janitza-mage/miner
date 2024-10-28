package name.martingeisse.miner.common.network;

import com.google.common.collect.ImmutableList;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class BufferUtil {

	public static void encodeString(String s, ByteBuffer buffer) {
		buffer.putInt(s.length());
		for (int i = 0; i < s.length(); i++) {
			buffer.putChar(s.charAt(i));
		}
	}

	public static String decodeString(ByteBuffer buffer) {
		int length = buffer.getInt();
		byte[] data = new byte[2 * length];
		buffer.getBytes(data);
		return new String(data, StandardCharsets.UTF_16);
	}

	public static <T> void encodeList(Collection<T> list, Encoder<T> elementEncoder, ByteBuffer buffer) {
		buffer.putInt(list.size());
		for (T element : list) {
			elementEncoder.encode(element, buffer);
		}
	}

	public static <T> ImmutableList<T> decodeList(Decoder<T> elementDecoder, ByteBuffer buffer) throws MessageDecodingException {
		List<T> list = new ArrayList<>();
		int count = buffer.getInt();
		for (int i = 0; i < count; i++) {
			list.add(elementDecoder.decode(buffer));
		}
		return ImmutableList.copyOf(list);
	}

	public static <T> void encodeImplicitSizeList(Collection<T> list, Encoder<T> elementEncoder, ByteBuffer buffer) {
		for (T element : list) {
			elementEncoder.encode(element, buffer);
		}
	}

	public static <T> ImmutableList<T> decodeImplicitSizeList(Decoder<T> elementDecoder, ByteBuffer buffer) throws MessageDecodingException {
		List<T> list = new ArrayList<>();
		while (buffer.readableBytes() > 0) {
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
		void encode(T object, ByteBuffer buffer);
	}

	public interface Decoder<T> {
		T decode(ByteBuffer buffer) throws MessageDecodingException;
	}

}
