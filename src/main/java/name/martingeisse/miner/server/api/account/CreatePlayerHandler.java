/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.api.account;

import com.querydsl.sql.dml.SQLInsertClause;
import name.martingeisse.api.handler.jsonapi.JsonApiException;
import name.martingeisse.api.request.ApiRequestCycle;
import name.martingeisse.common.javascript.analyze.JsonAnalyzer;
import name.martingeisse.common.javascript.jsonbuilder.JsonObjectBuilder;
import name.martingeisse.common.javascript.jsonbuilder.JsonValueBuilder;
import name.martingeisse.miner.common.Faction;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.entities.QPlayer;
import name.martingeisse.miner.server.entities.UserAccount;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

import java.math.BigDecimal;

/**
 * This handler creates a player character for the logged-in user.
 */
public final class CreatePlayerHandler extends AbstractLoggedInHandler {

	/* (non-Javadoc)
	 * @see name.martingeisse.miner.server.api.account.AbstractLoggedInHandler#handle(name.martingeisse.api.request.RequestCycle, name.martingeisse.common.javascript.analyze.JsonAnalyzer, name.martingeisse.common.javascript.jsonbuilder.JsonValueBuilder, name.martingeisse.webide.entity.UserAccount)
	 */
	@Override
	protected void handle(ApiRequestCycle requestCycle, JsonAnalyzer input, JsonValueBuilder<?> output, UserAccount userAccount) throws Exception {

		// analyze request data
		int factionIndex = input.analyzeMapElement("faction").expectInteger();
		Faction[] factions = Faction.values();
		if (factionIndex < 0 || factionIndex >= factions.length) {
			throw new JsonApiException(1, "invalid faction index: " + factionIndex);
		}
		String name = input.analyzeMapElement("name").expectString();
		if (name.isEmpty()) {
			throw new JsonApiException(1, "empty character name not allowed");
		}

		// create the player character
		long playerId;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			final QPlayer qp = QPlayer.Player;
			final SQLInsertClause insert = connection.insert(qp);
			insert.set(QPlayer.Player.userAccountId, userAccount.getId());
			insert.set(QPlayer.Player.coins, 0L);
			insert.set(QPlayer.Player.name, name);
			insert.set(QPlayer.Player.factionId, (long) factionIndex);
			insert.set(QPlayer.Player.x, BigDecimal.ZERO);
			insert.set(QPlayer.Player.y, BigDecimal.ONE.add(BigDecimal.ONE));
			insert.set(QPlayer.Player.z, BigDecimal.ZERO);
			insert.set(QPlayer.Player.leftAngle, BigDecimal.ZERO);
			insert.set(QPlayer.Player.upAngle, BigDecimal.ZERO);
			playerId = insert.executeWithKey(Long.class);
		}

		// build the response
		JsonObjectBuilder<?> objectBuilder = output.object();
		objectBuilder.property("id").number(playerId);
		objectBuilder.end();

	}

}
