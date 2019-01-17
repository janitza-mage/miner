/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message.c2s;

import name.martingeisse.miner.common.geometry.SectionId;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageCodes;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;

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
	public StackdPacket encodePacket() {
		StackdPacket packet = new StackdPacket(MessageCodes.C2S_INTERACTIVE_SECTION_DATA_REQUEST, SectionId.ENCODED_SIZE);
		sectionId.encode(packet.getBuffer());
		return packet;
	}

	public static InteractiveSectionDataRequest decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		return new InteractiveSectionDataRequest(SectionId.decode(buffer));
	}

}
