/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.world.entry;

import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.cubes.Cubes;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;
import name.martingeisse.miner.common.geometry.vector.Vector3i;
import name.martingeisse.miner.common.section.SectionDataId;
import name.martingeisse.miner.common.section.SectionDataType;
import name.martingeisse.miner.common.section.SectionId;
import name.martingeisse.miner.server.world.SectionWorkingSet;

/**
 * A section cache entry for the section data.
 */
public final class SectionCubesCacheEntry extends SectionDataCacheEntry {

	private final Vector3i anchor;
	private Cubes sectionCubes;

	/**
	 * Constructor.
	 * @param sectionWorkingSet the working set from which this cached object comes from
	 * @param sectionDataId the section data id
	 * @param sectionCubes the section data
	 */
	public SectionCubesCacheEntry(final SectionWorkingSet sectionWorkingSet, final SectionDataId sectionDataId, final Cubes sectionCubes) {
		super(sectionWorkingSet, sectionDataId);
		SectionId sectionId = sectionDataId.getSectionId();
		this.anchor = new Vector3i(sectionId.getX(), sectionId.getY(), sectionId.getZ()).multiply(Constants.SECTION_SIZE.getSize());
		this.sectionCubes = sectionCubes;
	}

	/**
	 * Getter method for the sectionCubes.
	 * @return the sectionCubes
	 */
	public Cubes getSectionCubes() {
		return sectionCubes;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.server.section.AbstractSectionRelatedCachedObject#serializeForSave()
	 */
	@Override
	protected byte[] serializeForSave() {
		return sectionCubes.compressToByteArray(Constants.SECTION_SIZE);
	}

	/**
	 * Returns the cube value for the specified absolute position.
	 */
	public final byte getCubeAbsolute(Vector3i position) {
		return getCubeRelative(position.subtract(anchor));
	}

	/**
	 * Returns the cube value for the specified section-relative position.
	 */
	public final byte getCubeRelative(Vector3i position) {
		return sectionCubes.getCubeRelative(Constants.SECTION_SIZE, position);
	}

	/**
	 * Sets the cube value for the specified absolute position.
	 */
	public final void setCubeAbsolute(Vector3i position, final byte value) {
		setCubeRelative(position.subtract(anchor), value);
	}

	/**
	 * Sets the cube value for the specified section-relative position.
	 */
	public final void setCubeRelative(Vector3i position, final byte value) {
		this.sectionCubes = sectionCubes.setCubeRelative(Constants.SECTION_SIZE, position, value);
		markCubeModifiedRelative(position);
	}

	/**
	 * Marks this cache entry as modified, and possibly neighbor sections as well. The cube
	 * position is specified in absolute coordinates.
	 */
	public final void markCubeModifiedAbsolute(Vector3i position) {
		markCubeModifiedRelative(position.subtract(anchor));
	}

	/**
	 * Marks this cache entry as modified, and possibly neighbor sections as well. The cube
	 * position is specified relative to this section.
	 */
	public synchronized final void markCubeModifiedRelative(Vector3i position) {

		// mark modified as usual
		markModified();
		
		// Mark neighbor sections modified if this cube is on the border. The reason we need to do this is because the
		// cube in the neighbor section gets sent as "unknown" to the client first since it's obscured, and by
		// revealing that cube, the client has to know what kind of cube it is. Thus, modifying a cube at a section
		// boundary must invalidate the INTERACTIVE image for the neighbor section.
		SectionDataId sectionDataId = getSectionDataId();
		for (AxisAlignedDirection neighborDirection : Constants.SECTION_SIZE.getBorderDirections(position)) {
			SectionDataId neighborInteractiveDataId = sectionDataId.getNeighbor(neighborDirection, SectionDataType.INTERACTIVE);
			InteractiveSectionImageCacheEntry neighborInteractiveDataEntry = (InteractiveSectionImageCacheEntry)getSectionWorkingSet().get(neighborInteractiveDataId);
			neighborInteractiveDataEntry.invalidateData();
		}

	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.server.section.AbstractSectionRelatedCacheEntry#onModification()
	 */
	@Override
	protected void onModification() {
		SectionDataId sectionDataId = getSectionDataId();
		((InteractiveSectionImageCacheEntry)getSectionWorkingSet().get(sectionDataId.getWithType(SectionDataType.INTERACTIVE))).invalidateData();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.server.section.SectionDataCacheEntry#getDataForClient()
	 */
	@Override
	public byte[] getDataForClient() {
		// this object cannot be sent to the client to prevent information cheating
		return new byte[0];
	}
	
}
