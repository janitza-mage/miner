/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.section;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;
import name.martingeisse.miner.common.geometry.ClusterSize;
import name.martingeisse.miner.common.geometry.vector.ReadableVector3d;
import name.martingeisse.miner.common.geometry.vector.ReadableVector3i;
import name.martingeisse.miner.common.geometry.vector.Vector3i;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A section ID is used as the identifying key for a section, and also
 * stores the location of the section in the grid. The location is measured
 * in cluster-size units.
 * <p>
 * Although possible in general, this class does not use {@link Vector3i}
 * since section IDs use coordinates in cluster-size units, not cube units, so
 * using a vector class would encourage mistakes that cannot be caught by the
 * type system.
 */
public final class SectionId {

	public static final int ENCODED_SIZE = 12;

	/**
	 * the x
	 */
	private final int x;

	/**
	 * the y
	 */
	private final int y;

	/**
	 * the z
	 */
	private final int z;

	/**
	 * Constructor for known (x, y, z) coordinates of the section in cluster-size units.
	 *
	 * @param x the x coordinate of the location in the grid, measured in cluster-size units
	 * @param y the y coordinate of the location in the grid, measured in cluster-size units
	 * @param z the z coordinate of the location in the grid, measured in cluster-size units
	 */
	public SectionId(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Factory method for scaling a cube-unit (x, y, z) position to section units.
	 *
	 * @param x           the x coordinate of the cube position
	 * @param y           the y coordinate of the cube position
	 * @param z           the z coordinate of the cube position
	 */
	public static SectionId fromPosition(int x, int y, int z) {
		int shift = Constants.SECTION_SIZE.getShiftBits();
		return new SectionId(x >> shift, y >> shift, z >> shift);
	}

	/**
	 * Factory method for scaling a cube-unit (x, y, z) position to cluster-size units.
	 *
	 * @param position    the cube position
	 */
	public static SectionId fromPosition(ReadableVector3i position) {
		return fromPosition(position.getX(), position.getY(), position.getZ());
	}

	/**
	 * Factory method for scaling a cube-unit (x, y, z) position to cluster-size units.
	 *
	 * @param position    the cube position
	 */
	public static SectionId fromPosition(ReadableVector3d position) {
		return fromPosition((int)Math.floor(position.getX()), (int)Math.floor(position.getY()), (int)Math.floor(position.getZ()));
	}

	/**
	 * Constructor.
	 *
	 * @param identifierText the text returned by {@link #getIdentifierText()}.
	 * @throws IllegalArgumentException if the identifier text is malformed
	 */
	public SectionId(String identifierText) throws IllegalArgumentException {
		final String[] idTextSegments = StringUtils.split(identifierText, '_');
		if (idTextSegments.length != 3) {
			throw new IllegalArgumentException(identifierText);
		}
		try {
			x = Integer.parseInt(idTextSegments[0]);
			y = Integer.parseInt(idTextSegments[1]);
			z = Integer.parseInt(idTextSegments[2]);
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException(identifierText);
		}
	}

	/**
	 * Getter method for the x.
	 *
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Getter method for the y.
	 *
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * Getter method for the z.
	 *
	 * @return the z
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Converts this ID to a string that is unique among all IDs, i.e. that can be
	 * converted back to an ID equal to this one.
	 *
	 * @return the identifier text
	 */
	public String getIdentifierText() {
		StringBuilder builder = new StringBuilder();
		builder.append(x);
		builder.append('_');
		builder.append(y);
		builder.append('_');
		builder.append(z);
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SectionId) {
			SectionId other = (SectionId) obj;
			return (x == other.x && y == other.y && z == other.z);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(x).append(y).append(z).toHashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	/**
	 * Returns the ID of the neighboring section by stepping in the specified
	 * direction.
	 *
	 * @param direction the direction
	 * @return the neighbor's ID
	 */
	public SectionId getNeighbor(AxisAlignedDirection direction) {
		return new SectionId(x + direction.getSignX(), y + direction.getSignY(), z + direction.getSignZ());
	}

	public final void encode(ByteBuf buffer) {
		buffer.writeInt(getX());
		buffer.writeInt(getY());
		buffer.writeInt(getZ());
	}

	public static SectionId decode(ByteBuf buffer) {
		return new SectionId(buffer.readInt(), buffer.readInt(), buffer.readInt());
	}

}
