/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.game;

import name.martingeisse.miner.server.network.StackdSession;

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
		PlayerAccess playerAccess = session.getPlayerAccess();
		if (playerAccess == null) {
			return false;
		} else {
			return playerAccess.addAchievement(achievementCode);
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
			PlayerAccess playerAccess = session.getPlayerAccess();
			if (playerAccess != null) {
				session.sendFlashMessage("Achievement Unlocked: " + description + "(reward: " + reward + " coins)");
				session.getPlayerAccess().addCoins(reward);
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
