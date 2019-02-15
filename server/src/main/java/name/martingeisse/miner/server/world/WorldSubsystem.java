/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;
import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.network.c2s.PlaceCube;
import name.martingeisse.miner.common.network.s2c.InteractiveSectionDataResponse;
import name.martingeisse.miner.common.section.SectionDataId;
import name.martingeisse.miner.common.section.SectionDataType;
import name.martingeisse.miner.common.section.SectionId;
import name.martingeisse.miner.common.task.Task;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.game.DigUtil;
import name.martingeisse.miner.server.game.Player;
import name.martingeisse.miner.server.world.entry.SectionCubesCacheEntry;
import name.martingeisse.miner.server.world.entry.SectionDataCacheEntry;
import name.martingeisse.miner.server.world.generate.TerrainGenerator;
import name.martingeisse.miner.server.world.storage.CassandraSectionStorage;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Encapsulates all server-side functionality recarding the cube world.
 * <p>
 * TODO Usage from outside: read, place, dig, listener
 * <p>
 * Separate WorldSubsystem from SectionDataCache:
 * - read --(readreq)-> world --(readreq)-> cache (pass through)
 * - place --placereq-> world --(cache entry change)-> cache
 * - dig --digreq-> world; hardness / damage --(cache entry change)-> cache
 * - listener: register with cache; register with world subsystem (e.g. damage changes)
 */
public final class WorldSubsystem {

	private static Logger logger = Logger.getLogger(WorldSubsystem.class);

	private final SectionWorkingSet workingSet;
	private final BlockingQueue<ShippingJob> jobQueue;
	private final HandleAllJobsTask handleAllJobsTask;
	private final Set<WorldModificationListener> modificationListeners = new HashSet<>();

	public WorldSubsystem() {
		this.workingSet = new SectionWorkingSet(new CassandraSectionStorage(Databases.world, "section_data"));
		this.jobQueue = new LinkedBlockingQueue<>();
		this.handleAllJobsTask = new HandleAllJobsTask();
	}

	//
	// --- listeners
	//

	public void addListener(WorldModificationListener listener) {
		modificationListeners.add(listener);
	}

	public void removeListener(WorldModificationListener listener) {
		modificationListeners.remove(listener);
	}

	private void notifyModificationListeners(ImmutableList<SectionId> sectionIds) {
		for (WorldModificationListener listener : modificationListeners) {
			listener.onSectionsModified(sectionIds);
		}
	}

	//
	// --- reading sections
	//

	public interface SectionDataConsumer {
		void consumeInteractiveSectionDataResponse(InteractiveSectionDataResponse response);
	}

	/**
	 * Adds a shipping job.
	 *
	 * @param sectionDataId the ID of the section data to ship
	 * @param consumer      the consumer to return the section data to
	 */
	public void addJob(SectionDataId sectionDataId, SectionDataConsumer consumer) {
		SectionDataCacheEntry presentEntry = workingSet.getIfPresent(sectionDataId);
		if (presentEntry == null) {
			ShippingJob job = new ShippingJob();
			job.sectionDataId = sectionDataId;
			job.consumer = consumer;
			jobQueue.add(job);
			handleAllJobsTask.schedule();
		} else {
			sendResult(presentEntry, consumer);
		}
	}

	/**
	 * Fetches all jobs from the job queue and handles them.
	 */
	public void handleJobs() {

		// Fetch pending jobs, returning if there is nothing to do. This happens quite often because
		// the job handling task gets scheduled for every added job, but the task handles *all*
		// pending jobs, so the remaining task executions only find an empty job queue.
		if (jobQueue.isEmpty()) {
			return;
		}
		ArrayList<ShippingJob> jobs = new ArrayList<WorldSubsystem.ShippingJob>();
		jobQueue.drainTo(jobs);
		if (jobs.isEmpty()) {
			return;
		}

		// collect section data IDs
		Set<SectionDataId> sectionDataIds = new HashSet<>();
		for (ShippingJob job : jobs) {
			sectionDataIds.add(job.sectionDataId);
		}

		// fetch the objects from the cache
		ImmutableMap<SectionDataId, SectionDataCacheEntry> cacheEntries = workingSet.getAll(sectionDataIds);

		// complete the jobs by sending data to the clients
		for (ShippingJob job : jobs) {
			sendResult(cacheEntries.get(job.sectionDataId), job.consumer);
		}

	}

	/**
	 *
	 */
	private void sendResult(SectionDataCacheEntry cacheEntry, SectionDataConsumer consumer) {
		final SectionDataId sectionDataId = cacheEntry.getSectionDataId();
		final SectionId sectionId = sectionDataId.getSectionId();
		final SectionDataType type = sectionDataId.getType();
		// LOD currently not supported
		if (type != SectionDataType.INTERACTIVE) {
			throw new RuntimeException(getClass() + " only supports INTERACTIVE section data");
		}
		final byte[] data = cacheEntry.getDataForClient();
		logger.debug("SERVER sending section data: " + sectionDataId + " (" + data.length + " bytes)");
		consumer.consumeInteractiveSectionDataResponse(new InteractiveSectionDataResponse(sectionId, data));
		logger.debug("SERVER sent section data: " + sectionDataId + " (" + data.length + " bytes)");
		total += data.length;
		logger.debug("SERVER total section data sent: " + total);
	}

	static volatile long total = 0;

	/**
	 * Contains the data for a single shipping job.
	 */
	static final class ShippingJob {
		SectionDataId sectionDataId;
		SectionDataConsumer consumer;
	}

	/**
	 * A task that gets scheduled repeatedly to finish the jobs.
	 */
	class HandleAllJobsTask extends Task {
		@Override
		public void run() {
			try {
				WorldSubsystem.this.handleJobs();
			} catch (Throwable t) {
				logger.error("unexpected exception", t);
			}
		}
	}

	//
	// --- modifications
	//

	public void placeCube(Vector3i position, CubeType cubeType) {
		SectionId sectionId = SectionId.fromPosition(position);
		SectionDataId sectionDataId = new SectionDataId(sectionId, SectionDataType.DEFINITIVE);
		SectionCubesCacheEntry sectionDataCacheEntry = (SectionCubesCacheEntry) workingSet.get(sectionDataId);
		sectionDataCacheEntry.setCubeAbsolute(position, (byte)cubeType.getIndex());
		notifyModificationListenersAboutModifiedPositions(ImmutableList.of(position));
	}

	public void dig(Player player, Vector3i position) {
		SectionId sectionId = SectionId.fromPosition(position);
		SectionDataId sectionDataId = new SectionDataId(sectionId, SectionDataType.DEFINITIVE);
		SectionCubesCacheEntry sectionDataCacheEntry = (SectionCubesCacheEntry) workingSet.get(sectionDataId);

		// check if successful and remove the cube
		byte oldCubeType = sectionDataCacheEntry.getCubeAbsolute(position);
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
		sectionDataCacheEntry.setCubeAbsolute(position, (byte) 0);
		notifyModificationListenersAboutModifiedPositions(ImmutableList.of(position));

		// trigger special logic (e.g. add a unit of ore to the player's inventory)
		if (player != null) {
			DigUtil.onCubeDugAway(player, position, oldCubeType);
		}

	}

	private void notifyModificationListenersAboutModifiedPositions(ImmutableList<Vector3i> positions) {
		// Treating the neighbors as modified is currently necessary. I am not totally sure why, but I suspect it is
		// because their INTERACTIVE data actually changes, even though their DEFINITIVE data does not.
		Set<SectionId> sectionIds = new HashSet<>();
		for (Vector3i position : positions) {
			SectionId sectionId = SectionId.fromPosition(position);
			sectionIds.add(sectionId);
			for (AxisAlignedDirection neighborDirection : Constants.SECTION_SIZE.getBorderDirections(position)) {
				sectionIds.add(sectionId.getNeighbor(neighborDirection));
			}
		}
		notifyModificationListeners(ImmutableList.copyOf(sectionIds));
	}

	//
	// --- initialization
	//

	/**
	 * Initializes the world using a Perlin noise based height field.
	 */
	public void initializeWorldWithHeightField() {
		int horizontalRadius = 10;
		int verticalRadius = 5;
		TerrainGenerator terrainGenerator = new TerrainGenerator();
		terrainGenerator.generate(workingSet.getStorage(), new SectionId(-horizontalRadius, -verticalRadius, -horizontalRadius), new SectionId(horizontalRadius, verticalRadius, horizontalRadius));
		workingSet.clearCache();
		logger.info("world initialized");
	}

}
