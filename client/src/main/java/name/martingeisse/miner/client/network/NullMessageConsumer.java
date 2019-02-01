package name.martingeisse.miner.client.network;

import name.martingeisse.miner.common.network.Message;

/**
 *
 */
public final class NullMessageConsumer implements MessageConsumer {

	public static final NullMessageConsumer INSTANCE = new NullMessageConsumer();

	@Override
	public void consume(Message message) {
	}

}
