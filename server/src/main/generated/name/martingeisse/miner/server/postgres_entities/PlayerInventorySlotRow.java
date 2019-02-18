/*
 * This file was generated from the database schema.
 */
package name.martingeisse.miner.server.postgres_entities;

import java.io.Serializable;

/**
 * This class represents rows from table 'PlayerInventorySlot'.
 */
public class PlayerInventorySlotRow implements Serializable {

	/**
	 * Constructor.
	 */
	public PlayerInventorySlotRow() {
	}

	/**
	 * the equipped
	 */
	private Boolean equipped;

	/**
	 * the playerId
	 */
	private Long playerId;

	/**
	 * the quantity
	 */
	private Integer quantity;

	/**
	 * the type
	 */
	private Integer type;

	/**
	 * Getter method for the equipped.
	 *
	 * @return the equipped
	 */
	public Boolean getEquipped() {
		return equipped;
	}

	/**
	 * Setter method for the equipped.
	 *
	 * @param equipped the equipped to set
	 */
	public void setEquipped(Boolean equipped) {
		this.equipped = equipped;
	}

	/**
	 * Getter method for the playerId.
	 *
	 * @return the playerId
	 */
	public Long getPlayerId() {
		return playerId;
	}

	/**
	 * Setter method for the playerId.
	 *
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	/**
	 * Getter method for the quantity.
	 *
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * Setter method for the quantity.
	 *
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * Getter method for the type.
	 *
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * Setter method for the type.
	 *
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{PlayerInventorySlotRow. equipped = " + equipped + ", playerId = " + playerId + ", quantity = " + quantity + ", type = " + type + "}";
	}

}

