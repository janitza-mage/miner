/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.game;

import name.martingeisse.common.SecurityTokenUtil;
import name.martingeisse.common.javascript.jsonbuilder.JsonListBuilder;
import name.martingeisse.common.javascript.jsonbuilder.JsonObjectBuilder;
import name.martingeisse.miner.common.network.c2s.request.CreatePlayerRequest;
import name.martingeisse.miner.common.network.c2s.request.DeletePlayerRequest;
import name.martingeisse.miner.common.network.s2c.response.LoginResponse;
import name.martingeisse.miner.common.util.UserVisibleMessageException;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.MinerServerSecurityConstants;
import name.martingeisse.miner.server.postgres_entities.PlayerRow;
import name.martingeisse.miner.server.postgres_entities.QPlayerRow;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class UserAccount {

	private final long id;

	public UserAccount(long id) {
		this.id = id;
	}

	public LoginResponse getLoginResponse() {
//		final QPlayerRow qp = QPlayerRow.Player;
//		try (PostgresConnection connection = Databases.main.newConnection()) {
//			List<LoginResponse.Element> responseElements = new ArrayList<>();
//			for (PlayerRow player : connection.query().select(qp).from(qp).where(qp.userAccountId.eq(id), qp.deleted.isFalse()).fetch()) {
//				responseElements.add(new LoginResponse.Element(player.getId(), player.getName(), player.getf));
//				JsonObjectBuilder<?> playerBuilder = listBuilder.element().object();
//				playerBuilder.property("id").number(player.getId());
//				playerBuilder.property("name").string(player.getName());
//				playerBuilder.property("faction").number(player.getFactionId());
//				playerBuilder.end();
//			}
//			listBuilder.end();
//			objectBuilder.end();
//		}



		throw new UserVisibleMessageException("NOT IMPLEMENTED");
	}

	public void createPlayer(CreatePlayerRequest request) {
		throw new UserVisibleMessageException("NOT IMPLEMENTED");
	}

	public void deletePlayer(DeletePlayerRequest request) {
		throw new UserVisibleMessageException("NOT IMPLEMENTED");
	}

}
