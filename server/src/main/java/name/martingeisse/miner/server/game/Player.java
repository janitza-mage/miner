/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.game;

import com.querydsl.core.QueryException;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.vector.Vector3d;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.c2s.EquipMessage;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.network.Avatar;
import name.martingeisse.miner.server.network.StackdSession;
import name.martingeisse.miner.server.postgres_entities.PlayerRow;
import name.martingeisse.miner.server.postgres_entities.QPlayerAwardedAchievementRow;
import name.martingeisse.miner.server.postgres_entities.QPlayerRow;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;
import name.martingeisse.miner.server.util.database.postgres.PostgresUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * TODO rename this class to Player.
 */
public final class Player {

	private static Logger logger = Logger.getLogger(StackdSession.class);

	private final long id;
	private final ConcurrentMap<PlayerListener, PlayerListener> listeners = new ConcurrentHashMap<>();
	private final Inventory inventory;

	/**
	 * TODO use repository pattern and prevent multiple instances from being created. Rather, re-use an existing instance.
	 * <p>
	 * TODO Creating a new instance for the same player also has a bug: the listeners of the original one won't get
	 * notified. Solve by moving notification to a separate PlayerNotificationHub (or -service) which gets injected
	 * by Guice.
	 */
	public Player(long id) {
		this.id = id;
		loadPlayerRow();
		this.inventory = new Inventory(this);
	}

	private PlayerRow loadPlayerRow() {
		try (PostgresConnection connection = Databases.main.newConnection()) {
			QPlayerRow qp = QPlayerRow.Player;
			PlayerRow player = connection.query().select(qp).from(qp).where(qp.id.eq(id)).fetchOne();
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

	public void add(PlayerListener listener) {
		listeners.put(listener, listener);
	}

	public void remove(PlayerListener listener) {
		listeners.remove(listener);
	}

	void notifyListeners(Consumer<PlayerListener> function) {
		for (PlayerListener listener : listeners.keySet()) {
			function.accept(listener);
		}
	}

	//
	// ------------------------------------------------------------------------------------------------------------
	//

	public void handleMessage(Message untypedMessage) {
		if (untypedMessage instanceof EquipMessage) {

			EquipMessage message = (EquipMessage) untypedMessage;
			if (message.isUnequip()) {
				inventory.unequip(message.getInventorySlotId());
			} else {
				inventory.equip(message.getInventorySlotId());
			}

		} else {
			logger.error("unknown message routed to Player object: " + untypedMessage);
		}
	}

	//
	// ------------------------------------------------------------------------------------------------------------
	//

	public boolean addAchievement(String achievementCode) {
		QPlayerAwardedAchievementRow qpaa = QPlayerAwardedAchievementRow.PlayerAwardedAchievement;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLInsertClause insert = connection.insert(qpaa);
			insert.set(qpaa.playerId, id);
			insert.set(qpaa.achievementCode, achievementCode);
			try {
				return (insert.execute() > 0);
			} catch (QueryException e) {
				if (PostgresUtil.isDuplicateKeyViolation(e)) {
					return false;
				} else {
					throw e;
				}
			}
		}
	}

	public boolean addCoins(long amount) {
		QPlayerRow qp = QPlayerRow.Player;
		boolean success;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLUpdateClause update = connection.update(qp);
			update.where(qp.id.eq(id));
			update.set(qp.coins, qp.coins.add(amount));
			success = (update.execute() > 0);
		}
		if (success) {
			notifyListeners(PlayerListener::onCoinsChanged);
		}
		return success;
	}

	public long getCoins() {
		QPlayerRow qp = QPlayerRow.Player;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			return connection.query().select(qp.coins).from(qp).where(qp.id.eq(id)).fetchFirst();
		}
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void loadAvatar(Avatar avatar) {
		PlayerRow row = loadPlayerRow();
		avatar.setPosition(new Vector3d(row.getX().doubleValue(), row.getY().doubleValue(), row.getZ().doubleValue()));
		avatar.setOrientation(new EulerAngles(row.getLeftAngle().doubleValue(), row.getUpAngle().doubleValue(), 0));
		avatar.setName(row.getName());
	}

	public void saveAvatar(Avatar avatar) {
		QPlayerRow qp = QPlayerRow.Player;
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

	public void sendFlashMessage(String message) {
		notifyListeners(l -> l.onFlashMessage(message));
	}

}
