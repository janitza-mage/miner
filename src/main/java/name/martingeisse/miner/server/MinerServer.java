/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server;

import com.google.common.collect.ImmutableList;
import name.martingeisse.common.SecurityTokenUtil;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.cubetype.CubeTypes;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;
import name.martingeisse.miner.common.geometry.SectionId;
import name.martingeisse.miner.common.geometry.angle.EulerAngles;
import name.martingeisse.miner.common.geometry.vector.Vector3d;
import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageCodes;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import name.martingeisse.miner.common.network.message.c2s.DigNotification;
import name.martingeisse.miner.common.network.message.c2s.ResumePlayer;
import name.martingeisse.miner.common.network.message.c2s.UpdatePosition;
import name.martingeisse.miner.common.network.message.s2c.PlayerListUpdate;
import name.martingeisse.miner.common.network.message.s2c.PlayerNamesUpdate;
import name.martingeisse.miner.common.network.message.s2c.PlayerResumed;
import name.martingeisse.miner.common.network.message.s2c.SingleSectionModificationEvent;
import name.martingeisse.miner.common.section.SectionDataId;
import name.martingeisse.miner.common.section.SectionDataType;
import name.martingeisse.miner.server.entities.Player;
import name.martingeisse.miner.server.entities.QPlayer;
import name.martingeisse.miner.server.game.DigUtil;
import name.martingeisse.miner.server.network.StackdServer;
import name.martingeisse.miner.server.section.entry.SectionCubesCacheEntry;
import name.martingeisse.miner.server.section.storage.CassandraSectionStorage;
import name.martingeisse.miner.server.terrain.TerrainGenerator;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.joda.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * High-level server code.
 * <p>
 * Applications can subclass this class to implement their own functionality,
 * mainly by implementing
 * {@link #handleApplicationRequest(HttpServletRequest, HttpServletResponse)}.
 */
public class MinerServer extends StackdServer<MinerSession> {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(MinerServer.class);

	/**
	 * Constructor.
	 */
	public MinerServer() {
		super(new CassandraSectionStorage(Constants.CLUSTER_SIZE, Databases.world, "section_data"));
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

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.server.StackdServer#newSession(int, org.jboss.netty.channel.Channel)
	 */
	@Override
	protected MinerSession newSession(int id, Channel channel) {
		return new MinerSession(id, channel);
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
	 * Handles an application-specific request.
	 *
	 * @param request  the HTTP request
	 * @param response the HTTP response
	 * @throws Exception on errors
	 */
	public void handleApplicationRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.server.StackdServer#onClientConnected(name.martingeisse.stackd.server.StackdSession)
	 */
	@Override
	protected void onClientConnected(MinerSession session) {
		session.sendFlashMessage("Connected to server.");
		session.sendCoinsUpdate();
	}

	@Override
	protected void onApplicationPacketReceived(MinerSession session, Message untypedMessage) {
		if (untypedMessage instanceof UpdatePosition) {

			UpdatePosition message = (UpdatePosition) untypedMessage;
			session.setX(message.getPosition().x);
			session.setY(message.getPosition().y);
			session.setZ(message.getPosition().z);
			session.setLeftAngle(message.getOrientation().horizontalAngle);
			session.setUpAngle(message.getOrientation().verticalAngle);

		} else if (untypedMessage instanceof ResumePlayer) {

			ResumePlayer message = (ResumePlayer)untypedMessage;

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

			DigNotification message = (DigNotification)untypedMessage;

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

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.server.network.StackdServer#onSectionsModified(name.martingeisse.stackd.common.geometry.SectionId[])
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.server.StackdServer#onClientDisconnected(name.martingeisse.stackd.server.StackdSession)
	 */
	@Override
	protected void onClientDisconnected(MinerSession session) {
		session.handleDisconnect();
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
			List<MinerSession> sessionList = new ArrayList<MinerSession>();
			for (MinerSession session : getSessions()) {
				if (session.getPlayerId() != null) {
					sessionList.add(session);
				}
			}

			// assemble the message
			List<PlayerListUpdate.Element> elements = new ArrayList<>();
			for (MinerSession session : sessionList) {
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
			for (MinerSession session : getSessions()) {
				elements.add(new PlayerNamesUpdate.Element(session.getId(), session.getName()));
			}
			broadcast(new PlayerNamesUpdate(ImmutableList.copyOf(elements)));
		}
	}

}
