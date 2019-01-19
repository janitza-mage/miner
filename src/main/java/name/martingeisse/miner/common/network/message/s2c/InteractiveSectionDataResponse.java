/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message.s2c;

import name.martingeisse.miner.common.geometry.SectionId;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import name.martingeisse.miner.common.network.message.MessageTypeRegistry;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 *
 */
public final class InteractiveSectionDataResponse extends Message {

	private final SectionId sectionId;
	private final byte[] data;

	public InteractiveSectionDataResponse(SectionId sectionId, byte[] data) {
		this(sectionId, data, true);
	}

	private InteractiveSectionDataResponse(SectionId sectionId, byte[] data, boolean copy) {
		this.sectionId = sectionId;
		this.data = copy ? data.clone() : data;
	}

	public SectionId getSectionId() {
		return sectionId;
	}

	/**
	 * Note: Copies the data to retain immutability, so don't call this method unnecessarily.
	 */
	public byte[] getData() {
		return data.clone();
	}

	@Override
	public StackdPacket encodePacket() {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeZero(StackdPacket.HEADER_SIZE);
		sectionId.encode(buffer);
		buffer.writeBytes(data);
		return new StackdPacket(MessageTypeRegistry.INSTANCE.getCodeForClass(getClass()), buffer, false);
	}

	public static InteractiveSectionDataResponse decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		SectionId sectionId = SectionId.decode(buffer);
		byte[] data = new byte[buffer.readableBytes()];
		buffer.readBytes(data);
		return new InteractiveSectionDataResponse(sectionId, data);
	}

}
