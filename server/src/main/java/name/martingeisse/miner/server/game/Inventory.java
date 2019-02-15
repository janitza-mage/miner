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
import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.cubetype.CubeTypes;
import name.martingeisse.miner.common.logic.EquipmentSlot;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.postgres_entities.PlayerInventorySlotRow;
import name.martingeisse.miner.server.postgres_entities.QPlayerInventorySlotRow;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

import java.util.List;

/**
 * Database utility class to deal with players' inventories.
 */
public final class Inventory {

	private final Player player;
	private final long playerId;

	/**
	 * You MUST invoke this constructor with the "real" player to make sure the real one gets its listeners
	 * notified about changes.
	 */
	Inventory(Player player) {
		this.player = player;
		this.playerId = player.getId();
	}

	public long getPlayerId() {
		return playerId;
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * Adds an item to the player's inventory. This is equivalent to add(type, 1).
	 *
	 * @param type the item type
	 */
	public void add(CubeType type) {
		add(type, 1);
	}

	/**
	 * Adds an item to the player's inventory.
	 *
	 * @param type     the item type
	 * @param quantity the quantity of the item stack
	 */
	public void add(CubeType type, int quantity) {
		QPlayerInventorySlotRow qpis = QPlayerInventorySlotRow.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLInsertClause insert = connection.insert(qpis);
			insert.set(qpis.playerId, playerId);
			insert.set(qpis.type, type.getIndex());
			insert.set(qpis.quantity, quantity);
			insert.set(qpis.equipped, false);
			insert.execute();
		}
		player.notifyListeners(PlayerListener::onInventoryChanged);
	}

	/**
	 * Lists all items in the player's inventory, both equipped and in the backpack.
	 * The returned list contains all backpack items first, then all equipped items.
	 * Both groups are sorted by id.
	 *
	 * @return the list of items
	 */
	public List<PlayerInventorySlotRow> listAll() {
		final QPlayerInventorySlotRow qpis = QPlayerInventorySlotRow.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			return connection.query().select(qpis).from(qpis).where(qpis.playerId.eq(playerId)).orderBy(qpis.id.asc()).fetch();
		}
	}

	/**
	 * Lists the items in the player's backpack, sorted by id.
	 *
	 * @return the list of items
	 */
	public List<PlayerInventorySlotRow> listBackback() {
		final QPlayerInventorySlotRow qpis = QPlayerInventorySlotRow.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			return connection.query().select(qpis).from(qpis).where(qpis.playerId.eq(playerId), qpis.equipped.isFalse()).orderBy(qpis.id.asc()).fetch();
		}
	}

	/**
	 * Lists the player's equipped items, sorted by id.
	 *
	 * @return the list of items
	 */
	public List<PlayerInventorySlotRow> listEquipped() {
		final QPlayerInventorySlotRow qpis = QPlayerInventorySlotRow.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			return connection.query().select(qpis).from(qpis).where(qpis.playerId.eq(playerId), qpis.equipped.isTrue()).orderBy(qpis.id.asc()).fetch();
		}
	}

	/**
	 * Deletes a backpack item, specified by id.
	 *
	 * @param id the item's id.
	 */
	public void deleteBackpackItem(long id) {
		final QPlayerInventorySlotRow qpis = QPlayerInventorySlotRow.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLDeleteClause delete = connection.delete(qpis);
			delete.where(qpis.playerId.eq(playerId), qpis.equipped.isFalse(), qpis.id.eq(id));
			delete.execute();
		}
		player.notifyListeners(PlayerListener::onInventoryChanged);
	}

	/**
	 * Deletes an equipped item, specified by id.
	 *
	 * @param id the item's id.
	 */
	public void deleteEquippedItem(long id) {
		final QPlayerInventorySlotRow qpis = QPlayerInventorySlotRow.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLDeleteClause delete = connection.delete(qpis);
			delete.where(qpis.playerId.eq(playerId), qpis.equipped.isTrue(), qpis.id.eq(id));
			delete.execute();
		}
		player.notifyListeners(PlayerListener::onInventoryChanged);
	}

	/**
	 * Equips the backpack item with the specified id.
	 *
	 * @param id the item's id
	 */
	public void equip(long id) {
		final QPlayerInventorySlotRow qpis = QPlayerInventorySlotRow.PlayerInventorySlot;
		boolean changed;
		try (PostgresConnection connection = Databases.main.newConnection()) {

			// fetch that row
			PlayerInventorySlotRow row = connection.query().select(qpis).from(qpis).
				where(qpis.playerId.eq(playerId), qpis.equipped.isFalse(), qpis.id.eq(id)).fetchFirst();
			if (row == null) {
				return;
			}

			// unequip the previously equipped row of the same equipment slot, if any
			unequip(CubeTypes.CUBE_TYPES[row.getType()].getEquipmentSlot());

			// equip the specified slot
			SQLUpdateClause update = connection.update(qpis);
			update.where(qpis.id.eq(row.getId()));
			update.set(qpis.equipped, true);
			changed = (update.execute() > 0);

		}
		if (changed) {
			player.notifyListeners(PlayerListener::onInventoryChanged);
		}
	}

	/**
	 * Unequips the equipped item with the specified id.
	 *
	 * @param id the item's id
	 */
	public void unequip(long id) {
		final QPlayerInventorySlotRow qpis = QPlayerInventorySlotRow.PlayerInventorySlot;
		boolean changed;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLUpdateClause update = connection.update(qpis);
			update.where(qpis.playerId.eq(playerId), qpis.equipped.isTrue(), qpis.id.eq(id));
			update.set(qpis.equipped, false);
			changed = (update.execute() > 0);
		}
		if (changed) {
			player.notifyListeners(PlayerListener::onInventoryChanged);
		}
	}

	/**
	 * Unequips the equipped item with the specified equipment slot, if any.
	 * <p>
	 * In case multiple inventory slots with that equipment slot are equipped (which should not happen), this
	 * method unequips them all.
	 */
	public void unequip(EquipmentSlot equipmentSlot) {
		List<PlayerInventorySlotRow> rows = listAll();
		for (PlayerInventorySlotRow row : rows) {
			CubeType type = CubeTypes.CUBE_TYPES[row.getType()];
			if (type.getEquipmentSlot() == equipmentSlot) {
				unequip(row.getId());
			}
		}
	}

	/**
	 * Finds an item in the player's inventory by item type.
	 *
	 * @param type     the item type
	 * @param equipped whether to look for equipped items or backpack items
	 * @return the id, or -1 if not found
	 */
	public long findByType(CubeType type, boolean equipped) {
		final QPlayerInventorySlotRow qpis = QPlayerInventorySlotRow.PlayerInventorySlot;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			PostgreSQLQuery<Long> query = connection.query().select(qpis.id).from(qpis);
			query.where(qpis.playerId.eq(playerId), qpis.equipped.eq(equipped), qpis.type.eq(type.getIndex()));
			Long result = query.fetchFirst();
			return (result == null ? -1 : result);
		}
	}

}
