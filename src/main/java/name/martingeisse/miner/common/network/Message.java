/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Base class for all network messages. These messages carry data between client and server. They will be encoded and
 * decoded by the {@link MessageCodec}.
 * <p>
 * Messages are immutable by design to simplify handling them. They are not value objects though; especially, equals /
 * hashCode support is based on identity.
 */
public abstract class Message {

	/**
	 * Returns the expected body size, if known before actually encoding a packet, or a negative value to indicate that
	 * the size can only be determined by actually encoding the packet and a dynamic buffer is therefore needed.
	 */
	protected abstract int getExpectedBodySize();

	/**
	 * Encodes the packet body to the specified buffer.
	 */
	protected abstract void encodeBody(ChannelBuffer buffer);

}
