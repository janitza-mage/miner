/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.api.account;

import name.martingeisse.api.handler.jsonapi.JsonApiException;
import name.martingeisse.common.SecurityTokenUtil;
import name.martingeisse.common.javascript.analyze.JsonAnalyzer;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.MinerServerSecurityConstants;
import name.martingeisse.miner.server.entities.Player;
import name.martingeisse.miner.server.entities.QPlayer;
import name.martingeisse.miner.server.entities.UserAccount;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;
import org.joda.time.Instant;

/**
 * Common helper methods for the account API.
 */
final class AccountApiUtil {

	/**
	 * Builds a player access token for the player with the specified ID.
	 *
	 * @param playerId the player's ID
	 * @return the token
	 */
	public static String createPlayerAccessToken(long playerId) {
		return createPlayerAccessToken(Long.toString(playerId));
	}

	/**
	 * Builds a player access token for the player with the specified ID (already converted
	 * to a string).
	 *
	 * @param playerIdText the player's ID, converted to a string
	 * @return the token
	 */
	public static String createPlayerAccessToken(String playerIdText) {
		Instant expiryTime = new Instant().plus(MinerServerSecurityConstants.PLAYER_ACCESS_TOKEN_MAX_AGE_MILLISECONDS);
		return SecurityTokenUtil.createToken(playerIdText, expiryTime, MinerServerSecurityConstants.SECURITY_TOKEN_SECRET);
	}

	/**
	 * Fetches one of the user's players. The player ID is specified in the field "playerId"
	 * in the reuqest data.
	 * <p>
	 * This method throws a {@link JsonApiException} if the player could not be fetched or
	 * if the input data has no "playerId" field.
	 *
	 * @param input       the input request data
	 * @param userAccount the user's account
	 * @return the player
	 */
	public static Player fetchPlayer(JsonAnalyzer input, UserAccount userAccount) {
		final long playerId = input.analyzeMapElement("playerId").expectLong();
		final QPlayer qp = QPlayer.Player;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			Player player = connection.query().select(qp).from(qp).where(qp.id.eq(playerId), qp.userAccountId.eq(userAccount.getId()), qp.deleted.isFalse()).fetchOne();
			if (player == null) {
				throw new JsonApiException(1, "player not found");
			}
			return player;
		}
	}

}
