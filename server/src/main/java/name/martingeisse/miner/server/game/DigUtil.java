/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.game;

import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.logic.ItemType;
import org.apache.log4j.Logger;

/**
 * Utility methods to deal with digging.
 */
public final class DigUtil {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(DigUtil.class);

	/**
	 * This method gets invoked when a player has dug away a cube. It handles
	 * any game logic besides the basic fact that clients must update that cube
	 * and saving the updated cube server-side. For example, this method rewards
	 * inventory items for digging ores.
	 */
	public static void onCubeDugAway(Player player, Vector3i position, byte cubeType) {
		logger.info("dug cube: " + cubeType);

		// check for ores
		switch (cubeType) {

			case 1:
				player.getInventory().add(ItemType.STONE);
				break;

			case 2:
			case 3:
				player.getInventory().add(ItemType.DIRT);
				break;

			case 12:
				player.getInventory().add(ItemType.SAND);
				break;

			case 16:
				oreFound(player, "coal", 2);
				player.getInventory().add(ItemType.COAL);
				player.sendFlashMessage("inventory item added");
				break;

			case 10:
				oreFound(player, "gold", 5);
				break;

			case 11:
				oreFound(player, "diamond", 20);
				break;

			case 13:
				oreFound(player, "ruby", 10);
				break;

			case 14:
				oreFound(player, "sapphire", 10);
				break;

		}

		// check for achievements
		if (position.y > 10) {
			AchievementUtil.awardAchievment(player, "digAbove10", "Dig a cube above 10m height", 10);
		}
		if (position.y > 100) {
			AchievementUtil.awardAchievment(player, "digAbove100", "Dig a cube above 100m height", 100);
		}
		if (position.y < -10) {
			AchievementUtil.awardAchievment(player, "digBelow10", "Dig a cube below 10m depth", 10);
		}
		if (position.y < -100) {
			AchievementUtil.awardAchievment(player, "bigBelow100", "Dig a cube below 100m depth", 100);
		}

	}

	private static void oreFound(Player player, String name, int value) {
		if (player == null) {
			return;
		}
		player.addCoins(value);
		player.sendFlashMessage("You found some " + name + " (worth " + value + " coins).");
	}

	/**
	 * Prevent instantiation.
	 */
	private DigUtil() {
	}

}
