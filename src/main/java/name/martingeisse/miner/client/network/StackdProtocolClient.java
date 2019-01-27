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
import name.martingeisse.miner.client.frame.handlers.FlashMessageHandler;
import name.martingeisse.miner.client.ingame.IngameHandler;
import name.martingeisse.miner.client.ingame.network.PlayerResumedMessage;
import name.martingeisse.miner.client.ingame.player.PlayerProxy;
import name.martingeisse.miner.client.startmenu.AccountApiClient;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.geometry.angle.ReadableEulerAngles;
import name.martingeisse.miner.common.geometry.vector.ReadableVector3d;
import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.c2s.DigNotification;
import name.martingeisse.miner.common.network.c2s.ResumePlayer;
import name.martingeisse.miner.common.network.c2s.UpdatePosition;
import name.martingeisse.miner.common.network.s2c.*;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the connection to the server. Applications are
 * free to create subclasses that add application-specific message
 * types.
 */
public class StackdProtocolClient {

	private static Logger logger = Logger.getLogger(StackdProtocolClient.class);

	private ClientEndpoint endpoint;
	private FlashMessageHandler flashMessageHandler;
	private SectionGridLoader sectionGridLoader;
	private List<PlayerProxy> updatedPlayerProxies;
	private PlayerResumedMessage playerResumedMessage;
	private volatile long coins = 0;

	/**
	 * Constructor.
	 */
	public StackdProtocolClient() {
		String host = IngameHandler.serverName;
		int port = Constants.NETWORK_PORT;

		logger.info("connecting to server");
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		final Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler(new ClientChannelInitializer(this));
		bootstrap.connect(new InetSocketAddress(host, port));
	}

	public void setEndpoint(ClientEndpoint endpoint) {
		this.endpoint = endpoint;
		if (endpoint != null) {
			send(new ResumePlayer(AccountApiClient.getInstance().getPlayerAccessToken().getBytes(StandardCharsets.UTF_8)));
		}
	}

	/**
	 * @return true if ready, false if still connecting
	 */
	public final boolean isReady() {
		return (endpoint != null);
	}

	/**
	 * Waits until this client is ready.
	 *
	 * @throws InterruptedException if interrupted while waiting
	 */
	public final void waitUntilReady() throws InterruptedException {
		while (!isReady()) {
			Thread.sleep(10);
		}
	}

	/**
	 * Getter method for the flashMessageHandler.
	 *
	 * @return the flashMessageHandler
	 */
	public FlashMessageHandler getFlashMessageHandler() {
		return flashMessageHandler;
	}

	/**
	 * Setter method for the flashMessageHandler.
	 *
	 * @param flashMessageHandler the flashMessageHandler to set
	 */
	public void setFlashMessageHandler(FlashMessageHandler flashMessageHandler) {
		this.flashMessageHandler = flashMessageHandler;
	}

	/**
	 * Getter method for the sectionGridLoader.
	 *
	 * @return the sectionGridLoader
	 */
	public SectionGridLoader getSectionGridLoader() {
		return sectionGridLoader;
	}

	/**
	 * Setter method for the sectionGridLoader.
	 *
	 * @param sectionGridLoader the sectionGridLoader to set
	 */
	public void setSectionGridLoader(SectionGridLoader sectionGridLoader) {
		this.sectionGridLoader = sectionGridLoader;
	}

	public final void send(Message message) {
		endpoint.send(message);
	}

	/**
	 * This method gets invoked when receiving a flash message packet from the server.
	 * The default implementation adds the message to the flash message handler
	 * that was previously set via {@link #setFlashMessageHandler(FlashMessageHandler)}.
	 *
	 * @param message the message
	 */
	protected void onFlashMessageReceived(String message) {
		if (flashMessageHandler != null) {
			// TODO: this happens in another thread, posibly causing a ConcurrentModificationException
			// -> use a concurrent queue for *all* messages including flash messages!
			flashMessageHandler.addMessage(message);
		}
	}

	/**
	 * Invoked when the networking code throws an exception.
	 * <p>
	 * NOTE: This method gets invoked by the Netty thread, asynchronous
	 * to the main game thread!
	 *
	 * @param e the exception
	 */
	protected void onException(Throwable e) {
		// should handle this more gracefully in the future
		Throwable t = e;
		while (true) {
			if (t instanceof ClosedChannelException) {
				logger.error("lost connection to server");
				System.exit(0);
			}
			if (t.getCause() == t || t.getCause() == null) {
				throw new RuntimeException(e);
			}
			t = t.getCause();
		}
	}

	/**
	 * Sends an update message for the player's position to the server.
	 *
	 * @param position    the player's position
	 * @param orientation the player's orientation
	 */
	public void sendPositionUpdate(ReadableVector3d position, ReadableEulerAngles orientation) {
		send(new UpdatePosition(position, orientation));
	}

	/**
	 * This method gets invoked when receiving an application packet from the server.
	 * The default implementation does nothing.
	 * <p>
	 * NOTE: This method gets invoked by the Netty thread, asynchronous
	 * to the main game thread!
	 */
	protected void onMessageReceived(Message untypedMessage) {
		if (untypedMessage instanceof FlashMessage) {

			FlashMessage message = (FlashMessage) untypedMessage;
			onFlashMessageReceived(message.getText());

		} else if (untypedMessage instanceof InteractiveSectionDataResponse) {

			if (sectionGridLoader != null) {
				InteractiveSectionDataResponse message = (InteractiveSectionDataResponse) untypedMessage;
				sectionGridLoader.handleInteractiveSectionImage(message);
			} else {
				logger.error("received interactive section image but no sectionGridLoader is set in the StackdProtoclClient!");
			}

		} else if (untypedMessage instanceof SingleSectionModificationEvent) {

			if (sectionGridLoader != null) {
				SingleSectionModificationEvent message = (SingleSectionModificationEvent) untypedMessage;
				sectionGridLoader.handleModificationEvent(message);
			} else {
				logger.error("received section modification event but no sectionGridLoader is set in the StackdProtoclClient!");
			}

		} else if (untypedMessage instanceof PlayerListUpdate) {

			PlayerListUpdate message = (PlayerListUpdate) untypedMessage;
			List<PlayerProxy> playerProxiesFromMessage = new ArrayList<PlayerProxy>();
			for (PlayerListUpdate.Element element : message.getElements()) {
				PlayerProxy proxy = new PlayerProxy();
				proxy.getPosition().copyFrom(element.getPosition());
				proxy.getOrientation().copyFrom(element.getAngles());
				proxy.setName(element.getName());
				playerProxiesFromMessage.add(proxy);
			}
			synchronized (this) {
				this.updatedPlayerProxies = playerProxiesFromMessage;
			}

		} else if (untypedMessage instanceof PlayerResumed) {

			PlayerResumed message = (PlayerResumed) untypedMessage;
			synchronized (this) {
				this.playerResumedMessage = new PlayerResumedMessage(message.getPosition(), message.getOrientation());
			}

		} else if (untypedMessage instanceof UpdateCoins) {

			UpdateCoins message = (UpdateCoins) untypedMessage;
			coins = message.getCoins();
			logger.info("update coins: " + coins);

		} else {
			logger.error("client received unexpected message: " + untypedMessage);
		}
	}

	/**
	 * If there is an updated list of player proxies, returns that
	 * list and deletes it from this object.
	 *
	 * @return the updated player proxies, or null if no update
	 * is available
	 */
	public synchronized List<PlayerProxy> fetchUpdatedPlayerProxies() {
		List<PlayerProxy> result = updatedPlayerProxies;
		updatedPlayerProxies = null;
		return result;
	}

	/**
	 * If there is a {@link PlayerResumedMessage}, returns that
	 * message and deletes it from this object.
	 *
	 * @return the message, or null if no message is available
	 */
	public synchronized PlayerResumedMessage fetchPlayerResumedMessage() {
		PlayerResumedMessage result = playerResumedMessage;
		playerResumedMessage = null;
		return result;
	}

	/**
	 * Sends a notification about the fact that this player has dug away a cube.
	 *
	 * @param x the x position of the cube
	 * @param y the y position of the cube
	 * @param z the z position of the cube
	 */
	public void sendDigNotification(int x, int y, int z) {
		send(new DigNotification(new Vector3i(x, y, z)));
	}

	/**
	 * Getter method for the coins.
	 *
	 * @return the coins
	 */
	public long getCoins() {
		return coins;
	}

}
