/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message;

import name.martingeisse.miner.common.network.StackdPacket;

/**
 * Base class for all network messages. These messages carry data between client and server. They can be encoded and
 * decoded to {@link StackdPacket}s for transfer.
 * <p>
 * Messages are immutable by design to simplify handling them. They are not value objects though; especially, equals /
 * hashCode support is based on identity.
 */
public abstract class Message {

	/**
	 * Encodes this message to a new network packet.
	 */
	public abstract StackdPacket encodePacket();

}
