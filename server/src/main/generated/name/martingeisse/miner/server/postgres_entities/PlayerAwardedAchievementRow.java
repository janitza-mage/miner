/*
 * This file was generated from the database schema.
 */
package name.martingeisse.miner.server.postgres_entities;

import com.querydsl.sql.dml.SQLInsertClause;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

import java.io.Serializable;

/**
 * This class represents rows from table 'PlayerAwardedAchievement'.
 */
public class PlayerAwardedAchievementRow implements Serializable {

	/**
	 * Constructor.
	 */
	public PlayerAwardedAchievementRow() {
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

	/**
	 * Loads the instance with the specified ID.
	 *
	 * @param connection the database connection
	 * @param id         the ID of the instance to load
	 * @return the loaded instance
	 */
	public static PlayerAwardedAchievementRow loadById(PostgresConnection connection, Long id) {
		QPlayerAwardedAchievementRow q = QPlayerAwardedAchievementRow.PlayerAwardedAchievement;
		return connection.query().select(q).from(q).where(q.id.eq(id)).fetchFirst();
	}

	/**
	 * Inserts this instance into the database. This object must not have an ID yet.
	 */
	public void insert(PostgresConnection connection) {
		if (id != null) {
			throw new IllegalStateException("this object already has an id: " + id);
		}
		QPlayerAwardedAchievementRow q = QPlayerAwardedAchievementRow.PlayerAwardedAchievement;
		SQLInsertClause insert = connection.insert(q);
		insert.set(q.achievementCode, achievementCode);
		insert.set(q.playerId, playerId);
		id = insert.executeWithKey(Long.class);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{PlayerAwardedAchievementRow. achievementCode = " + achievementCode + ", id = " + id + ", playerId = " + playerId + "}";
	}

}

