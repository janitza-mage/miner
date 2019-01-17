/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.network;

import name.martingeisse.miner.client.console.Console;
import name.martingeisse.miner.client.frame.handlers.FlashMessageHandler;
import name.martingeisse.miner.client.ingame.IngameHandler;
import name.martingeisse.miner.client.ingame.network.PlayerResumedMessage;
import name.martingeisse.miner.client.ingame.player.PlayerProxy;
import name.martingeisse.miner.client.startmenu.AccountApiClient;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.angle.ReadableEulerAngles;
import name.martingeisse.miner.common.geometry.vector.ReadableVector3d;
import name.martingeisse.miner.common.geometry.vector.Vector3d;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.StackdPacketCodec;
import name.martingeisse.miner.common.network.message.MessageCodes;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.ThreadRenamingRunnable;

import java.io.IOException;
import java.net.InetSocketAddress;
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

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(StackdProtocolClient.class);

	/**
	 * the connectFuture
	 */
	private final ChannelFuture connectFuture;

	/**
	 * the syncObject
	 */
	private final Object syncObject = new Object();

	/**
	 * the sessionId
	 */
	private int sessionId = -1;

	/**
	 * the flashMessageHandler
	 */
	private FlashMessageHandler flashMessageHandler;

	/**
	 * the sectionGridLoader
	 */
	private SectionGridLoader sectionGridLoader;

	/**
	 * the console
	 */
	private Console console;

	/**
	 * the updatedPlayerProxies
	 */
	private List<PlayerProxy> updatedPlayerProxies;

	/**
	 * the updatedPlayerNames
	 */
	private Map<Integer, String> updatedPlayerNames;

	/**
	 * the playerResumedMessage
	 */
	private PlayerResumedMessage playerResumedMessage;

	/**
	 * the coins
	 */
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
		// final ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		final ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());
		final ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() {
				return Channels.pipeline(StackdPacketCodec.createFrameCodec(), new StackdPacketCodec(), new ApplicationHandler());
			}
		});
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		connectFuture = bootstrap.connect(new InetSocketAddress(host, port));
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
		logger.debug("client sent packet " + packet.getType());
		connectFuture.getChannel().write(packet);
	}

	/**
	 * Disconnects from the server.
	 */
	public final void disconnect() {
		connectFuture.getChannel().disconnect();
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
		throw new RuntimeException(e);
	}

	/**
	 * Sends a server-side console command to the server.
	 *
	 * @param command the command
	 * @param args    the arguments
	 */
	public void sendConsoleCommand(String command, String[] args) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeZero(StackdPacket.HEADER_SIZE);
		try (ChannelBufferOutputStream out = new ChannelBufferOutputStream(buffer)) {
			out.writeUTF(command);
			for (String arg : args) {
				out.writeUTF(arg);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		send(new StackdPacket(MessageCodes.C2S_CONSOLE_INPUT, buffer, false));
	}

	/**
	 * The netty handler class.
	 */
	final class ApplicationHandler extends SimpleChannelHandler {

		/* (non-Javadoc)
		 * @see org.jboss.netty.channel.SimpleChannelHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
		 */
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			StackdPacket packet = (StackdPacket) e.getMessage();
			logger.debug("client received packet " + packet.getType());
			onApplicationPacketReceived(packet);
		}

		/* (non-Javadoc)
		 * @see org.jboss.netty.channel.SimpleChannelHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
		 */
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			onException(e.getCause());
		}

	}

	/**
	 * Sends an update message for the player's position to the server.
	 *
	 * @param position    the player's position
	 * @param orientation the player's orientation
	 */
	public void sendPositionUpdate(ReadableVector3d position, ReadableEulerAngles orientation) {
		StackdPacket packet = new StackdPacket(MessageCodes.C2S_UPDATE_POSITION, 40);
		ChannelBuffer buffer = packet.getBuffer();
		buffer.writeDouble(position.getX());
		buffer.writeDouble(position.getY());
		buffer.writeDouble(position.getZ());
		buffer.writeDouble(orientation.getHorizontalAngle());
		buffer.writeDouble(orientation.getVerticalAngle());
		send(packet);
	}

	/**
	 * This method gets invoked after receiving the "hello" packet from the server.
	 * The default implementation does nothing.
	 * <p>
	 * NOTE: This method gets invoked by the Netty thread, asynchronous
	 * to the main game thread!
	 */
	protected void onReady() {

		// send the "resume player" packet
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeZero(StackdPacket.HEADER_SIZE);
		byte[] tokenBytes = AccountApiClient.getInstance().getPlayerAccessToken().getBytes(StandardCharsets.UTF_8);
		buffer.writeInt(tokenBytes.length);
		buffer.writeBytes(tokenBytes);
		send(new StackdPacket(MessageCodes.C2S_RESUME_PLAYER, buffer, false));

	}

	/**
	 * This method gets invoked when receiving an application packet from the server.
	 * The default implementation does nothing.
	 * <p>
	 * NOTE: This method gets invoked by the Netty thread, asynchronous
	 * to the main game thread!
	 *
	 * @param packet the received packet
	 */
	protected void onApplicationPacketReceived(StackdPacket packet) {
		ChannelBuffer buffer = packet.getBuffer();
		switch (packet.getType()) {

			case MessageCodes.S2C_HELLO: {
				logger.debug("hello packet received");
				synchronized (syncObject) {
					sessionId = buffer.readInt();
					if (sessionId < 0) {
						throw new RuntimeException("server sent invalid session ID: " + sessionId);
					}
					syncObject.notifyAll();
				}
				onReady();
				logger.debug("protocol client ready");
				break;
			}

			case MessageCodes.S2C_FLASH_MESSAGE: {
				byte[] binary = new byte[buffer.readableBytes()];
				buffer.readBytes(binary);
				String message = new String(binary, StandardCharsets.UTF_8);
				onFlashMessageReceived(message);
				break;
			}

			case MessageCodes.S2C_INTERACTIVE_SECTION_DATA_RESPONSE: {
				if (sectionGridLoader != null) {
					sectionGridLoader.handleInteractiveSectionImagePacket(packet);
				} else {
					logger.error("received interactive section image but no sectionGridLoader is set in the StackdProtoclClient!");
				}
				break;
			}

			case MessageCodes.S2C_SINGLE_SECTION_MODIFICATION_EVENT: {
				if (sectionGridLoader != null) {
					sectionGridLoader.handleModificationEventPacket(packet);
				} else {
					logger.error("received section modification event but no sectionGridLoader is set in the StackdProtoclClient!");
				}
				break;
			}

			case MessageCodes.S2C_CONSOLE_OUTPUT: {
				if (console != null) {
					try (ChannelBufferInputStream in = new ChannelBufferInputStream(buffer)) {
						while (buffer.readable()) {
							console.println(in.readUTF());
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				} else {
					logger.error("received console output packet but there's no console set for the StackdProtocolClient!");
				}
				break;
			}

			case MessageCodes.S2C_PLAYER_LIST_UPDATE: {
				List<PlayerProxy> playerProxiesFromMessage = new ArrayList<PlayerProxy>();
				while (buffer.readableBytes() >= 28) {
					int id = buffer.readInt();
					double x = buffer.readDouble();
					double y = buffer.readDouble();
					double z = buffer.readDouble();
					double horizontalAngle = buffer.readDouble();
					double verticalAngle = buffer.readDouble();
					PlayerProxy proxy = new PlayerProxy(id);
					proxy.getPosition().setX(x);
					proxy.getPosition().setY(y);
					proxy.getPosition().setZ(z);
					proxy.getOrientation().setHorizontalAngle(horizontalAngle);
					proxy.getOrientation().setVerticalAngle(verticalAngle);
					playerProxiesFromMessage.add(proxy);
				}
				synchronized (this) {
					this.updatedPlayerProxies = playerProxiesFromMessage;
				}
				break;
			}

			case MessageCodes.S2C_PLAYER_NAMES_UPDATE: {
				Map<Integer, String> updatedPlayerNames = new HashMap<Integer, String>();
				while (buffer.readableBytes() > 0) {
					int id = buffer.readInt();
					int length = buffer.readInt();
					byte[] data = new byte[2 * length];
					buffer.readBytes(data);
					try {
						updatedPlayerNames.put(id, new String(data, "UTF-16"));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				synchronized (this) {
					this.updatedPlayerNames = updatedPlayerNames;
				}
				break;
			}

			case MessageCodes.S2C_PLAYER_RESUMED: {
				synchronized (this) {
					double x = buffer.readDouble();
					double y = buffer.readDouble();
					double z = buffer.readDouble();
					double horizontalAngle = buffer.readDouble();
					double verticalAngle = buffer.readDouble();
					double rollAngle = 0;
					this.playerResumedMessage = new PlayerResumedMessage(new Vector3d(x, y, z), new EulerAngles(horizontalAngle, verticalAngle, rollAngle));
				}
				break;
			}

			case MessageCodes.S2C_UPDATE_COINS: {
				this.coins = buffer.readLong();
				logger.info("update coins: " + coins);
				break;
			}

			default:
				logger.error("unknown packet type: " + packet.getType());
				break;

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
		StackdPacket packet = new StackdPacket(MessageCodes.C2S_DIG_NOTIFICATION, 13);
		ChannelBuffer buffer = packet.getBuffer();
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		send(packet);
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
