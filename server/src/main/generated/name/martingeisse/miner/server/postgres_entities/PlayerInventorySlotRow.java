/*
 * This file was generated from the database schema.
 */
package name.martingeisse.miner.server.postgres_entities;

import com.querydsl.sql.dml.SQLInsertClause;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

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
     * the id
     */
    private Long id;

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
     * Getter method for the id.
     * 
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter method for the id.
     * 
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
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

    /**
     * Loads the instance with the specified ID.
     * 
     * @param connection the database connection
     * @param id the ID of the instance to load
     * @return the loaded instance
     */
    public static PlayerInventorySlotRow loadById(PostgresConnection connection, Long id) {
        QPlayerInventorySlotRow q = QPlayerInventorySlotRow.PlayerInventorySlot;
        return connection.query().select(q).from(q).where(q.id.eq(id)).fetchFirst();
    }

    /**
     * Inserts this instance into the database. This object must not have an ID yet.
     */
    public void insert(PostgresConnection connection) {
        if (id != null) {
        	throw new IllegalStateException("this object already has an id: " + id);
        }
        QPlayerInventorySlotRow q = QPlayerInventorySlotRow.PlayerInventorySlot;
        SQLInsertClause insert = connection.insert(q);
        insert.set(q.equipped, equipped);
        insert.set(q.playerId, playerId);
        insert.set(q.quantity, quantity);
        insert.set(q.type, type);
        id = insert.executeWithKey(Long.class);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{PlayerInventorySlotRow. equipped = " + equipped + ", id = " + id + ", playerId = " + playerId + ", quantity = " + quantity + ", type = " + type + "}";
    }

}

