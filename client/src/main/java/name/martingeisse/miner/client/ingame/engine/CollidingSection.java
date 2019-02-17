/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.engine;

import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.collision.IAxisAlignedCollider;
import name.martingeisse.miner.common.geometry.RectangularRegion;
import name.martingeisse.miner.common.section.SectionId;

/**
 * This class wraps an {@link IAxisAlignedCollider} for a section.
 * Instances of this class are stored for sections in the {@link WorldWorkingSet}.
 */
public final class CollidingSection implements IAxisAlignedCollider {

	/**
	 * the workingSet
	 */
	private final WorldWorkingSet workingSet;

	/**
	 * the sectionId
	 */
	private final SectionId sectionId;

	/**
	 * the region
	 */
	private final RectangularRegion region;

	/**
	 * the RenderableSection.java
	 */
	private final IAxisAlignedCollider collider;

	/**
	 * Constructor.
	 * @param workingSet the working set that contains this object
	 * @param sectionId the section id
	 * @param collider the collider
	 */
	public CollidingSection(final WorldWorkingSet workingSet, final SectionId sectionId, final IAxisAlignedCollider collider) {
		this.workingSet = workingSet;
		this.sectionId = sectionId;
		this.region = new RectangularRegion(sectionId.getX(), sectionId.getY(), sectionId.getZ()).multiply(Constants.SECTION_SIZE);
		this.collider = collider;
	}

	/**
	 * Getter method for the workingSet.
	 * @return the workingSet
	 */
	public WorldWorkingSet getWorkingSet() {
		return workingSet;
	}

	/**
	 * Getter method for the sectionId.
	 * @return the sectionId
	 */
	public SectionId getSectionId() {
		return sectionId;
	}

	/**
	 * Getter method for the region.
	 * @return the region
	 */
	public RectangularRegion getRegion() {
		return region;
	}

	/**
	 * Getter method for the collider.
	 * @return the collider
	 */
	public IAxisAlignedCollider getCollider() {
		return collider;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.common.collision.ICubeCollidingObject#getCurrentCollider()
	 */
	@Override
	public IAxisAlignedCollider getCurrentCollider() {
		return this;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.stackd.common.collision.ICubeCollider#collides(name.martingeisse.stackd.common.geometry.RectangularRegion)
	 */
	@Override
	public boolean collides(final RectangularRegion region) {
		return collider.collides(region);
	}

}
