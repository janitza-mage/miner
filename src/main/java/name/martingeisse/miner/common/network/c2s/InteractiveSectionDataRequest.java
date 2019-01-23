/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;
import name.martingeisse.miner.common.section.SectionId;

/**
 *
 */
public final class InteractiveSectionDataRequest extends Message {

	private final SectionId sectionId;

	public InteractiveSectionDataRequest(SectionId sectionId) {
		this.sectionId = sectionId;
	}

	public SectionId getSectionId() {
		return sectionId;
	}

	@Override
	protected int getExpectedBodySize() {
		return SectionId.ENCODED_SIZE;
	}

	@Override
	protected void encodeBody(ByteBuf buffer) {
		sectionId.encode(buffer);
	}

	public static InteractiveSectionDataRequest decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new InteractiveSectionDataRequest(SectionId.decode(buffer));
	}

}
