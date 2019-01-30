/*
 * This file was generated from the database schema.
 */
package name.martingeisse.miner.server.postgres_entities;

import com.querydsl.sql.dml.SQLInsertClause;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;
import java.io.Serializable;

/**
 * This class represents rows from table 'Faction'.
 */
public class FactionRow implements Serializable {

    /**
     * Constructor.
     */
    public FactionRow() {
    }

    /**
     * the divinePower
     */
    private Long divinePower;

    /**
     * the id
     */
    private Long id;

    /**
     * the score
     */
    private Long score;

    /**
     * Getter method for the divinePower.
     * 
     * @return the divinePower
     */
    public Long getDivinePower() {
        return divinePower;
    }

    /**
     * Setter method for the divinePower.
     * 
     * @param divinePower the divinePower to set
     */
    public void setDivinePower(Long divinePower) {
        this.divinePower = divinePower;
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
     * Getter method for the score.
     * 
     * @return the score
     */
    public Long getScore() {
        return score;
    }

    /**
     * Setter method for the score.
     * 
     * @param score the score to set
     */
    public void setScore(Long score) {
        this.score = score;
    }

    /**
     * Loads the instance with the specified ID.
     * 
     * @param connection the database connection
     * @param id the ID of the instance to load
     * @return the loaded instance
     */
    public static FactionRow loadById(PostgresConnection connection, Long id) {
        QFactionRow q = QFactionRow.Faction;
        return connection.query().select(q).from(q).where(q.id.eq(id)).fetchFirst();
    }

    /**
     * Inserts this instance into the database. This object must not have an ID yet.
     */
    public void insert(PostgresConnection connection) {
        if (id != null) {
        	throw new IllegalStateException("this object already has an id: " + id);
        }
        QFactionRow q = QFactionRow.Faction;
        SQLInsertClause insert = connection.insert(q);
        insert.set(q.divinePower, divinePower);
        insert.set(q.score, score);
        id = insert.executeWithKey(Long.class);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{FactionRow. divinePower = " + divinePower + ", id = " + id + ", score = " + score + "}";
    }

}

