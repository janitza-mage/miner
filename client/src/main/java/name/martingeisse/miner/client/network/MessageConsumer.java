package name.martingeisse.miner.client.network;

import name.martingeisse.miner.common.network.Message;

/**
 * This interface is used to replace the message handling when switching from the start menu to in-game and back.
 * This makes sense because the sets of messages understood by either logic are mostly disjoint: The start menu
 * deals with logging in and selecting a player (which in-game logic cannot deal with), and in-game logic deals
 * with loading sections and objects from the cube world etc.
 */
public interface MessageConsumer {

	// TODO remove this eventually. This method was intended to let the protocol client know about its messageSender as
	// soon as it exists so the client can send messages. However, we want to refactor the connection process such
	// that the end point exists *before* the client (i.e. MessageConsumer), so the messageSender can just be passed in
	// the client's constructor and this method isn't needed anymore.
	void setMessageSender(MessageSender messageSender);

	void consume(Message message);

}
