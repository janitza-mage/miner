/*
 * This file was generated from the database schema.
 */
package name.martingeisse.miner.server.entities;

import java.io.Serializable;

/**
 * This class represents rows from table 'PlayerInventorySlot'.
 */
public class PlayerInventorySlot implements Serializable {

    /**
     * Constructor.
     */
    public PlayerInventorySlot() {
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
     * the index
     */
    private Integer index;

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
     * Getter method for the index.
     * 
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * Setter method for the index.
     * 
     * @param index the index to set
     */
    public void setIndex(Integer index) {
        this.index = index;
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
        return "{PlayerInventorySlot. equipped = " + equipped + ", id = " + id + ", index = " + index + ", playerId = " + playerId + ", quantity = " + quantity + ", type = " + type + "}";
    }

}

