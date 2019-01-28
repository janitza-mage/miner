/**
 * Copyright (c) 2013 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.game;

import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.querydsl.sql.postgresql.PostgreSQLQuery;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.entities.PlayerInventorySlot;
import name.martingeisse.miner.server.entities.QPlayerInventorySlot;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Database utility class to deal with players' inventories.
 *
 * TODO consider removing the "index" field. I will likely not use it because there is no intrinsic order in items,
 * and sorting will probably happen client-side only so there is no need to save it to the database.
 */
public final class InventoryAccess {

	private final PlayerAccess playerAccess;
	private final long playerId;

	/**
	 * You MUST invoke this constructor with the "real" PlayerAccess to make sure the real one gets its listeners
	 * notified about changes.
	 */
	InventoryAccess(PlayerAccess playerAccess) {
		this.playerAccess = playerAccess;
		this.playerId = playerAccess.getId();
	}

	public long getPlayerId() {
		return playerId;
	}

	public PlayerAccess getPlayerAccess() {
		return playerAccess;
	}

	/**
	 * Adds an item to the player's inventory. This is equivalent to add(type, 1).
	 *
	 * @param type the item type
	 */
	public void add(ItemType type) {
		add(type, 1);
	}

	/**
	 * Adds an item to the player's inventory.
	 *
	 * @param type     the item type
	 * @param quantity the quantity of the item stack
	 */
	public void add(ItemType type, int quantity) {
		QPlayerInventorySlot qpis = QPlayerInventorySlot.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			int previousInventoryLength = (int) connection.query().select(qpis).from(qpis).where(qpis.playerId.eq(playerId), qpis.equipped.isFalse()).fetchCount();
			SQLInsertClause insert = connection.insert(qpis);
			insert.set(qpis.playerId, playerId);
			insert.set(qpis.equipped, false);
			insert.set(qpis.index, previousInventoryLength);
			insert.set(qpis.type, type.ordinal());
			insert.set(qpis.quantity, quantity);
			insert.execute();
		}
		getPlayerAccess().notifyListeners(PlayerListener::onInventoryChanged);
	}

	/**
	 * Lists all items in the player's inventory, both equipped and in the backpack.
	 * The returned list contains all backpack items first, then all equipped items.
	 * Both groups are sorted by index.
	 *
	 * @return the list of items
	 */
	public List<PlayerInventorySlot> listAll() {
		final QPlayerInventorySlot qpis = QPlayerInventorySlot.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			return connection.query().select(qpis).from(qpis).where(qpis.playerId.eq(playerId)).orderBy(qpis.equipped.asc(), qpis.index.asc()).fetch();
		}
	}

	/**
	 * Lists the items in the player's backpack, sorted by index.
	 *
	 * @return the list of items
	 */
	public List<PlayerInventorySlot> listBackback() {
		final QPlayerInventorySlot qpis = QPlayerInventorySlot.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			return connection.query().select(qpis).from(qpis).where(qpis.playerId.eq(playerId), qpis.equipped.isFalse()).orderBy(qpis.index.asc()).fetch();
		}
	}

	/**
	 * Lists the player's equipped items, sorted by index.
	 *
	 * @return the list of items
	 */
	public List<PlayerInventorySlot> listEquipped() {
		final QPlayerInventorySlot qpis = QPlayerInventorySlot.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			return connection.query().select(qpis).from(qpis).where(qpis.playerId.eq(playerId), qpis.equipped.isTrue()).orderBy(qpis.index.asc()).fetch();
		}
	}

	/**
	 * Deletes a backpack item, specified by index.
	 *
	 * @param index the item's index.
	 */
	public void deleteBackpackItemByIndex(int index) {
		final QPlayerInventorySlot qpis = QPlayerInventorySlot.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLDeleteClause delete = connection.delete(qpis);
			delete.where(qpis.playerId.eq(playerId), qpis.equipped.isFalse(), qpis.index.eq(index));
			delete.execute();
			renumber(false);
		}
		getPlayerAccess().notifyListeners(PlayerListener::onInventoryChanged);
	}

	/**
	 * Deletes an equipped item, specified by index.
	 *
	 * @param index the item's index.
	 */
	public void deleteEquippedItemByIndex(int index) {
		final QPlayerInventorySlot qpis = QPlayerInventorySlot.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLDeleteClause delete = connection.delete(qpis);
			delete.where(qpis.playerId.eq(playerId), qpis.equipped.isTrue(), qpis.index.eq(index));
			delete.execute();
			renumber(true);
		}
		getPlayerAccess().notifyListeners(PlayerListener::onInventoryChanged);
	}

	/**
	 * Equips the backpack item with the specified index.
	 *
	 * @param index the item's index
	 */
	public void equip(int index) {
		final QPlayerInventorySlot qpis = QPlayerInventorySlot.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLUpdateClause update = connection.update(qpis);
			update.where(qpis.playerId.eq(playerId), qpis.equipped.isFalse(), qpis.index.eq(index));
			update.set(qpis.equipped, true);
			update.execute();
			renumber(false);
			renumber(true);
		}
		getPlayerAccess().notifyListeners(PlayerListener::onInventoryChanged);
	}

	/**
	 * Unequips the equipped item with the specified index.
	 *
	 * @param index the item's index
	 */
	public void unequip(int index) {
		final QPlayerInventorySlot qpis = QPlayerInventorySlot.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLUpdateClause update = connection.update(qpis);
			update.where(qpis.playerId.eq(playerId), qpis.equipped.isTrue(), qpis.index.eq(index));
			update.set(qpis.equipped, false);
			update.execute();
			renumber(false);
			renumber(true);
		}
		getPlayerAccess().notifyListeners(PlayerListener::onInventoryChanged);
	}

	/**
	 *
	 */
	private void renumber(boolean equipped) {
		try (Connection connection = Databases.main.newJdbcConnection()) {
			Statement statement = connection.createStatement();
			statement.execute("SELECT (@a := -1);");
			statement.execute("UPDATE `player_inventory_slot` SET `index` = (@a := @a + 1) WHERE `player_id` = " + playerId + " AND `equipped` = " + (equipped ? '1' : '0') + ";");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Finds an item in the player's inventory by item type.
	 *
	 * @param type     the item type
	 * @param equipped whether to look for equipped items or backpack items
	 * @return the index, or -1 if not found
	 */
	public int findByType(ItemType type, boolean equipped) {
		final QPlayerInventorySlot qpis = QPlayerInventorySlot.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			PostgreSQLQuery<Integer> query = connection.query().select(qpis.index).from(qpis);
			query.where(qpis.playerId.eq(playerId), qpis.equipped.eq(equipped), qpis.type.eq(type.ordinal()));
			Integer result = query.fetchFirst();
			return (result == null ? -1 : result);
		}
	}

}
