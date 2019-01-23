package name.martingeisse.miner.common.network.s2c;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;
import name.martingeisse.miner.common.section.SectionId;

/**
 * Sent by the server when a section gets modified. Clients that are close enough to be interested in the update would
 * typically request the new section render model and collider in turn. Clients that are too far away would ignore
 * these events. TODO: store the client's rough position on the server, filter mod events server-side, then just send
 * the updated objects. This slightly increases network traffic (sending additional data) but reduces latency and
 * simplifies the code.
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
	protected int getExpectedBodySize() {
		return SectionId.ENCODED_SIZE;
	}

	@Override
	protected void encodeBody(ByteBuf buffer) {
		sectionId.encode(buffer);
	}

	public static SingleSectionModificationEvent decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new SingleSectionModificationEvent(SectionId.decode(buffer));
	}

}
