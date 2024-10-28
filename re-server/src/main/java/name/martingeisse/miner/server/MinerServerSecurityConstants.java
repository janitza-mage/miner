/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server;

/**
 * TODO fields from this class should be moved to the server configuration
 */
public class MinerServerSecurityConstants {

	public static final String SECURITY_TOKEN_SECRET = "qwiuofghiuqwhqipuwhfiuqoghfiuoqwhfiuqwbfhowquizgbfhuqhfgiuqghio";
	public static final int ACCOUNT_ACCESS_TOKEN_MAX_AGE_SECONDS = 5 * 60;
	public static final int ACCOUNT_ACCESS_TOKEN_MAX_AGE_MILLISECONDS = 1000 * ACCOUNT_ACCESS_TOKEN_MAX_AGE_SECONDS;
	public static final int PLAYER_ACCESS_TOKEN_MAX_AGE_SECONDS = 5 * 60;
	public static final int PLAYER_ACCESS_TOKEN_MAX_AGE_MILLISECONDS = 1000 * PLAYER_ACCESS_TOKEN_MAX_AGE_SECONDS;

}
