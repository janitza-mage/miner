/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.network;

import name.martingeisse.miner.client.ingame.player.Player;
import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.util.frame.AbstractIntervalFrameHandler;
import name.martingeisse.miner.common.network.c2s.UpdatePosition;

/**
 * This frame handler sends the player's position to the server.
 */
public class SendPositionToServerHandler extends AbstractIntervalFrameHandler {

	/**
	 * the player
	 */
	private final Player player;

	/**
	 * Constructor.
	 *
	 * @param player the player whose position to send
	 */
	public SendPositionToServerHandler(Player player) {
		super(200);
		this.player = player;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.client.frame.AbstractIntervalFrameHandler#onIntervalTimerExpired()
	 */
	@Override
	protected void onIntervalTimerExpired() {
		ClientEndpoint.INSTANCE.send(new UpdatePosition(player.getPosition(), player.getOrientation()));
	}

}
