/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c;

import name.martingeisse.miner.common.geometry.SectionId;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;

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
	protected int getExpectedBodySize() {
		return -1;
	}

	@Override
	protected void encodeBody(ChannelBuffer buffer) {
		sectionId.encode(buffer);
		buffer.writeBytes(data);
	}

	public static InteractiveSectionDataResponse decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		SectionId sectionId = SectionId.decode(buffer);
		byte[] data = new byte[buffer.readableBytes()];
		buffer.readBytes(data);
		return new InteractiveSectionDataResponse(sectionId, data);
	}

}
