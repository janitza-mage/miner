/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server;

import com.querydsl.sql.dml.SQLUpdateClause;
import name.martingeisse.miner.common.network.MinerPacketConstants;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.server.entities.QPlayer;
import name.martingeisse.miner.server.network.StackdSession;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.math.BigDecimal;

/**
 * Stores the data for one user session (currently associated with the connection,
 * but intended to service connection dropping and re-connecting).
 */
public class MinerSession extends StackdSession {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(MinerSession.class);

	/**
	 * the playerId
	 */
	private volatile Long playerId;

	/**
	 * the x
	 */
	private volatile double x;

	/**
	 * the y
	 */
	private volatile double y;

	/**
	 * the z
	 */
	private volatile double z;

	/**
	 * the leftAngle
	 */
	private volatile double leftAngle;

	/**
	 * the upAngle
	 */
	private volatile double upAngle;

	/**
	 * the name
	 */
	private volatile String name;

	/**
	 * Constructor.
	 * @param id the session ID
	 * @param channel the channel that connects to the client
	 */
	public MinerSession(final int id, final Channel channel) {
		super(id, channel);
		this.name = "Player";
	}

	/**
	 * Getter method for the playerId.
	 * @return the playerId
	 */
	public Long getPlayerId() {
		return playerId;
	}

	/**
	 * Setter method for the playerId.
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(final Long playerId) {
		this.playerId = playerId;
	}

	/**
	 * Getter method for the x.
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Setter method for the x.
	 * @param x the x to set
	 */
	public void setX(final double x) {
		this.x = x;
	}

	/**
	 * Getter method for the y.
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Setter method for the y.
	 * @param y the y to set
	 */
	public void setY(final double y) {
		this.y = y;
	}

	/**
	 * Getter method for the z.
	 * @return the z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Setter method for the z.
	 * @param z the z to set
	 */
	public void setZ(final double z) {
		this.z = z;
	}

	/**
	 * Getter method for the leftAngle.
	 * @return the leftAngle
	 */
	public double getLeftAngle() {
		return leftAngle;
	}

	/**
	 * Setter method for the leftAngle.
	 * @param leftAngle the leftAngle to set
	 */
	public void setLeftAngle(final double leftAngle) {
		this.leftAngle = leftAngle;
	}

	/**
	 * Getter method for the upAngle.
	 * @return the upAngle
	 */
	public double getUpAngle() {
		return upAngle;
	}

	/**
	 * Setter method for the upAngle.
	 * @param upAngle the upAngle to set
	 */
	public void setUpAngle(final double upAngle) {
		this.upAngle = upAngle;
	}

	/**
	 * Getter method for the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter method for the name.
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
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
	 * @param coins the number of coins to send
	 */
	public void sendCoinsUpdate(final long coins) {
		final StackdPacket packet = new StackdPacket(MinerPacketConstants.TYPE_S2C_UPDATE_COINS, 8);
		packet.getBuffer().writeLong(coins);
		sendPacketDestructive(packet);
	}

}
