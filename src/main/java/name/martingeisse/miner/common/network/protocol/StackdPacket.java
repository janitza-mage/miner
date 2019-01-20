/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.network.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.DataOutput;

/**
 * Represents packets that are sent between client and server.
 * The packet consists of header data (including the packet
 * type) and a body that is treated as a buffer by this class.
 * <p>
 * Conceptually, header and body are separate. Implementation-wise
 * this class uses a single buffer that contains the body and
 * leaves room for header fields. For received packets, the
 * header data is stored redundantly in the buffer and the fields
 * of this class. For sent packets, this class encodes the header
 * fields into the buffer when sending.
 * <p>
 * The packet type tells which kind of packet this is. Packet
 * types are 16-bit unsigned integers (0x0000 through 0xFFFF),
 * with packet types in the range 0xFF00 through 0xFFFF being
 * reserved for special protocol packets.
 * <p>
 * The total packet size (and thus buffer size) must not
 * exceed 255 bytes.
 * <p>
 * Wire format for a packet:
 * - body size (unsigned byte)
 * - type (unsigned short)
 * - body (bytes)
 * <p>
 * When sending a packet, the readable bytes determine the
 * packet's contents (header and body), with the header being
 * assembled in the buffer on-the-fly.
 * <p>
 * When receiving a packet, the header is consumed by the
 * receiver logic and the readable bytes are set up to contain
 * the packet body.
 */
public final class StackdPacket {

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
	 * the type
	 */
	private int type;

	/**
	 * the buffer
	 */
	private final ChannelBuffer buffer;

	/**
	 * Constructor for a new packet with an empty buffer.
	 *
	 * @param type     the packet type
	 * @param bodySize the size of the packet body
	 */
	public StackdPacket(int type, int bodySize) {
		this(type, ChannelBuffers.buffer(HEADER_SIZE + bodySize), true);
	}

	/**
	 * Constructor that specifies the type and buffer explicitly.
	 * <p>
	 * This constructor allows to optionally write a header full of
	 * zero-bytes. Callers *must* make sure that the buffer contains
	 * space for the header in the finished buffer; they can either
	 * do that by telling this constructor to do it or do it
	 * themselves.
	 *
	 * @param type            the packet type
	 * @param buffer          the buffer for header and body
	 * @param writeZeroHeader whether this class shall write zeroes for the header
	 */
	public StackdPacket(int type, ChannelBuffer buffer, boolean writeZeroHeader) {
		this.type = type;
		this.buffer = buffer;
		if (writeZeroHeader) {
			buffer.writeZero(HEADER_SIZE);
		}
	}

	/**
	 * Getter method for the type.
	 *
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Setter method for the type.
	 *
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Getter method for the buffer.
	 *
	 * @return the buffer
	 */
	public ChannelBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Encodes header fields into the buffer. Also sets the reader index to the
	 * beginning and the writer index to the end of the buffer.
	 */
	public void encodeHeader() {

		// determine packet size and make sure we can accept it
		int packetSize = buffer.readableBytes();
		if (packetSize < HEADER_SIZE) {
			throw new IllegalArgumentException("buffer is too small for header, readable bytes: " + packetSize);
		}
		if (packetSize > MAX_PACKET_SIZE) {
			throw new IllegalArgumentException("packet is too large, size: " + packetSize);
		}

		// assemble header fields
		int previousWriterIndex = buffer.writerIndex();
		buffer.writerIndex(buffer.readerIndex());
		buffer.writeShort(packetSize - HEADER_SIZE);
		buffer.writeShort(type);
		buffer.writerIndex(previousWriterIndex);

	}

	public String readableBytesToString(int limit) {
		StringBuilder builder = new StringBuilder();
		int previousReaderIndex = buffer.readerIndex();
		boolean first = true;
		while (buffer.readableBytes() > 0) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}
			if (limit > 0) {
				builder.append(buffer.readUnsignedByte());
				limit--;
			} else {
				builder.append("...");
				break;
			}
		}
		buffer.readerIndex(previousReaderIndex);
		return builder.toString();
	}

}
