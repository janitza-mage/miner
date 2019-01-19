/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.network;

import com.google.common.collect.ImmutableList;
import com.querydsl.sql.dml.SQLUpdateClause;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.s2c.ConsoleOutput;
import name.martingeisse.miner.common.network.message.s2c.FlashMessage;
import name.martingeisse.miner.common.network.message.s2c.UpdateCoins;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.entities.QPlayer;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Stores the data for one user session (currently associated with the connection,
 * but intended to service connection dropping and re-connecting).
 * <p>
 * Application code may subclass this class to add application-specific
 * per-session data.
 */
public class StackdSession {

	private static Logger logger = Logger.getLogger(StackdSession.class);

	private final int id;
	private final Channel channel;

	private volatile Long playerId;
	private volatile double x;
	private volatile double y;
	private volatile double z;
	private volatile double leftAngle;
	private volatile double upAngle;
	private volatile String name;

	/**
	 * Constructor.
	 *
	 * @param id      the session ID
	 * @param channel the channel that connects to the client
	 */
	public StackdSession(final int id, final Channel channel) {
		this.id = id;
		this.channel = channel;
		this.name = "Player";
	}

	/**
	 * Getter method for the id.
	 *
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Getter method for the channel.
	 *
	 * @return the channel
	 */
	public final Channel getChannel() {
		return channel;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(final Long playerId) {
		this.playerId = playerId;
	}

	public double getX() {
		return x;
	}

	public void setX(final double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(final double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(final double z) {
		this.z = z;
	}

	public double getLeftAngle() {
		return leftAngle;
	}

	public void setLeftAngle(final double leftAngle) {
		this.leftAngle = leftAngle;
	}

	public double getUpAngle() {
		return upAngle;
	}

	public void setUpAngle(final double upAngle) {
		this.upAngle = upAngle;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
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
	 *
	 * @param message the message
	 */
	public final void sendFlashMessage(String message) {
		send(new FlashMessage(message));
	}

	/**
	 * Sends console output lines to the client.
	 *
	 * @param lines the lines to send
	 */
	public final void sendConsoleOutput(Collection<String> lines) {
		if (!lines.isEmpty()) {
			sendConsoleOutput(lines.toArray(new String[lines.size()]));
		}
	}

	/**
	 * Sends console output lines to the client.
	 *
	 * @param lines the lines to send
	 */
	public final void sendConsoleOutput(String... lines) {
		if (lines.length > 0) {
			send(new ConsoleOutput(ImmutableList.copyOf(lines)));
		}
	}

	/**
	 * Handles disconnected clients.
	 */
	public void handleDisconnect() {
		if (playerId != null) {
			try (PostgresConnection connection = Databases.main.newConnection()) {
				QPlayer qp = QPlayer.Player;
				final SQLUpdateClause update = connection.update(qp);
				update.where(qp.id.eq(playerId));
				update.set(qp.x, new BigDecimal(x));
				update.set(qp.y, new BigDecimal(y));
				update.set(qp.z, new BigDecimal(z));
				update.set(qp.leftAngle, new BigDecimal(leftAngle));
				update.set(qp.upAngle, new BigDecimal(upAngle));
				update.execute();
			}
		}
	}

	/**
	 * Sends an update for the number of coins to the client, fetching the
	 * number of coins from the database.
	 */
	public void sendCoinsUpdate() {
		if (playerId == null) {
			sendCoinsUpdate(0);
		} else {
			try (PostgresConnection connection = Databases.main.newConnection()) {
				QPlayer qp = QPlayer.Player;
				Long coins = connection.query().select(qp.coins).from(qp).where(qp.id.eq(playerId)).fetchFirst();
				sendCoinsUpdate(coins == null ? 0 : coins);
			}
		}
	}

	/**
	 * Sends an update for the number of coins to the client.
	 *
	 * @param coins the number of coins to send
	 */
	public void sendCoinsUpdate(final long coins) {
		send(new UpdateCoins(coins));
	}

}
