/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.ProtocolChannelInitializer;
import name.martingeisse.miner.common.network.ProtocolEndpoint;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;

/**
 *
 */
public final class ClientEndpoint extends ProtocolEndpoint {

	private static Logger logger = Logger.getLogger(ClientEndpoint.class);

	public static final String SERVER_NAME = "localhost";

	public static final ClientEndpoint INSTANCE = new ClientEndpoint();

	private volatile MessageConsumer messageConsumer;
	private volatile boolean connected;

	private ClientEndpoint() {
		this.messageConsumer = NullMessageConsumer.INSTANCE;
		this.connected = false;
	}

	@Override
	protected void onConnect() {
		connected = true;
	}

	@Override
	protected void onDisconnect() {
		connected = false;
	}

	public MessageConsumer getMessageConsumer() {
		return messageConsumer;
	}

	public void setMessageConsumer(MessageConsumer messageConsumer) {
		this.messageConsumer = messageConsumer;
	}

	public boolean isConnected() {
		return connected;
	}

	@Override
	protected void onDisconnectAfterException(Throwable originalException) {
		// should handle this more gracefully in the future
		Throwable t = originalException;
		while (true) {
			if (t instanceof ClosedChannelException) {
				logger.error("lost connection to server");
				System.exit(0);
			}
			if (t.getCause() == t || t.getCause() == null) {
				throw new RuntimeException(originalException);
			}
			t = t.getCause();
		}
	}

	@Override
	protected void onMessage(Message message) {
		messageConsumer.consume(message);
	}

	public void connect() {
		logger.info("connecting to server");
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		final Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler(new ProtocolChannelInitializer() {
			@Override
			protected ProtocolEndpoint createProtocolEndpoint() {
				return ClientEndpoint.this;
			}
		});
		bootstrap.connect(new InetSocketAddress(SERVER_NAME, Constants.NETWORK_PORT));
	}

	public void waitUntilConnected() throws InterruptedException {
		while (!connected) {
			Thread.sleep(10);
		}
	}

}
