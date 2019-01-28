package name.martingeisse.miner.common.network;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class BufferUtil {

	public static void encodeString(String s, ByteBuf buffer) {
		buffer.writeInt(s.length());
		for (int i = 0; i < s.length(); i++) {
			buffer.writeChar(s.charAt(i));
		}
	}

	public static String decodeString(ByteBuf buffer) {
		int length = buffer.readInt();
		byte[] data = new byte[2 * length];
		buffer.readBytes(data);
		return new String(data, StandardCharsets.UTF_16);
	}

	public static <T> void encodeList(Collection<T> list, Encoder<T> elementEncoder, ByteBuf buffer) {
		buffer.writeInt(list.size());
		for (T element : list) {
			elementEncoder.encode(element, buffer);
		}
	}

	public static <T> ImmutableList<T> decodeList(Decoder<T> elementDecoder, ByteBuf buffer) throws MessageDecodingException {
		List<T> list = new ArrayList<>();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			list.add(elementDecoder.decode(buffer));
		}
		return ImmutableList.copyOf(list);
	}

	public static <T> void encodeImplicitSizeList(Collection<T> list, Encoder<T> elementEncoder, ByteBuf buffer) {
		for (T element : list) {
			elementEncoder.encode(element, buffer);
		}
	}

	public static <T> ImmutableList<T> decodeImplicitSizeList(Decoder<T> elementDecoder, ByteBuf buffer) throws MessageDecodingException {
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
		void encode(T object, ByteBuf buffer);
	}

	public interface Decoder<T> {
		T decode(ByteBuf buffer) throws MessageDecodingException;
	}

}
