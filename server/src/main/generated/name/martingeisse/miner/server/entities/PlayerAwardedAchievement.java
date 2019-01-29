/*
 * This file was generated from the database schema.
 */
package name.martingeisse.miner.server.entities;

import java.io.Serializable;

/**
 * This class represents rows from table 'PlayerAwardedAchievement'.
 */
public class PlayerAwardedAchievement implements Serializable {

    /**
     * Constructor.
     */
    public PlayerAwardedAchievement() {
    }

    /**
     * the achievementCode
     */
    private String achievementCode;

    /**
     * the id
     */
    private Long id;

    /**
     * the playerId
     */
    private Long playerId;

    /**
     * Getter method for the achievementCode.
     * 
     * @return the achievementCode
     */
    public String getAchievementCode() {
        return achievementCode;
    }

    /**
     * Setter method for the achievementCode.
     * 
     * @param achievementCode the achievementCode to set
     */
    public void setAchievementCode(String achievementCode) {
        this.achievementCode = achievementCode;
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{PlayerAwardedAchievement. achievementCode = " + achievementCode + ", id = " + id + ", playerId = " + playerId + "}";
    }

}

