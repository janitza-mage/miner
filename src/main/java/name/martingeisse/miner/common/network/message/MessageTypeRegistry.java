package name.martingeisse.miner.common.network.message;

import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.c2s.*;
import name.martingeisse.miner.common.network.message.s2c.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO remove packet construction from message classes here?
 */
public final class MessageTypeRegistry {

	public static final MessageTypeRegistry INSTANCE = new MessageTypeRegistry();

	private final List<Entry<?>> codeToEntry = new ArrayList<>();
	private final Map<Class<? extends Message>, Integer> classToCode = new HashMap<>();

	private MessageTypeRegistry() {

		register(ConsoleInput.class, ConsoleInput::decodeBody);
		register(CubeModification.class, CubeModification::decodeBody);
		register(DigNotification.class, DigNotification::decodeBody);
		register(ResumePlayer.class, ResumePlayer::decodeBody);
		register(UpdatePosition.class, UpdatePosition::decodeBody);

		register(ConsoleOutput.class, ConsoleOutput::decodeBody);
		register(FlashMessage.class, FlashMessage::decodeBody);
		register(Hello.class, Hello::decodeBody);
		register(PlayerListUpdate.class, PlayerListUpdate::decodeBody);
		register(PlayerNamesUpdate.class, PlayerNamesUpdate::decodeBody);
		register(PlayerResumed.class, PlayerResumed::decodeBody);
		register(SingleSectionModificationEvent.class, SingleSectionModificationEvent::decodeBody);
		register(UpdateCoins.class, UpdateCoins::decodeBody);

	}

	private <M extends Message> void register(Class<M> messageClass, BufferUtil.Decoder<M> decoder) {
		register(new Entry<>(messageClass, decoder));
	}

	private void register(Entry<? extends Message> entry) {
		int code = codeToEntry.size();
		codeToEntry.add(entry);
		classToCode.put(entry.getMessageClass(), code);
	}

	public int getCodeForClass(Class<? extends Message> messageClass) {
		Integer code = classToCode.get(messageClass);
		if (code == null) {
			throw new RuntimeException("unregistered message class: " + messageClass);
		}
		return code;
	}

	/**
	 * Decodes a message from a network packet. The packet is expected to be in the initial state after receiving,
	 * that is, with the reader index positioned at the start of the packet body and the number of readable bytes
	 * equal to the packet body size.
	 */
	public Message decodePacket(StackdPacket packet) throws MessageDecodingException {
		try {
			Message message = decodePacketInternal(packet);
			if (packet.getBuffer().readableBytes() != 0) {
				throw new MessageDecodingException("unexpected extra bytes at end of packet");
			}
			return message;
		} catch (IndexOutOfBoundsException e) {
			throw new MessageDecodingException("index out of bounds, probably unexpected end of packet", e);
		}
	}

	private Message decodePacketInternal(StackdPacket packet) throws MessageDecodingException {
		int code = packet.getType();
		if (code < 0 || code >= codeToEntry.size()) {
			throw new RuntimeException("unknown message code: " + code);
		}
		Entry<?> entry = codeToEntry.get(code);
		return entry.getDecoder().decode(packet.getBuffer());
	}

	private static final class Entry<M extends Message> {

		private final Class<M> messageClass;
		private final BufferUtil.Decoder<M> decoder;

		public Entry(Class<M> messageClass, BufferUtil.Decoder<M> decoder) {
			this.messageClass = messageClass;
			this.decoder = decoder;
		}

		public Class<M> getMessageClass() {
			return messageClass;
		}

		public BufferUtil.Decoder<M> getDecoder() {
			return decoder;
		}

	}

}
