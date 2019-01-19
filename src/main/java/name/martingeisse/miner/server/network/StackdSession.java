/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.network;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.s2c.ConsoleOutput;
import name.martingeisse.miner.common.network.message.s2c.FlashMessage;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.Collection;

/**
 * Stores the data for one user session (currently associated with the connection,
 * but intended to service connection dropping and re-connecting).
 *
 * Application code may subclass this class to add application-specific
 * per-session data.
 */
public class StackdSession {

	private static Logger logger = Logger.getLogger(StackdSession.class);

	/**
	 * the id
	 */
	private final int id;

	/**
	 * the channel
	 */
	private final Channel channel;

	/**
	 * Constructor.
	 * @param id the session ID
	 * @param channel the channel that connects to the client
	 */
	public StackdSession(final int id, final Channel channel) {
		this.id = id;
		this.channel = channel;
	}

	/**
	 * Getter method for the id.
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Getter method for the channel.
	 * @return the channel
	 */
	public final Channel getChannel() {
		return channel;
	}

	/**
	 * Sends a network packet to the client that owns this session.
	 * The packet object should be considered invalid afterwards
	 * (hence "destructive") since this method will assemble header
	 * fields in the packet and alter its reader/writer index,
	 * possibly asynchronous to the calling thread.
	 *
	 * @param packet the packet to send
	 */
	public final void sendPacketDestructive(StackdPacket packet) {
		if (logger.isDebugEnabled()) {
			logger.debug("server is going to send packet " + packet.getType() + ": " + packet.readableBytesToString(10));
		}
		channel.write(packet);
	}

	public final void send(Message message) {
		sendPacketDestructive(message.encodePacket());
	}

	/**
	 * Sends a flash message to the client that owns this session.
	 * @param message the message
	 */
	public final void sendFlashMessage(String message) {
		send(new FlashMessage(message));
	}

	/**
	 * Sends console output lines to the client.
	 * @param lines the lines to send
	 */
	public final void sendConsoleOutput(Collection<String> lines) {
		if (!lines.isEmpty()) {
			sendConsoleOutput(lines.toArray(new String[lines.size()]));
		}
	}

	/**
	 * Sends console output lines to the client.
	 * @param lines the lines to send
	 */
	public final void sendConsoleOutput(String... lines) {
		if (lines.length > 0) {
			send(new ConsoleOutput(ImmutableList.copyOf(lines)));
		}
	}

}
