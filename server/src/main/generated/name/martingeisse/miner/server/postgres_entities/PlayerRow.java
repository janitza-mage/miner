/*
 * This file was generated from the database schema.
 */
package name.martingeisse.miner.server.postgres_entities;

import com.querydsl.sql.dml.SQLInsertClause;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

import java.io.Serializable;

/**
 * This class represents rows from table 'Player'.
 */
public class PlayerRow implements Serializable {

    /**
     * Constructor.
     */
    public PlayerRow() {
    }

    /**
     * the coins
     */
    private Long coins;

    /**
     * the deleted
     */
    private Boolean deleted;

    /**
     * the faction
     */
    private Integer faction;

    /**
     * the id
     */
    private Long id;

    /**
     * the leftAngle
     */
    private java.math.BigDecimal leftAngle;

    /**
     * the name
     */
    private String name;

    /**
     * the upAngle
     */
    private java.math.BigDecimal upAngle;

    /**
     * the userAccountId
     */
    private Long userAccountId;

    /**
     * the x
     */
    private java.math.BigDecimal x;

    /**
     * the y
     */
    private java.math.BigDecimal y;

    /**
     * the z
     */
    private java.math.BigDecimal z;

    /**
     * Getter method for the coins.
     * 
     * @return the coins
     */
    public Long getCoins() {
        return coins;
    }

    /**
     * Setter method for the coins.
     * 
     * @param coins the coins to set
     */
    public void setCoins(Long coins) {
        this.coins = coins;
    }

    /**
     * Getter method for the deleted.
     * 
     * @return the deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * Setter method for the deleted.
     * 
     * @param deleted the deleted to set
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Getter method for the faction.
     * 
     * @return the faction
     */
    public Integer getFaction() {
        return faction;
    }

    /**
     * Setter method for the faction.
     * 
     * @param faction the faction to set
     */
    public void setFaction(Integer faction) {
        this.faction = faction;
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
     * Getter method for the leftAngle.
     * 
     * @return the leftAngle
     */
    public java.math.BigDecimal getLeftAngle() {
        return leftAngle;
    }

    /**
     * Setter method for the leftAngle.
     * 
     * @param leftAngle the leftAngle to set
     */
    public void setLeftAngle(java.math.BigDecimal leftAngle) {
        this.leftAngle = leftAngle;
    }

    /**
     * Getter method for the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for the name.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method for the upAngle.
     * 
     * @return the upAngle
     */
    public java.math.BigDecimal getUpAngle() {
        return upAngle;
    }

    /**
     * Setter method for the upAngle.
     * 
     * @param upAngle the upAngle to set
     */
    public void setUpAngle(java.math.BigDecimal upAngle) {
        this.upAngle = upAngle;
    }

    /**
     * Getter method for the userAccountId.
     * 
     * @return the userAccountId
     */
    public Long getUserAccountId() {
        return userAccountId;
    }

    /**
     * Setter method for the userAccountId.
     * 
     * @param userAccountId the userAccountId to set
     */
    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    /**
     * Getter method for the x.
     * 
     * @return the x
     */
    public java.math.BigDecimal getX() {
        return x;
    }

    /**
     * Setter method for the x.
     * 
     * @param x the x to set
     */
    public void setX(java.math.BigDecimal x) {
        this.x = x;
    }

    /**
     * Getter method for the y.
     * 
     * @return the y
     */
    public java.math.BigDecimal getY() {
        return y;
    }

    /**
     * Setter method for the y.
     * 
     * @param y the y to set
     */
    public void setY(java.math.BigDecimal y) {
        this.y = y;
    }

    /**
     * Getter method for the z.
     * 
     * @return the z
     */
    public java.math.BigDecimal getZ() {
        return z;
    }

    /**
     * Setter method for the z.
     * 
     * @param z the z to set
     */
    public void setZ(java.math.BigDecimal z) {
        this.z = z;
    }

    /**
     * Loads the instance with the specified ID.
     * 
     * @param connection the database connection
     * @param id the ID of the instance to load
     * @return the loaded instance
     */
    public static PlayerRow loadById(PostgresConnection connection, Long id) {
        QPlayerRow q = QPlayerRow.Player;
        return connection.query().select(q).from(q).where(q.id.eq(id)).fetchFirst();
    }

    /**
     * Inserts this instance into the database. This object must not have an ID yet.
     */
    public void insert(PostgresConnection connection) {
        if (id != null) {
        	throw new IllegalStateException("this object already has an id: " + id);
        }
        QPlayerRow q = QPlayerRow.Player;
        SQLInsertClause insert = connection.insert(q);
        insert.set(q.coins, coins);
        insert.set(q.deleted, deleted);
        insert.set(q.faction, faction);
        insert.set(q.leftAngle, leftAngle);
        insert.set(q.name, name);
        insert.set(q.upAngle, upAngle);
        insert.set(q.userAccountId, userAccountId);
        insert.set(q.x, x);
        insert.set(q.y, y);
        insert.set(q.z, z);
        id = insert.executeWithKey(Long.class);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{PlayerRow. coins = " + coins + ", deleted = " + deleted + ", faction = " + faction + ", id = " + id + ", leftAngle = " + leftAngle + ", name = " + name + ", upAngle = " + upAngle + ", userAccountId = " + userAccountId + ", x = " + x + ", y = " + y + ", z = " + z + "}";
    }

}

