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
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.c2s.*;
import name.martingeisse.miner.common.network.s2c.PlayerListUpdate;
import name.martingeisse.miner.common.network.s2c.SingleSectionModificationEvent;
import name.martingeisse.miner.common.section.SectionDataId;
import name.martingeisse.miner.common.section.SectionDataType;
import name.martingeisse.miner.common.section.SectionId;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.MinerServerSecurityConstants;
import name.martingeisse.miner.server.console.IConsoleCommandHandler;
import name.martingeisse.miner.server.console.MinerConsoleCommandHandler;
import name.martingeisse.miner.server.console.NullConsoleCommandHandler;
import name.martingeisse.miner.server.game.DigUtil;
import name.martingeisse.miner.server.game.PlayerAccess;
import name.martingeisse.miner.server.world.WorldSubsystem;
import name.martingeisse.miner.server.world.SectionWorkingSet;
import name.martingeisse.miner.server.world.storage.AbstractSectionStorage;
import name.martingeisse.miner.server.world.storage.CassandraSectionStorage;
import name.martingeisse.miner.server.world.terrain.TerrainGenerator;
import org.apache.log4j.Logger;
import org.joda.time.Instant;

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
	private final ConcurrentHashMap<StackdSession, StackdSession> sessions;

	/**
	 * the sectionWorkingSet
	 */
	private final SectionWorkingSet sectionWorkingSet;

	/**
	 * the worldSubsystem
	 */
	private final WorldSubsystem worldSubsystem;

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
		AbstractSectionStorage sectionStorage = new CassandraSectionStorage(Constants.SECTION_SIZE, Databases.world, "section_data");

		this.sessions = new ConcurrentHashMap<>();
		this.sectionWorkingSet = new SectionWorkingSet(this, sectionStorage);
		this.worldSubsystem = new WorldSubsystem(sectionWorkingSet);
		worldSubsystem.addListener(this::onSectionsModified);
		this.cubeTypes = new CubeType[0];
		this.consoleCommandHandler = new NullConsoleCommandHandler();

		setCubeTypes(CubeTypes.CUBE_TYPES);

		Timer timer = new Timer(true);
		timer.schedule(new AvatarUpdateSender(), 0, 200);

		//
		setConsoleCommandHandler(new MinerConsoleCommandHandler(this));

		// TODO for testing
		try {
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
	 * Creates a new session with a random unused ID and returns it.
	 */
	public final StackdSession createSession(final ServerEndpoint endpoint) {
		final StackdSession session = new StackdSession(endpoint);
		sessions.put(session, session);
		return session;
	}

	/**
	 * Returns a collection that contains all sessions. This collection is "live" and will be changed
	 * concurrently by other threads.
	 */
	public final Set<StackdSession> getSessions() {
		return sessions.keySet();
	}

	public final void broadcast(final Message message) {
		for (final StackdSession session : sessions.keySet()) {
			session.send(message);
		}
	}

	/**
	 * Internal packet dispatch that gets called in the receiving thread.
	 * TODO should not dispatch directly but through a queue.
	 */
	final void onMessageReceived(final StackdSession session, final Message untypedMessage) {
		if (untypedMessage instanceof CubeModification) {

			worldSubsystem.handleMessage((CubeModification) untypedMessage);

		} else if (untypedMessage instanceof InteractiveSectionDataRequest) {

			InteractiveSectionDataRequest message = (InteractiveSectionDataRequest) untypedMessage;
			SectionId sectionId = message.getSectionId();
			SectionDataType type = SectionDataType.INTERACTIVE;
			final SectionDataId dataId = new SectionDataId(sectionId, type);
			logger.debug("SERVER received section data request: " + dataId);
			worldSubsystem.addJob(dataId, session);

		} else if (untypedMessage instanceof ConsoleInput) {

			ConsoleInput message = (ConsoleInput) untypedMessage;
			ImmutableList<String> segments = message.getSegments();
			String command = segments.get(0);
			String[] args = segments.subList(1, segments.size()).toArray(new String[0]);
			handleConsoleCommand(session, command, args);

		} else if (untypedMessage instanceof UpdatePosition) {

			UpdatePosition message = (UpdatePosition) untypedMessage;
			session.getAvatar().setPosition(message.getPosition());
			session.getAvatar().setOrientation(message.getOrientation());

		} else if (untypedMessage instanceof ResumePlayer) {

			ResumePlayer message = (ResumePlayer) untypedMessage;

			String token = new String(message.getToken(), StandardCharsets.UTF_8);
			String tokenSubject = SecurityTokenUtil.validateToken(token, new Instant(), MinerServerSecurityConstants.SECURITY_TOKEN_SECRET);
			long playerId = Long.parseLong(tokenSubject);
			session.selectPlayer(playerId);
			session.createAvatar();

		} else if (untypedMessage instanceof DigNotification) {

			// check if successful and remove the cube
			DigNotification message = (DigNotification) untypedMessage;
			byte oldCubeType = worldSubsystem.getCube(message.getPosition());
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
			worldSubsystem.setCube(message.getPosition(), (byte) 0);

			// trigger special logic (e.g. add a unit of ore to the player's inventory)
			PlayerAccess playerAccess = session.getPlayerAccess();
			if (playerAccess != null) {
				DigUtil.onCubeDugAway(playerAccess, message.getPosition(), oldCubeType);
			}

		} else {
			logger.error("unknown message: " + untypedMessage);
		}
	}

	/**
	 * This method is called when one or more sections have been modified.
	 */
	protected void onSectionsModified(ImmutableList<SectionId> sectionIds) {
		for (SectionId sectionId : sectionIds) {
			broadcast(new SingleSectionModificationEvent(sectionId));
		}
	}

	/**
	 * This method is called when a client channel has been disconnected.
	 */
	final void removeSession(final StackdSession session) {
		if (session != null) {
			sessions.remove(session);
		}
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
	 * Sends updates for position, orientation and name of all avatars to all clients.
	 */
	private class AvatarUpdateSender extends TimerTask {

		@Override
		public void run() {
			for (final StackdSession recipientSession : sessions.keySet()) {
				List<PlayerListUpdate.Element> elements = new ArrayList<>();
				for (StackdSession avatarSession : getSessions()) {
					if (avatarSession != recipientSession) {
						Avatar avatar = avatarSession.getAvatar();
						if (avatar != null) {
							elements.add(new PlayerListUpdate.Element(avatar.getPosition(), avatar.getOrientation(), avatar.getName()));
						}
					}
				}
				if (elements.size() > 0) {
					recipientSession.send(new PlayerListUpdate(ImmutableList.copyOf(elements)));
				}
			}
		}

	}

}
