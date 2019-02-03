/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.api.account;

import name.martingeisse.api.request.ApiRequestCycle;
import name.martingeisse.common.javascript.analyze.JsonAnalyzer;
import name.martingeisse.common.javascript.jsonbuilder.JsonObjectBuilder;
import name.martingeisse.common.javascript.jsonbuilder.JsonValueBuilder;
import name.martingeisse.miner.server.postgres_entities.PlayerRow;
import name.martingeisse.miner.server.postgres_entities.UserAccountRow;

/**
 * This handler returns detailed data for one of the logged-in user's player characters.
 */
public final class PlayerDetailsHandler extends AbstractLoggedInHandler {

	/* (non-Javadoc)
	 * @see name.martingeisse.miner.server.api.account.AbstractLoggedInHandler#handle(name.martingeisse.api.request.RequestCycle, name.martingeisse.common.javascript.analyze.JsonAnalyzer, name.martingeisse.common.javascript.jsonbuilder.JsonValueBuilder, name.martingeisse.webide.entity.UserAccount)
	 */
	@Override
	protected void handle(ApiRequestCycle requestCycle, JsonAnalyzer input, JsonValueBuilder<?> output, UserAccountRow userAccount) throws Exception {
		PlayerRow player = AccountApiUtil.fetchPlayer(input, userAccount);
		JsonObjectBuilder<?> objectBuilder = output.object();
		objectBuilder.property("id").number(player.getId());
		objectBuilder.property("name").string(player.getName());
		objectBuilder.property("faction").number(player.getFaction());
		objectBuilder.property("coins").number(player.getCoins());
		objectBuilder.end();
	}

}
