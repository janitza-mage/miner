package name.martingeisse.miner.common.network.message.s2c;

import name.martingeisse.miner.common.geometry.SectionId;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageCodes;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 *
 */
public final class SingleSectionModificationEvent extends Message {

	private final SectionId sectionId;

	public SingleSectionModificationEvent(SectionId sectionId) {
		this.sectionId = sectionId;
	}

	public SectionId getSectionId() {
		return sectionId;
	}

	@Override
	public StackdPacket encodePacket() {
		StackdPacket packet = new StackdPacket(MessageCodes.S2C_SINGLE_SECTION_MODIFICATION_EVENT, SectionId.ENCODED_SIZE);
		ChannelBuffer buffer = packet.getBuffer();
		sectionId.encode(buffer);
		return packet;
	}

	public static SingleSectionModificationEvent decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		validateSize(buffer, SectionId.ENCODED_SIZE);
		return new SingleSectionModificationEvent(SectionId.decode(buffer));
	}

}
