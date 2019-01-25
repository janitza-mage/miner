/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.network;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.s2c.ConsoleOutput;
import name.martingeisse.miner.common.network.s2c.FlashMessage;
import name.martingeisse.miner.common.network.s2c.PlayerResumed;
import name.martingeisse.miner.common.network.s2c.UpdateCoins;
import name.martingeisse.miner.server.game.PlayerAccess;
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
	private volatile PlayerAccess playerAccess;

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
	// player management
	//

	public void selectPlayer(long playerId) {
		if (playerAccess != null) {
			throw new IllegalStateException("player already selected");
		}
		if (avatar != null) {
			throw new IllegalStateException("cannot select player -- avatar exists (state inconsistent)");
		}
		playerAccess = new PlayerAccess(playerId);
	}

	public PlayerAccess getPlayerAccess() {
		return playerAccess;
	}

	//
	// avatar management
	//

	public void createAvatar() {
		if (playerAccess == null) {
			throw new IllegalStateException("cannot create avatar -- no player selected");
		}
		if (avatar != null) {
			throw new IllegalStateException("avatar already exists");
		}
		avatar = new Avatar();
		playerAccess.loadAvatar(avatar);
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
		sendFlashMessage("Connected to server.");
		sendCoinsUpdate();
	}

	/**
	 * Called when the client disconnects, just before the session gets removed from the server.
	 */
	public void onDisconnect() {
		if (playerAccess != null && avatar != null) {
			playerAccess.saveAvatar(avatar);
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
		sendCoinsUpdate(playerAccess == null ? 0 : playerAccess.getCoins());
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
