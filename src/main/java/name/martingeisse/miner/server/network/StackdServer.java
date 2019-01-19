/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.network;

import com.google.common.collect.ImmutableList;
import name.martingeisse.common.SecurityTokenUtil;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.cubetype.CubeTypes;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;
import name.martingeisse.miner.common.geometry.SectionId;
import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.vector.Vector3d;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.c2s.*;
import name.martingeisse.miner.common.network.message.s2c.PlayerListUpdate;
import name.martingeisse.miner.common.network.message.s2c.PlayerNamesUpdate;
import name.martingeisse.miner.common.network.message.s2c.PlayerResumed;
import name.martingeisse.miner.common.network.message.s2c.SingleSectionModificationEvent;
import name.martingeisse.miner.common.section.SectionDataId;
import name.martingeisse.miner.common.section.SectionDataType;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.MinerConsoleCommandHandler;
import name.martingeisse.miner.server.MinerServerSecurityConstants;
import name.martingeisse.miner.server.TestRegionImporter;
import name.martingeisse.miner.server.console.IConsoleCommandHandler;
import name.martingeisse.miner.server.console.NullConsoleCommandHandler;
import name.martingeisse.miner.server.entities.Player;
import name.martingeisse.miner.server.entities.QPlayer;
import name.martingeisse.miner.server.game.DigUtil;
import name.martingeisse.miner.server.section.SectionToClientShipper;
import name.martingeisse.miner.server.section.SectionWorkingSet;
import name.martingeisse.miner.server.section.entry.SectionCubesCacheEntry;
import name.martingeisse.miner.server.section.storage.AbstractSectionStorage;
import name.martingeisse.miner.server.section.storage.CassandraSectionStorage;
import name.martingeisse.miner.server.terrain.TerrainGenerator;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.joda.time.Instant;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The main server class. Typically, a single instance of this class
 * is used on the server side, and it is actually an application-specific
 * subclass of this class.
 */
public class StackdServer {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(StackdServer.class);

	/**
	 * the sessions
	 */
	private final ConcurrentHashMap<Integer, StackdSession> sessions;

	/**
	 * the sectionWorkingSet
	 */
	private final SectionWorkingSet sectionWorkingSet;

	/**
	 * the sectionToClientShipper
	 */
	private final SectionToClientShipper sectionToClientShipper;

	/**
	 * the cubeTypes
	 */
	private CubeType[] cubeTypes;

	/**
	 * the consoleCommandHandler
	 */
	private IConsoleCommandHandler consoleCommandHandler;

	/**
	 * Constructor.
	 */
	public StackdServer() {
		AbstractSectionStorage sectionStorage = new CassandraSectionStorage(Constants.CLUSTER_SIZE, Databases.world, "section_data");

		this.sessions = new ConcurrentHashMap<Integer, StackdSession>();
		this.sectionWorkingSet = new SectionWorkingSet(this, sectionStorage);
		this.sectionToClientShipper = new SectionToClientShipper(sectionWorkingSet);
		this.cubeTypes = new CubeType[0];
		this.consoleCommandHandler = new NullConsoleCommandHandler();

		setCubeTypes(CubeTypes.CUBE_TYPES);

		Timer timer = new Timer(true);
		timer.schedule(new PlayerListUpdateSender(), 0, 200);
		timer.schedule(new PlayerNameUpdateSender(), 0, 2000);

		//
		setConsoleCommandHandler(new MinerConsoleCommandHandler(this));

		// TODO for testing
		try {
			// initializeWorld();
			// initializeWorldWithHeightField();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Getter method for the sectionWorkingSet.
	 *
	 * @return the sectionWorkingSet
	 */
	public SectionWorkingSet getSectionWorkingSet() {
		return sectionWorkingSet;
	}

	/**
	 * Getter method for the cubeTypes.
	 *
	 * @return the cubeTypes
	 */
	public final CubeType[] getCubeTypes() {
		return cubeTypes;
	}

	/**
	 * Setter method for the cubeTypes.
	 *
	 * @param cubeTypes the cubeTypes to set
	 */
	public final void setCubeTypes(final CubeType[] cubeTypes) {
		this.cubeTypes = cubeTypes;
	}

	/**
	 * Getter method for the consoleCommandHandler.
	 *
	 * @return the consoleCommandHandler
	 */
	public IConsoleCommandHandler getConsoleCommandHandler() {
		return consoleCommandHandler;
	}

	/**
	 * Setter method for the consoleCommandHandler.
	 *
	 * @param consoleCommandHandler the consoleCommandHandler to set
	 */
	public void setConsoleCommandHandler(IConsoleCommandHandler consoleCommandHandler) {
		this.consoleCommandHandler = (consoleCommandHandler == null ? new NullConsoleCommandHandler() : consoleCommandHandler);
	}

	/**
	 * Creates a new session.
	 * <p>
	 * Note that a race condition might cause this method to be invoked twice to
	 * create a session for the same ID. In such a case, the race condition will be
	 * detected later on and one of the sessions will be thrown away. This method must
	 * be able to handle such a case. Use {@link #onClientConnected(StackdSession)}
	 * for code that should only run once per created session.
	 *
	 * @param id      the session ID
	 * @param channel the channel to the client
	 * @return the session
	 */
	protected StackdSession newSession(int id, Channel channel) {
		return new StackdSession(id, channel);
	}

	/**
	 * Returns the session with the specified ID, or null if no such session exists.
	 *
	 * @param id the session ID
	 * @return the session or null
	 */
	public final StackdSession getExistingSession(final int id) {
		return sessions.get(id);
	}

	/**
	 * Returns the session with the specified ID, creating it if it does not yet exist.
	 *
	 * @param id      the session ID
	 * @param channel the channel to use when creating a session
	 * @return the session
	 */
	public final StackdSession getOrCreateSession(final int id, final Channel channel) {

		// shortcut for existing sessions
		StackdSession existingSession = sessions.get(id);
		if (existingSession != null) {
			return existingSession;
		}

		// create and store a new session in a thread-safe way
		final StackdSession newSession = newSession(id, channel);
		existingSession = sessions.putIfAbsent(id, newSession);
		final StackdSession effectiveSession = (existingSession != null ? existingSession : newSession);

		return effectiveSession;
	}

	/**
	 * Creates a new session with a random unused ID and returns it.
	 *
	 * @param channel the channel that connects to the client
	 * @return the session
	 */
	public final StackdSession createSession(final Channel channel) {
		final Random random = new Random();
		while (true) {
			final StackdSession session = newSession(random.nextInt(0x7fffffff), channel);
			if (sessions.putIfAbsent(session.getId(), session) == null) {
				return session;
			}
		}
	}

	/**
	 * Returns a collection that contains all sessions. This collection is
	 * a live view on the session map in this server and will be changed
	 * concurrently by other threads.
	 *
	 * @return the sessions
	 */
	public final Collection<StackdSession> getSessions() {
		return sessions.values();
	}

	/**
	 * Broadcasts a packet to all clients.
	 * <p>
	 * This method takes the list of clients as it exists when calling this
	 * method. Calling code must make sure that the packet does not
	 * contain information that can become invalid when the list of clients
	 * has changed since the packet was built.
	 * <p>
	 * Header fields of the packet will be assembled by this method. This
	 * will actually happen for each channel, but since the header fields
	 * are the same for all channel (they only depend on the packet), this
	 * should not be a problem. (Implementation note: This method still
	 * must ensure that each channel gets its own buffer object with
	 * separate reader/writer index).
	 *
	 * @param packet the packet to broadcast to all clients
	 */
	public final void broadcast(final StackdPacket packet) {
		for (final StackdSession session : sessions.values()) {
			final StackdPacket duplicatePacket = new StackdPacket(packet.getType(), packet.getBuffer().duplicate(), false);
			session.sendPacketDestructive(duplicatePacket);
		}
	}

	public final void broadcast(final Message message) {
		for (final StackdSession session : sessions.values()) {
			session.send(message);
		}
	}

	/**
	 * Internal packet dispatch that gets called in the receiving thread.
	 * TODO should not dispatch directly but through a queue.
	 */
	final void onRawPacketReceived(final StackdSession session, final StackdPacket packet) throws Exception {
		Message untypedMessage = Message.decodePacket(packet);
		if (untypedMessage instanceof CubeModification) {

			CubeModification message = (CubeModification) untypedMessage;
			int shiftBits = sectionWorkingSet.getClusterSize().getShiftBits();
			List<SectionId> affectedSectionIds = new ArrayList<>();
			for (CubeModification.Element element : message.getElements()) {
				int x = element.getPosition().getX(), sectionX = (x >> shiftBits);
				int y = element.getPosition().getY(), sectionY = (y >> shiftBits);
				int z = element.getPosition().getZ(), sectionZ = (z >> shiftBits);
				byte newCubeType = element.getCubeType();
				SectionId sectionId = new SectionId(sectionX, sectionY, sectionZ);
				SectionDataId sectionDataId = new SectionDataId(sectionId, SectionDataType.DEFINITIVE);
				SectionCubesCacheEntry sectionDataCacheEntry = (SectionCubesCacheEntry) sectionWorkingSet.get(sectionDataId);
				sectionDataCacheEntry.setCubeAbsolute(x, y, z, newCubeType);
				affectedSectionIds.add(sectionId);
			}
			SectionId[] affectedSectionIdArray = affectedSectionIds.toArray(new SectionId[affectedSectionIds.size()]);
			onSectionsModified(affectedSectionIdArray);

		} else if (untypedMessage instanceof InteractiveSectionDataRequest) {

			InteractiveSectionDataRequest message = (InteractiveSectionDataRequest) untypedMessage;
			SectionId sectionId = message.getSectionId();
			SectionDataType type = SectionDataType.INTERACTIVE;
			final SectionDataId dataId = new SectionDataId(sectionId, type);
			logger.debug("SERVER received section data request: " + dataId);
			sectionToClientShipper.addJob(dataId, session);

		} else if (untypedMessage instanceof ConsoleInput) {

			ConsoleInput message = (ConsoleInput) untypedMessage;
			ImmutableList<String> segments = message.getSegments();
			String command = segments.get(0);
			String[] args = segments.subList(1, segments.size()).toArray(new String[0]);
			handleConsoleCommand(session, command, args);

		} else if (untypedMessage instanceof UpdatePosition) {

			UpdatePosition message = (UpdatePosition) untypedMessage;
			session.setX(message.getPosition().x);
			session.setY(message.getPosition().y);
			session.setZ(message.getPosition().z);
			session.setLeftAngle(message.getOrientation().horizontalAngle);
			session.setUpAngle(message.getOrientation().verticalAngle);

		} else if (untypedMessage instanceof ResumePlayer) {

			ResumePlayer message = (ResumePlayer) untypedMessage;

			String token = new String(message.getToken(), StandardCharsets.UTF_8);
			String tokenSubject = SecurityTokenUtil.validateToken(token, new Instant(), MinerServerSecurityConstants.SECURITY_TOKEN_SECRET);
			long playerId = Long.parseLong(tokenSubject);

			Player player;
			try (PostgresConnection connection = Databases.main.newConnection()) {
				QPlayer qp = QPlayer.Player;
				player = connection.query().select(qp).from(qp).where(qp.id.eq(playerId)).fetchOne();
				if (player == null) {
					throw new RuntimeException("player not found, id: " + playerId);
				}
			}

			session.setPlayerId(player.getId());
			session.setName(player.getName());
			session.setX(player.getX().doubleValue());
			session.setY(player.getY().doubleValue());
			session.setZ(player.getZ().doubleValue());
			session.setLeftAngle(player.getLeftAngle().doubleValue());
			session.setUpAngle(player.getUpAngle().doubleValue());
			session.sendCoinsUpdate();

			Vector3d position = new Vector3d(session.getX(), session.getY(), session.getZ());
			EulerAngles orientation = new EulerAngles(session.getLeftAngle(), session.getUpAngle(), 0);
			broadcast(new PlayerResumed(position, orientation));

		} else if (untypedMessage instanceof DigNotification) {

			DigNotification message = (DigNotification) untypedMessage;

			// determine the cube being dug away
			int shiftBits = getSectionWorkingSet().getClusterSize().getShiftBits();
			int x = message.getPosition().x, sectionX = (x >> shiftBits);
			int y = message.getPosition().y, sectionY = (y >> shiftBits);
			int z = message.getPosition().z, sectionZ = (z >> shiftBits);
			SectionId id = new SectionId(sectionX, sectionY, sectionZ);
			SectionCubesCacheEntry sectionDataCacheEntry = (SectionCubesCacheEntry) getSectionWorkingSet().get(new SectionDataId(id, SectionDataType.DEFINITIVE));
			byte oldCubeType = sectionDataCacheEntry.getCubeAbsolute(x, y, z);

			// determine whether digging is successful
			boolean success;
			if (oldCubeType == 1 || oldCubeType == 5 || oldCubeType == 15) {
				success = true;
			} else {
				success = (new Random().nextInt(3) < 1);
			}
			if (!success) {
				// TODO enable god mode -- digging always succeeds
				// break;
			}

			// remove the cube and notify other clients
			sectionDataCacheEntry.setCubeAbsolute(x, y, z, (byte) 0);

			// TODO should not be necessary with auto-save
			sectionDataCacheEntry.save();

			// notify listeners
			notifyClientsAboutModifiedSections(id);
			for (AxisAlignedDirection neighborDirection : getSectionWorkingSet().getClusterSize().getBorderDirections(x, y, z)) {
				notifyClientsAboutModifiedSections(id.getNeighbor(neighborDirection));
			}

			// trigger special logic (e.g. add a unit of ore to the player's inventory)
			DigUtil.onCubeDugAway(session, x, y, z, oldCubeType);

		} else {
			logger.error("unknown message: " + untypedMessage);
		}
	}

	/**
	 * This method is called when a client has been connected.
	 *
	 * @param session the client's session
	 */
	protected void onClientConnected(StackdSession session) {
		session.sendFlashMessage("Connected to server.");
		session.sendCoinsUpdate();
	}

	/**
	 * This method is called when one or more sections have been modified.
	 *
	 * @param sections the modified sections
	 */
	protected void onSectionsModified(SectionId[] sectionIds) {
		notifyClientsAboutModifiedSections(sectionIds);
	}

	/**
	 * Sends a "section modified" event packet to all clients.
	 *
	 * @param sectionIds the modified section IDs
	 */
	public void notifyClientsAboutModifiedSections(SectionId... sectionIds) {
		for (SectionId sectionId : sectionIds) {
			broadcast(new SingleSectionModificationEvent(sectionId));
		}
	}

	/**
	 * This method is called when a client channel has been disconnected.
	 */
	final void internalOnClientDisconnected(final StackdSession session) {
		if (session == null) {
			logger.info("client without session disconnected");
		} else {
			final int sessionId = session.getId();
			logger.info("client disconnected: " + sessionId);
			onClientDisconnected(session);
			sessions.remove(sessionId);
		}
	}

	/**
	 * This method is called when a client has been disconnected.
	 * <p>
	 * Special case: Clients that have not been allocated a session
	 * yet are handled without calling this method.
	 *
	 * @param session the client's session
	 */
	protected void onClientDisconnected(StackdSession session) {
		session.handleDisconnect();
	}

	/**
	 * Handles a console command. Such a command is typically sent by a client. Even if
	 * not, the command must at least be associated with a client, and is handled as if
	 * that client sent it.
	 * <p>
	 * The default implementation delegates to the current console command handler set
	 * for this server.
	 *
	 * @param session the client's session
	 * @param command the command
	 * @param args    the arguments
	 */
	public void handleConsoleCommand(final StackdSession session, String command, String[] args) {
		consoleCommandHandler.handleCommand(session, command, args);
	}

	/**
	 * Handles an "initialize world" request.
	 *
	 * @throws Exception on errors
	 */
	public void initializeWorld() throws Exception {
		logger.info("initializing world...");

		TestRegionImporter importer = new TestRegionImporter(getSectionWorkingSet().getStorage());
		// importer.setTranslation(-15, -9, -72);
		// importer.importRegions(new File("resource/fis/region"));
		importer.importRegions(new File("resource/stoneless"));

		getSectionWorkingSet().clearCache();
		logger.info("world initialized");
	}

	/**
	 * Initializes the world using a Perlin noise based height field.
	 */
	public void initializeWorldWithHeightField() {
		int horizontalRadius = 10;
		int verticalRadius = 5;
		TerrainGenerator terrainGenerator = new TerrainGenerator();
		terrainGenerator.generate(getSectionWorkingSet().getStorage(), new SectionId(-horizontalRadius, -verticalRadius, -horizontalRadius), new SectionId(horizontalRadius, verticalRadius, horizontalRadius));
		getSectionWorkingSet().clearCache();
		logger.info("world initialized");
	}

	/**
	 * The runnable is run regularly to send an updated player's
	 * list to all players.
	 */
	private class PlayerListUpdateSender extends TimerTask {

		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {

			// copy the session list to be safe against concurrent modification
			// (the number of sessions must not change since we must allocate
			// a buffer of the correct size in advance)
			List<StackdSession> sessionList = new ArrayList<StackdSession>();
			for (StackdSession session : getSessions()) {
				if (session.getPlayerId() != null) {
					sessionList.add(session);
				}
			}

			// assemble the message
			List<PlayerListUpdate.Element> elements = new ArrayList<>();
			for (StackdSession session : sessionList) {
				Vector3d position = new Vector3d(session.getX(), session.getY(), session.getZ());
				EulerAngles eulerAngles = new EulerAngles(session.getLeftAngle(), session.getUpAngle(), 0);
				elements.add(new PlayerListUpdate.Element(session.getId(), position, eulerAngles));
			}

			// send the packet
			// broadcast(packet);
			broadcast(new PlayerListUpdate(ImmutableList.copyOf(elements)));

		}

	}

	/**
	 * The runnable is run regularly to tell the clients each other's names.
	 */
	private class PlayerNameUpdateSender extends TimerTask {
		@Override
		public void run() {
			List<PlayerNamesUpdate.Element> elements = new ArrayList<>();
			for (StackdSession session : getSessions()) {
				elements.add(new PlayerNamesUpdate.Element(session.getId(), session.getName()));
			}
			broadcast(new PlayerNamesUpdate(ImmutableList.copyOf(elements)));
		}
	}

}
