/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.game;

import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.vector.Vector3d;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.entities.Player;
import name.martingeisse.miner.server.entities.QPlayer;
import name.martingeisse.miner.server.entities.QPlayerAwardedAchievement;
import name.martingeisse.miner.server.network.Avatar;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

import java.math.BigDecimal;

/**
 * TODO rename {@link Player} to PlayerRow and rename this class to Player.
 */
public final class PlayerAccess {

	private final long id;

	public PlayerAccess(long id) {
		this.id = id;
		loadPlayerRow();
	}

	private Player loadPlayerRow() {
		try (PostgresConnection connection = Databases.main.newConnection()) {
			QPlayer qp = QPlayer.Player;
			Player player = connection.query().select(qp).from(qp).where(qp.id.eq(id)).fetchOne();
			if (player == null) {
				throw new RuntimeException("player not found, id: " + id);
			}
			return player;
		}
	}

	public long getId() {
		return id;
	}

	//
	// ------------------------------------------------------------------------------------------------------------
	//

	public boolean addAchievement(String achievementCode) {
		QPlayerAwardedAchievement qpaa = QPlayerAwardedAchievement.PlayerAwardedAchievement;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLInsertClause insert = connection.insert(qpaa);
			insert.set(qpaa.playerId, id);
			insert.set(qpaa.achievementCode, achievementCode);
			return (insert.execute() > 0);
		}
	}

	public boolean addCoins(long amount) {
		QPlayer qp = QPlayer.Player;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLUpdateClause update = connection.update(qp);
			update.where(qp.id.eq(id));
			update.set(qp.coins, qp.coins.add(amount));
			return (update.execute() > 0);
		}
	}

	public long getCoins() {
		QPlayer qp = QPlayer.Player;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			return connection.query().select(qp.coins).from(qp).where(qp.id.eq(id)).fetchFirst();
		}
	}

	public InventoryAccess getInventoryAccess() {
		return new InventoryAccess(id);
	}

	public void loadAvatar(Avatar avatar) {
		Player row = loadPlayerRow();
		avatar.setPosition(new Vector3d(row.getX().doubleValue(), row.getY().doubleValue(), row.getZ().doubleValue()));
		avatar.setOrientation(new EulerAngles(row.getLeftAngle().doubleValue(), row.getUpAngle().doubleValue(), 0));
		avatar.setName(row.getName());
	}

	public void saveAvatar(Avatar avatar) {
		QPlayer qp = QPlayer.Player;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLUpdateClause update = connection.update(qp);
			update.where(qp.id.eq(id));
			update.set(qp.x, BigDecimal.valueOf(avatar.getPosition().x));
			update.set(qp.y, BigDecimal.valueOf(avatar.getPosition().y));
			update.set(qp.z, BigDecimal.valueOf(avatar.getPosition().z));
			update.set(qp.leftAngle, BigDecimal.valueOf(avatar.getOrientation().horizontalAngle));
			update.set(qp.upAngle, BigDecimal.valueOf(avatar.getOrientation().verticalAngle));
			// note: cannot change the player's name this way
			update.execute();
		}
	}

}
