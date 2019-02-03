package name.martingeisse.miner.common.network;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.c2s.*;
import name.martingeisse.miner.common.network.c2s.request.CreatePlayerRequest;
import name.martingeisse.miner.common.network.c2s.request.DeletePlayerRequest;
import name.martingeisse.miner.common.network.c2s.request.LoginRequest;
import name.martingeisse.miner.common.network.s2c.*;
import name.martingeisse.miner.common.network.s2c.response.CreatePlayerResponse;
import name.martingeisse.miner.common.network.s2c.response.ErrorResponse;
import name.martingeisse.miner.common.network.s2c.response.LoginResponse;
import name.martingeisse.miner.common.network.s2c.response.OkayResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO remove packet construction from message classes here?
 */
final class MessageTypeRegistry {

	static final MessageTypeRegistry INSTANCE = new MessageTypeRegistry();

	private final List<Entry<?>> codeToEntry = new ArrayList<>();
	private final Map<Class<? extends Message>, Integer> classToCode = new HashMap<>();

	private MessageTypeRegistry() {

		// client-to-server requests
		register(LoginRequest.class, LoginRequest::decodeBody);
		register(CreatePlayerRequest.class, CreatePlayerRequest::decodeBody);
		register(DeletePlayerRequest.class, DeletePlayerRequest::decodeBody);

		// other client-to-server messages
		register(CubeModification.class, CubeModification::decodeBody);
		register(DigNotification.class, DigNotification::decodeBody);
		register(ResumePlayer.class, ResumePlayer::decodeBody);
		register(UpdatePosition.class, UpdatePosition::decodeBody);
		register(InteractiveSectionDataRequest.class, InteractiveSectionDataRequest::decodeBody);

		// server-to-client responses
		register(OkayResponse.class, OkayResponse::decodeBody);
		register(ErrorResponse.class, ErrorResponse::decodeBody);
		register(LoginResponse.class, LoginResponse::decodeBody);
		register(CreatePlayerResponse.class, CreatePlayerResponse::decodeBody);

		// other server-to-client messages
		register(FlashMessage.class, FlashMessage::decodeBody);
		register(PlayerListUpdate.class, PlayerListUpdate::decodeBody);
		register(PlayerResumed.class, PlayerResumed::decodeBody);
		register(SingleSectionModificationEvent.class, SingleSectionModificationEvent::decodeBody);
		register(UpdateCoins.class, UpdateCoins::decodeBody);
		register(InteractiveSectionDataResponse.class, InteractiveSectionDataResponse::decodeBody);
		register(UpdateInventory.class, UpdateInventory::decodeBody);

	}

	private <M extends Message> void register(Class<M> messageClass, BufferUtil.Decoder<M> decoder) {
		register(new Entry<>(messageClass, decoder));
	}

	private void register(Entry<? extends Message> entry) {
		int code = codeToEntry.size();
		codeToEntry.add(entry);
		classToCode.put(entry.getMessageClass(), code);
	}

	int getTypeCodeForClass(Class<? extends Message> messageClass) {
		Integer code = classToCode.get(messageClass);
		if (code == null) {
			throw new RuntimeException("unregistered message class: " + messageClass);
		}
		return code;
	}

	Message decodePacket(int messageTypeCode, ByteBuf bodyBuffer) throws MessageDecodingException {
		try {
			Message message = decodePacketInternal(messageTypeCode, bodyBuffer);
			if (bodyBuffer.readableBytes() != 0) {
				throw new MessageDecodingException("unexpected extra bytes at end of packet body buffer");
			}
			return message;
		} catch (IndexOutOfBoundsException e) {
			throw new MessageDecodingException("index out of bounds, probably unexpected end of packet body buffer", e);
		}
	}

	private Message decodePacketInternal(int messageTypeCode, ByteBuf bodyBuffer) throws MessageDecodingException {
		if (messageTypeCode < 0 || messageTypeCode >= codeToEntry.size()) {
			throw new RuntimeException("unknown message type code: " + messageTypeCode);
		}
		Entry<?> entry = codeToEntry.get(messageTypeCode);
		return entry.getDecoder().decode(bodyBuffer);
	}

	private static final class Entry<M extends Message> {

		private final Class<M> messageClass;
		private final BufferUtil.Decoder<M> decoder;

		Entry(Class<M> messageClass, BufferUtil.Decoder<M> decoder) {
			this.messageClass = messageClass;
			this.decoder = decoder;
		}

		Class<M> getMessageClass() {
			return messageClass;
		}

		BufferUtil.Decoder<M> getDecoder() {
			return decoder;
		}

	}

}
