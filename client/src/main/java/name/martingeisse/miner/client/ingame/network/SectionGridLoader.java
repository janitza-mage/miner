/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.network;

import name.martingeisse.miner.client.ingame.engine.InteractiveSection;
import name.martingeisse.miner.client.ingame.engine.WorldWorkingSet;
import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.cubes.Cubes;
import name.martingeisse.miner.common.network.c2s.InteractiveSectionDataRequest;
import name.martingeisse.miner.common.network.s2c.InteractiveSectionDataResponse;
import name.martingeisse.miner.common.network.s2c.SingleSectionModificationEvent;
import name.martingeisse.miner.common.section.SectionId;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class updates the set of sections in the {@link WorldWorkingSet} based
 * on the viewer's position. It requests new sections by sending section request
 * messages to the server and receives the responses via the in-game message router.
 * <p>
 * Sending a request for sections is not initiated by this class; this class
 * only provides a method to do so. Clients should call that method in regular
 * intervals. TODO provide a frame handler for that.
 * <p>
 * The current implementation is very simple: It keeps a cube-shaped region
 * of sections in the working set. The region contains an odd number of
 * sections along each axis, with the viewer in the middle section.
 * The section set changes whenever the viewer moves to another section.
 * In other words, the number of active sections is (2r+1)^3, with
 * r being the "radius" of the active set. The radius is a parameter for
 * the constructor of this class.
 */
public final class SectionGridLoader {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(SectionGridLoader.class);

	private final WorldWorkingSet workingSet;
	private final int interactiveRadius;
	private SectionId viewerPosition;

	/**
	 * Constructor.
	 *
	 * @param workingSet        the working set to load sections for
	 * @param interactiveRadius the "radius" of the active set of interactive sections
	 */
	public SectionGridLoader(final WorldWorkingSet workingSet, final int interactiveRadius) {
		this.workingSet = workingSet;
		this.interactiveRadius = interactiveRadius;
		this.viewerPosition = null;
	}

	/**
	 * Getter method for the viewerPosition.
	 *
	 * @return the viewerPosition
	 */
	public SectionId getViewerPosition() {
		return viewerPosition;
	}

	/**
	 * Setter method for the viewerPosition.
	 *
	 * @param viewerPosition the viewerPosition to set
	 */
	public void setViewerPosition(SectionId viewerPosition) {
		this.viewerPosition = viewerPosition;
	}

	/**
	 * Updates the set of visible sections, based on the viewer's position which
	 * was previously set via {@link #setViewerPosition(SectionId)} (mandatory).
	 *
	 * @return true if anything was update-requested, false if everything stays the same
	 */
	public boolean update() {
		boolean anythingUpdated = false;

		// ProfilingHelper.start();

		// check that a position was set.
		if (viewerPosition == null) {
			throw new IllegalStateException("viewer position not set");
		}

		// remove all sections that are too far away
		if (restrictMapToRadius(workingSet.getInteractiveSections(), interactiveRadius + 1)) {
			workingSet.markRenderModelsModified();
			anythingUpdated = true;
		}

		// ProfilingHelper.checkRelevant("update sections 1");

		// if the protocol client isn't ready yet, we cannot load anything
		if (!ClientEndpoint.INSTANCE.isConnected()) {
			logger.debug("cannot load sections, protocol client not ready yet");
			return anythingUpdated;
		}

		// detect missing section render models in the viewer's proximity, then request them all at once
		// logger.trace("checking for missing section data; position: " + viewerPosition);
		// TODO implement a batch request packet
		// TODO fetch non-interactive data for "far" sections
		{
			final List<SectionId> missingSectionIds = findMissingSectionIds(workingSet.getInteractiveSections().keySet(), interactiveRadius);
			if (missingSectionIds != null && !missingSectionIds.isEmpty()) {
				final SectionId[] sectionIds = missingSectionIds.toArray(new SectionId[missingSectionIds.size()]);
				for (SectionId sectionId : sectionIds) {
					logger.debug("requested render model update for section " + sectionId);
					ClientEndpoint.INSTANCE.send(new InteractiveSectionDataRequest(sectionId));
					anythingUpdated = true;
				}
			}
		}

		// ProfilingHelper.checkRelevant("update sections 2");

		return anythingUpdated;
	}

	/**
	 * Reloads a single section by requesting its render model and/or collider from the server.
	 *
	 * @param sectionId the ID of the section to reload
	 */
	public void reloadSection(SectionId sectionId) {
		// TODO check distance; adjust data type; fetch at all?
		ClientEndpoint.INSTANCE.send(new InteractiveSectionDataRequest(sectionId));
	}

	/**
	 * Handles a single interactive section image that was received from the server.
	 */
	public void handleInteractiveSectionImage(InteractiveSectionDataResponse response) {
		final SectionId sectionId = response.getSectionId();
		logger.debug("received interactive section image for section " + sectionId);
		byte[] data = response.getData();
		final Cubes cubes = Cubes.createFromCompressedData(Constants.SECTION_SIZE, data);
		logger.debug("created Cubes instance for section " + sectionId);
		workingSet.getInteractiveSectionsLoadedQueue().add(new InteractiveSection(workingSet, sectionId, cubes));
		logger.debug("consumed interactive section image for section " + sectionId);
	}

	/**
	 * Handles a single section modification event that was received from the server.
	 * Note that such packets are ignored here until the player's position has been set.
	 */
	public void handleModificationEvent(SingleSectionModificationEvent event) {
		reloadSection(event.getSectionId());
	}

	/**
	 * Removes all entries from the map whose keys are too far away from the center,
	 * currently using "city block distance" (leaving a rectangular region), not
	 * euclidian distance (which would leave a sphere).
	 * <p>
	 * Returns true if any entries have been removed.
	 */
	private boolean restrictMapToRadius(final Map<SectionId, ?> map, final int radius) {
		List<SectionId> idsToRemoveOld = null;
		for (final Map.Entry<SectionId, ?> entry : map.entrySet()) {
			final SectionId id = entry.getKey();
			final int dx = id.getX() - viewerPosition.getX(), dy = id.getY() - viewerPosition.getY(), dz = id.getZ() - viewerPosition.getZ();
			if (dx < -radius || dx > radius || dy < -radius || dy > radius || dz < -radius || dz > radius) {
				if (idsToRemoveOld == null) {
					idsToRemoveOld = new ArrayList<SectionId>();
				}
				idsToRemoveOld.add(id);
			}
		}
		if (idsToRemoveOld != null) {
			map.keySet().removeAll(idsToRemoveOld);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Builds a list of section IDs that are "close enough" to the center and are not yet
	 * present in the specified set of section IDs. May return null instead of an
	 * empty list.
	 */
	private List<SectionId> findMissingSectionIds(final Set<SectionId> presentSectionIds, final int radius) {
		List<SectionId> missingSectionIds = null;
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					final int cx = viewerPosition.getX() + dx, cy = viewerPosition.getY() + dy, cz = viewerPosition.getZ() + dz;
					final SectionId id = new SectionId(cx, cy, cz);
					if (!presentSectionIds.contains(id)) {
						if (missingSectionIds == null) {
							missingSectionIds = new ArrayList<SectionId>();
						}
						missingSectionIds.add(id);
					}
				}
			}
		}
		return missingSectionIds;
	}

}
