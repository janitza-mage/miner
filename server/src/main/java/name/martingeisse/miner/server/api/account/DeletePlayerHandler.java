/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.api.account;

import com.querydsl.sql.dml.SQLUpdateClause;
import name.martingeisse.api.request.ApiRequestCycle;
import name.martingeisse.common.javascript.analyze.JsonAnalyzer;
import name.martingeisse.common.javascript.jsonbuilder.JsonValueBuilder;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.entities.Player;
import name.martingeisse.miner.server.entities.QPlayer;
import name.martingeisse.miner.server.entities.UserAccount;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

/**
 * This handler soft-deletes a player.
 */
public final class DeletePlayerHandler extends AbstractLoggedInHandler {

	/* (non-Javadoc)
	 * @see name.martingeisse.miner.server.api.account.AbstractLoggedInHandler#handle(name.martingeisse.api.request.RequestCycle, name.martingeisse.common.javascript.analyze.JsonAnalyzer, name.martingeisse.common.javascript.jsonbuilder.JsonValueBuilder, name.martingeisse.webide.entity.UserAccount)
	 */
	@Override
	protected void handle(ApiRequestCycle requestCycle, JsonAnalyzer input, JsonValueBuilder<?> output, UserAccount userAccount) throws Exception {
		QPlayer qp = QPlayer.Player;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			Player player = AccountApiUtil.fetchPlayer(input, userAccount);
			SQLUpdateClause update = connection.update(qp);
			update.where(qp.id.eq(player.getId())).set(qp.deleted, true).execute();
			output.object().end();
		}
	}

}
