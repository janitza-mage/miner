/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.game;

import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.entities.Player;
import name.martingeisse.miner.server.entities.QPlayer;
import name.martingeisse.miner.server.entities.QPlayerAwardedAchievement;
import name.martingeisse.miner.server.network.StackdSession;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

/**
 * Utility methods to deal with player's achievements.
 */
public class AchievementUtil {

	/**
	 * Awards an achievement to a player, but takes into account that the
	 * player might have been awarded the same achievement previously. Returns
	 * true if successfully awarded, false if the player already had been
	 * awarded the same achievement before.
	 * <p>
	 * This method is intended to be called when the prerequisites for the
	 * achievement have been asserted. If it returns false, the calling
	 * code should not take any further action. If it returns true, the
	 * achievement has been saved into the database (except if running in
	 * a database transaction; then it is of course only saved on commit).
	 * In that case, the calling function should next credit a reward to
	 * the player.
	 *
	 * @param session         the player's session
	 * @param achievementCode the internal unique code for the achievement
	 * @return true if successfully awarded, false if the player had already
	 * been awarded this achievement (or if no player was loaded)
	 */
	public static boolean awardAchievment(StackdSession session, String achievementCode) {
		if (session.getPlayerId() == null) {
			return false;
		}
		QPlayerAwardedAchievement qpaa = QPlayerAwardedAchievement.PlayerAwardedAchievement;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			SQLInsertClause insert = connection.insert(qpaa);
			insert.set(qpaa.playerId, session.getPlayerId());
			insert.set(qpaa.achievementCode, achievementCode);
			return (insert.execute() > 0);
		}
	}

	/**
	 * Like {@link #awardAchievment(StackdSession, String)}, but also sends a message to
	 * the client if the achievment was successfully awarded and assigns some bonus
	 * coins to the player.
	 *
	 * @param session         the player's session
	 * @param achievementCode the internal unique code for the achievement
	 * @param description     a short description of the achievement, used in the message
	 * @param reward          player's reward in coins
	 * @return true if successfully awarded, false if the player had already
	 * been awarded this achievement
	 */
	public static boolean awardAchievment(StackdSession session, String achievementCode, String description, int reward) {
		boolean success = awardAchievment(session, achievementCode);
		if (success) {
			session.sendFlashMessage("Achievement Unlocked: " + description + "(reward: " + reward + " coins)");
			try (PostgresConnection connection = Databases.main.newConnection()) {
				QPlayer qp = QPlayer.Player;
				Player player = connection.query().select(qp).from(qp).where(qp.id.eq(session.getPlayerId())).fetchFirst();
				if (player == null) {
					session.sendFlashMessage("no player loaded");
				} else {
					long newCoins = player.getCoins() + reward;
					SQLUpdateClause update = connection.update(qp);
					update.where(qp.id.eq(session.getPlayerId()));
					update.set(qp.coins, newCoins);
					update.execute();
					session.sendCoinsUpdate(newCoins);
				}
			}
		}
		return success;
	}

	/**
	 * Prevent instantiation.
	 */
	private AchievementUtil() {
	}

}
