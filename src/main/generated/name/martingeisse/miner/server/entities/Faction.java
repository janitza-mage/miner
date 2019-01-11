/*
 * This file was generated from the database schema.
 */
package name.martingeisse.miner.server.entities;

import java.io.Serializable;

/**
 * This class represents rows from table 'Faction'.
 */
public class Faction implements Serializable {

    /**
     * Constructor.
     */
    public Faction() {
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{Faction. divinePower = " + divinePower + ", id = " + id + ", score = " + score + "}";
    }

}

