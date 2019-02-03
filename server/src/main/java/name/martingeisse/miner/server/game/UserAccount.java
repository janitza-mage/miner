/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.game;

import com.google.common.collect.ImmutableList;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import name.martingeisse.miner.common.Faction;
import name.martingeisse.miner.common.network.c2s.request.CreatePlayerRequest;
import name.martingeisse.miner.common.network.c2s.request.DeletePlayerRequest;
import name.martingeisse.miner.common.network.s2c.response.CreatePlayerResponse;
import name.martingeisse.miner.common.network.s2c.response.LoginResponse;
import name.martingeisse.miner.common.network.s2c.response.OkayResponse;
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

	public CreatePlayerResponse createPlayer(CreatePlayerRequest request) {
		String name = request.getName();
		Faction faction = request.getFaction();
		long coins = 0;
		long playerId;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			final QPlayerRow qp = QPlayerRow.Player;
			final SQLInsertClause insert = connection.insert(qp);
			insert.set(qp.userAccountId, this.id);
			insert.set(qp.coins, coins);
			insert.set(qp.name, name);
			insert.set(qp.faction, faction.ordinal());
			insert.set(qp.x, BigDecimal.ZERO);
			insert.set(qp.y, BigDecimal.ONE.add(BigDecimal.ONE));
			insert.set(qp.z, BigDecimal.ZERO);
			insert.set(qp.leftAngle, BigDecimal.ZERO);
			insert.set(qp.upAngle, BigDecimal.ZERO);
			playerId = insert.executeWithKey(Long.class);
		}
		return new CreatePlayerResponse(new LoginResponse.Element(playerId, name, faction, coins));
	}

	public OkayResponse deletePlayer(DeletePlayerRequest request) {
		QPlayerRow qp = QPlayerRow.Player;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLUpdateClause update = connection.update(qp);
			update.where(qp.userAccountId.eq(this.id));
			update.where(qp.id.eq(request.getId()));
			update.where(qp.deleted.isFalse());
			long result = update.set(qp.deleted, true).execute();
			if (result > 0) {
				return new OkayResponse();
			} else {
				throw new UserVisibleMessageException("player not found");
			}
		}
	}

}
