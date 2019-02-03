/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.game;

import com.google.common.collect.ImmutableList;
import com.querydsl.sql.dml.SQLInsertClause;
import name.martingeisse.api.handler.jsonapi.JsonApiException;
import name.martingeisse.common.javascript.jsonbuilder.JsonObjectBuilder;
import name.martingeisse.miner.common.Faction;
import name.martingeisse.miner.common.network.c2s.request.CreatePlayerRequest;
import name.martingeisse.miner.common.network.c2s.request.DeletePlayerRequest;
import name.martingeisse.miner.common.network.s2c.response.LoginResponse;
import name.martingeisse.miner.common.util.UserVisibleMessageException;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.postgres_entities.PlayerRow;
import name.martingeisse.miner.server.postgres_entities.QPlayerRow;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

import java.math.BigDecimal;
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
		final QPlayerRow qp = QPlayerRow.Player;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			List<LoginResponse.Element> responseElements = new ArrayList<>();
			for (PlayerRow playerRow : connection.query().select(qp).from(qp).where(qp.userAccountId.eq(id), qp.deleted.isFalse()).fetch()) {
				responseElements.add(new LoginResponse.Element(playerRow.getId(), playerRow.getName(),
					Faction.values()[playerRow.getFaction()], playerRow.getCoins()));
			}
			return new LoginResponse(ImmutableList.copyOf(responseElements));
		}
	}

	public void createPlayer(CreatePlayerRequest request) {
//		long playerId;
//		try (PostgresConnection connection = Databases.main.newConnection()) {
//			final QPlayerRow qp = QPlayerRow.Player;
//			final SQLInsertClause insert = connection.insert(qp);
//			insert.set(qp.userAccountId, id);
//			insert.set(qp.coins, 0L);
//			insert.set(qp.name, request.getName());
//			insert.set(qp.faction, request.getFaction().ordinal());
//			insert.set(qp.x, BigDecimal.ZERO);
//			insert.set(qp.y, BigDecimal.ONE.add(BigDecimal.ONE));
//			insert.set(qp.z, BigDecimal.ZERO);
//			insert.set(qp.leftAngle, BigDecimal.ZERO);
//			insert.set(qp.upAngle, BigDecimal.ZERO);
//			playerId = insert.executeWithKey(Long.class);
//		}
//
//		// build the response
//		JsonObjectBuilder<?> objectBuilder = output.object();
//		objectBuilder.property("id").number(playerId);
//		objectBuilder.end();


		throw new UserVisibleMessageException("NOT IMPLEMENTED");
	}

	public void deletePlayer(DeletePlayerRequest request) {
		throw new UserVisibleMessageException("NOT IMPLEMENTED");
	}

}
