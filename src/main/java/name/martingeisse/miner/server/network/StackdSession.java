/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.network;

import com.google.common.collect.ImmutableList;
import com.querydsl.sql.dml.SQLUpdateClause;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.s2c.*;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.entities.Player;
import name.martingeisse.miner.server.entities.QPlayer;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;
import org.apache.log4j.Logger;

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
	private final ServerEndpoint endpoint;

	// player info
	private volatile Long playerId;

	// avatar info
	private volatile Avatar avatar;

	/**
	 * Note that a race condition might cause this constructor to be invoked twice to
	 * create a session for the same ID. In such a case, the race condition will be
	 * detected later on and one of the sessions will be thrown away. This method must
	 * be able to handle such a case.
	 */
	public StackdSession(int id, ServerEndpoint endpoint) {
		this.id = id;
		this.endpoint = endpoint;
	}

	public int getId() {
		return id;
	}

	public ServerEndpoint getEndpoint() {
		return endpoint;
	}

	//
	// database access
	//

	private static Player loadPlayerRow(long playerId) {
		try (PostgresConnection connection = Databases.main.newConnection()) {
			QPlayer qp = QPlayer.Player;
			Player player = connection.query().select(qp).from(qp).where(qp.id.eq(playerId)).fetchOne();
			if (player == null) {
				throw new RuntimeException("player not found, id: " + playerId);
			}
			return player;
		}
	}

	private Player loadPlayerRow() {
		return loadPlayerRow(playerId);
	}

	//
	// player management
	//

	public void selectPlayer(long playerId) {
		if (this.playerId != null) {
			throw new IllegalStateException("player already selected");
		}
		if (this.avatar != null) {
			throw new IllegalStateException("cannot select player -- avatar exists (state inconsistent)");
		}
		Player player = loadPlayerRow(playerId);
		this.playerId = playerId;
	}

	public Long getPlayerId() {
		return playerId;
	}

	//
	// avatar management
	//

	public void createAvatar() {
		if (playerId == null) {
			throw new IllegalStateException("cannot create avatar -- no player selected");
		}
		if (avatar != null) {
			throw new IllegalStateException("avatar already exists");
		}
		Player player = loadPlayerRow();
		avatar = new Avatar();
		avatar.copyFrom(player);
	}

	public Avatar getAvatar() {
		return avatar;
	}

	//
	// networking
	//

	/**
	 * Called after this session has been created.
	 * <p>
	 * As noted in the constructor comment, a race condition may cause multiple sessions to be created. This
	 * initialization method will be called only for the one that is actually kept.
	 */
	public void onConnect() {
		send(new Hello(id));
		sendFlashMessage("Connected to server.");
		sendCoinsUpdate();
	}

	/**
	 * Called when the client disconnects, just before the session gets removed from the server.
	 */
	public void onDisconnect() {
		if (playerId != null) {
			try (PostgresConnection connection = Databases.main.newConnection()) {
				Player patch = new Player();
				avatar.copyTo(patch);

				QPlayer qp = QPlayer.Player;
				SQLUpdateClause update = connection.update(qp);
				update.where(qp.id.eq(playerId));
				update.set(qp.x, patch.getX());
				update.set(qp.y, patch.getY());
				update.set(qp.z, patch.getZ());
				update.set(qp.leftAngle, patch.getLeftAngle());
				update.set(qp.upAngle, patch.getUpAngle());
				update.execute();
			}
		}
	}

	public void send(Message message) {
		endpoint.send(message);
	}

	/**
	 * Sends a flash message to the client that owns this session.
	 *
	 * @param message the message
	 */
	public void sendFlashMessage(String message) {
		send(new FlashMessage(message));
	}

	/**
	 * Sends console output lines to the client.
	 *
	 * @param lines the lines to send
	 */
	public void sendConsoleOutput(Collection<String> lines) {
		if (!lines.isEmpty()) {
			sendConsoleOutput(lines.toArray(new String[lines.size()]));
		}
	}

	/**
	 * Sends console output lines to the client.
	 *
	 * @param lines the lines to send
	 */
	public void sendConsoleOutput(String... lines) {
		if (lines.length > 0) {
			send(new ConsoleOutput(ImmutableList.copyOf(lines)));
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
	public void sendCoinsUpdate(long coins) {
		send(new UpdateCoins(coins));
	}

	void sendPlayerResumed() {
		send(new PlayerResumed(avatar.getPosition(), avatar.getOrientation()));
	}

}
