package name.martingeisse.miner.client.engine.network;

import name.martingeisse.miner.common.network.Message;

/**
 * This interface is used to replace the message handling when switching from the start menu to in-game and back.
 * This makes sense because the sets of messages understood by either logic are mostly disjoint: The start menu
 * deals with logging in and selecting a player (which in-game logic cannot deal with), and in-game logic deals
 * with loading sections and objects from the cube world etc.
 */
public interface MessageConsumer {

	/**
	 * Consumes an incoming message. This method is called by a Netty thread and should not block for a long time.
	 */
	void consume(Message message);

}
