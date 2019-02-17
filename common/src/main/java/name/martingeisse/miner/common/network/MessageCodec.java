/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.log4j.Logger;

/**
 * Netty handler that decodes and encodes between {@link Message}s and raw buffers. Other payloads are ignored.
 */
public class MessageCodec extends ChannelDuplexHandler {

	private static Logger logger = Logger.getLogger(MessageCodec.class);

	/**
	 * The number of bytes for the header.
	 */
	public static final int HEADER_SIZE = 4;

	/**
	 * The maximum size for the body of a single packet.
	 */
	public static final int MAX_BODY_SIZE = 65535;

	/**
	 * The maximum size for a whole packet.
	 */
	public static final int MAX_PACKET_SIZE = HEADER_SIZE + MAX_BODY_SIZE;

	/**
	 * Creates a Netty handler that splits and merges raw buffers
	 * according to packet frame boundaries. Such a handler is needed
	 * downstream of the {@link MessageCodec}.
	 *
	 * @return the handler
	 */
	public static ChannelHandler createFrameDecoder() {
		return new LengthFieldBasedFrameDecoder(MAX_PACKET_SIZE, 0, 2, 2, 0, true);
	}

	@Override
	public void channelRead(ChannelHandlerContext context, Object payload) throws Exception {
		if (payload instanceof ByteBuf) {
			ByteBuf buffer = (ByteBuf) payload;
			buffer.setIndex(2, buffer.capacity());
			int messageTypeCode = buffer.readUnsignedShort();
			logger.debug("received message of type: " + messageTypeCode);
			Message message = MessageTypeRegistry.INSTANCE.decodePacket(messageTypeCode, buffer);
			buffer.release();
			logger.debug("message object: " + message);
			context.fireChannelRead(message);
		} else {
			logger.debug("received message: " + payload);
			context.fireChannelRead(payload);
		}
	}

	@Override
	public void write(ChannelHandlerContext context, Object payload, ChannelPromise promise) throws Exception {
		logger.debug("sending message: " + payload);
		if (payload instanceof Message) {

			// analyze message
			Message message = (Message) payload;
			int messageTypeCode = MessageTypeRegistry.INSTANCE.getTypeCodeForClass(message.getClass());
			int expectedBodySize = message.getExpectedBodySize();

			// assemble a packet with expected size, empty header but actual message body
			ByteBuf buffer = expectedBodySize < 0 ? Unpooled.buffer() : Unpooled.buffer(HEADER_SIZE + expectedBodySize);
			buffer.writeZero(HEADER_SIZE);
			message.encodeBody(buffer);
			int packetSize = buffer.readableBytes();
			if (packetSize > MAX_PACKET_SIZE) {
				throw new IllegalArgumentException("packet is too large, size: " + packetSize);
			}

			// assemble header fields
			int previousWriterIndex = buffer.writerIndex();
			buffer.writerIndex(buffer.readerIndex());
			buffer.writeShort(packetSize - HEADER_SIZE);
			buffer.writeShort(messageTypeCode);
			buffer.writerIndex(previousWriterIndex);

			// send message
			context.write(buffer, promise);

		} else {
			context.write(payload, promise);
		}
	}

}
