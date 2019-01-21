/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.network;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.console.Console;
import name.martingeisse.miner.client.frame.handlers.FlashMessageHandler;
import name.martingeisse.miner.client.ingame.IngameHandler;
import name.martingeisse.miner.client.ingame.network.PlayerResumedMessage;
import name.martingeisse.miner.client.ingame.player.PlayerProxy;
import name.martingeisse.miner.client.startmenu.AccountApiClient;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.geometry.angle.ReadableEulerAngles;
import name.martingeisse.miner.common.geometry.vector.ReadableVector3d;
import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.c2s.ConsoleInput;
import name.martingeisse.miner.common.network.message.c2s.DigNotification;
import name.martingeisse.miner.common.network.message.c2s.ResumePlayer;
import name.martingeisse.miner.common.network.message.c2s.UpdatePosition;
import name.martingeisse.miner.common.network.message.s2c.*;
import name.martingeisse.miner.common.network.protocol.StackdPacket;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.ThreadRenamingRunnable;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * This class handles the connection to the server. Applications are
 * free to create subclasses that add application-specific message
 * types.
 */
public class StackdProtocolClient {

	private static Logger logger = Logger.getLogger(StackdProtocolClient.class);

	private ClientEndpoint endpoint;
	private final Object syncObject = new Object();
	private int sessionId = -1;
	private FlashMessageHandler flashMessageHandler;
	private SectionGridLoader sectionGridLoader;
	private Console console;
	private List<PlayerProxy> updatedPlayerProxies;
	private Map<Integer, String> updatedPlayerNames;
	private PlayerResumedMessage playerResumedMessage;
	private volatile long coins = 0;

	/**
	 * Constructor.
	 */
	public StackdProtocolClient() {
		String host = IngameHandler.serverName;
		int port = Constants.NETWORK_PORT;

		logger.info("connecting to server");
		ThreadRenamingRunnable.setThreadNameDeterminer(new ThreadNameDeterminer() {
			@Override
			public String determineThreadName(String currentThreadName, String proposedThreadName) throws Exception {
				return proposedThreadName.replace(' ', '-');
			}
		});
		final ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());
		final ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipelineFactory(new ClientPipelineFactory(this));
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.connect(new InetSocketAddress(host, port));
	}

	public void setEndpoint(ClientEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * @return true if ready, false if still connecting
	 */
	public final boolean isReady() {
		synchronized (syncObject) {
			return (sessionId != -1);
		}
	}

	/**
	 * Waits until this client is ready.
	 *
	 * @throws InterruptedException if interrupted while waiting
	 */
	public final void waitUntilReady() throws InterruptedException {
		synchronized (syncObject) {
			if (sessionId == -1) {
				syncObject.wait();
			}
		}
	}

	/**
	 * Getter method for the sessionId.
	 *
	 * @return the sessionId
	 */
	public final int getSessionId() {
		return sessionId;
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

	/**
	 * Getter method for the console.
	 *
	 * @return the console
	 */
	public Console getConsole() {
		return console;
	}

	/**
	 * Setter method for the console.
	 *
	 * @param console the console to set
	 */
	public void setConsole(Console console) {
		this.console = console;
	}

	/**
	 * Sends a packet to the server.
	 * <p>
	 * The packet object should be considered invalid afterwards
	 * (hence "destructive") since this method will assemble header
	 * fields in the packet and alter its reader/writer index,
	 * possibly asynchronous to the calling thread.
	 * <p>
	 * TODO: call this method sendDestructive().
	 *
	 * @param packet the packet to send
	 */
	public final void send(StackdPacket packet) {
		endpoint.sendPacketDestructive(packet);
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
	 * Sends a server-side console command to the server.
	 *
	 * @param command the command
	 * @param args    the arguments
	 */
	public void sendConsoleCommand(String command, String[] args) {
		List<String> segments = new ArrayList<>();
		segments.add(command);
		for (String arg : args) {
			segments.add(arg);
		}
		send(new ConsoleInput(ImmutableList.copyOf(segments)));
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
	 * This method gets invoked after receiving the "hello" packet from the server.
	 * The default implementation does nothing.
	 * <p>
	 * NOTE: This method gets invoked by the Netty thread, asynchronous
	 * to the main game thread!
	 */
	protected void onReady() {
		send(new ResumePlayer(AccountApiClient.getInstance().getPlayerAccessToken().getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * This method gets invoked when receiving an application packet from the server.
	 * The default implementation does nothing.
	 * <p>
	 * NOTE: This method gets invoked by the Netty thread, asynchronous
	 * to the main game thread!
	 */
	protected void onMessageReceived(Message untypedMessage) {
		if (untypedMessage instanceof Hello) {

			logger.debug("hello packet received");
			Hello message = (Hello) untypedMessage;
			synchronized (syncObject) {
				sessionId = message.getSessionId();
				if (sessionId < 0) {
					throw new RuntimeException("server sent invalid session ID: " + sessionId);
				}
				syncObject.notifyAll();
			}
			onReady();
			logger.debug("protocol client ready");

		} else if (untypedMessage instanceof FlashMessage) {

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

		} else if (untypedMessage instanceof ConsoleOutput) {

			if (console != null) {
				ConsoleOutput message = (ConsoleOutput) untypedMessage;
				for (String line : message.getSegments()) {
					console.println(line);
				}
			} else {
				logger.error("received console output packet but there's no console set for the StackdProtocolClient!");
			}

		} else if (untypedMessage instanceof PlayerListUpdate) {

			PlayerListUpdate message = (PlayerListUpdate) untypedMessage;
			List<PlayerProxy> playerProxiesFromMessage = new ArrayList<PlayerProxy>();
			for (PlayerListUpdate.Element element : message.getElements()) {
				PlayerProxy proxy = new PlayerProxy(element.getId());
				proxy.getPosition().copyFrom(element.getPosition());
				proxy.getOrientation().copyFrom(element.getAngles());
				playerProxiesFromMessage.add(proxy);
			}
			synchronized (this) {
				this.updatedPlayerProxies = playerProxiesFromMessage;
			}

		} else if (untypedMessage instanceof PlayerNamesUpdate) {

			PlayerNamesUpdate message = (PlayerNamesUpdate) untypedMessage;
			Map<Integer, String> updatedPlayerNames = new HashMap<>();
			for (PlayerNamesUpdate.Element element : message.getElements()) {
				updatedPlayerNames.put(element.getId(), element.getName());
			}
			synchronized (this) {
				this.updatedPlayerNames = updatedPlayerNames;
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
	 * If there is an updated map of player names, returns that
	 * map and deletes it from this object.
	 *
	 * @return the updated player names, or null if no update
	 * is available
	 */
	public synchronized Map<Integer, String> fetchUpdatedPlayerNames() {
		Map<Integer, String> result = updatedPlayerNames;
		updatedPlayerNames = null;
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
