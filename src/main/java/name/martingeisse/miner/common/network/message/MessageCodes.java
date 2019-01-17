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

	/**
	 * The type constant for single-section modification events.
	 * Sent by the server when a section gets modified. Clients that
	 * are close enough to be interested in the update would typically
	 * request the new section render model and collider in turn. Clients
	 * that are too far away would ignore these events. TODO: store the
	 * client's rough position on the server, filter mod events
	 * server-side, then just send the updated objects. This slightly
	 * increases network traffic (sending unnecessary data) but reduces
	 * latency and simplifies the code.
	 */
	public static final int S2C_SINGLE_SECTION_MODIFICATION_EVENT = 9;

	/**
	 * This packet contains a console command to be executed by the server,
	 * encoded as a sequence of strings (see {@link DataOutput#writeUTF(String)}).
	 */
	public static final int C2S_CONSOLE_INPUT = 10;

	/**
	 * This packet contains output lines to print on the console.
	 * Only complete lines can be output this way.
	 */
	public static final int S2C_CONSOLE_OUTPUT = 11;

	/**
	 * Request/response code to fetch section data objects of type
	 * {@link SectionDataType#INTERACTIVE}.
	 */
	public static final int SINGLE_SECTION_DATA_INTERACTIVE = 0xff11;

	/**
	 * The type constant for cube modification packets (client to server only).
	 */
	public static final int C2S_CUBE_MODIFICATION = 99;

	/**
	 * Prevent instantiation.
	 */
	private MessageCodes() {
	}

}
