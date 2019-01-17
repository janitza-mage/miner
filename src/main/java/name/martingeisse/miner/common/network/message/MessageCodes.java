/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.network.message;

import name.martingeisse.miner.common.section.SectionDataType;

import java.io.DataOutput;

/**
 * Codes that indicate the {@link Message} subclass encoded in a packet.
 */
public final class MessageCodes {

	public static final int C2S_UPDATE_POSITION = 0;
	public static final int C2S_RESUME_PLAYER = 1;
	public static final int C2S_DIG_NOTIFICATION = 2;

	public static final int S2C_PLAYER_LIST_UPDATE = 3;
	public static final int S2C_PLAYER_NAMES_UPDATE = 4;
	public static final int S2C_PLAYER_RESUMED = 5;
	public static final int S2C_UPDATE_COINS = 6;
	public static final int S2C_HELLO = 7;
	public static final int S2C_FLASH_MESSAGE = 8;

	public static final int S2C_SINGLE_SECTION_MODIFICATION_EVENT = 9;

	public static final int C2S_CONSOLE_INPUT = 10;

	public static final int S2C_CONSOLE_OUTPUT = 11;

	/**
	 * Request/response code to fetch section data objects of type
	 * {@link SectionDataType#INTERACTIVE}. TODO move to message class
	 */
	public static final int C2S_INTERACTIVE_SECTION_DATA_REQUEST = 12;
	public static final int S2C_INTERACTIVE_SECTION_DATA_RESPONSE = 13;

	public static final int C2S_CUBE_MODIFICATION = 14;

	/**
	 * Prevent instantiation.
	 */
	private MessageCodes() {
	}

}
