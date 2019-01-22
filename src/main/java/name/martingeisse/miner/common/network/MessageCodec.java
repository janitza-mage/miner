/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.network;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

/**
 * Netty handler that decodes and encodes between {@link Message}s and raw buffers. Other payloads are ignored.
 */
public class MessageCodec extends SimpleChannelHandler {

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
	public static ChannelHandler createFrameCodec() {
		return new LengthFieldBasedFrameDecoder(MAX_PACKET_SIZE, 0, 2, 2, 0, true);
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		Object payload = e.getMessage();
		if (payload instanceof ChannelBuffer) {
			ChannelBuffer buffer = (ChannelBuffer)payload;
			buffer.setIndex(2, buffer.capacity());
			int messageTypeCode = buffer.readUnsignedShort();
			Message message = MessageTypeRegistry.INSTANCE.decodePacket(messageTypeCode, buffer);
			Channels.fireMessageReceived(ctx, message);
		} else {
			super.messageReceived(ctx, e);
		}
	}
	
	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		Object payload = e.getMessage();
		if (payload instanceof Message) {

			// analyze message
			Message message = (Message)payload;
			int messageTypeCode = MessageTypeRegistry.INSTANCE.getTypeCodeForClass(message.getClass());
			int expectedBodySize = message.getExpectedBodySize();

			// assemble a packet with expected size, empty header but actual message body
			ChannelBuffer buffer = expectedBodySize < 0 ? ChannelBuffers.dynamicBuffer() :
				ChannelBuffers.buffer(HEADER_SIZE + expectedBodySize);
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
			Channels.write(ctx, e.getFuture(), buffer);

		} else {
			super.writeRequested(ctx, e);
		}
	}
	
}
