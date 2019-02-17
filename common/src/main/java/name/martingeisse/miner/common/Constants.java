/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common;

import name.martingeisse.miner.common.geometry.ClusterSize;

/**
 * Common constants for client-side and server-side code.
 */
public class Constants {

	/**
	 * The network port used by the client to connect to the server.
	 */
	public static final int NETWORK_PORT = 7259;

	/**
	 * the BCRYPT_COST
	 */
	public static final int BCRYPT_COST = 12;

	/**
	 * the SECTION_SIZE
	 */
	public static final ClusterSize SECTION_SIZE = new ClusterSize(5);

	/**
	 * Whenever "detailed" coordinates are needed, fixed-point numbers
	 * are used that are represented using integers. Each cube, whose normal
	 * size is 1, is {@link #GEOMETRY_DETAIL_FACTOR} units wide in detail
	 * coordinates.
	 * <p>
	 * Detail coordinates are only used in exceptional cases, so whenever
	 * a method comment doesn't mention them, you can assume that the method
	 * uses normal coordinates (1 unit per cube). An example where detail
	 * coordinates are used is building the polygon meshes for "detailed"
	 * cube types. Using integer computations brings a surprising performance
	 * boost for them.
	 */
	public static final int GEOMETRY_DETAIL_FACTOR = 8;

	/**
	 * This value is the log2 of {@link #GEOMETRY_DETAIL_FACTOR} and can
	 * be used for bit shifting.
	 */
	public static final int GEOMETRY_DETAIL_SHIFT = 3;

	/**
	 * A {@link ClusterSize} for {@link #GEOMETRY_DETAIL_FACTOR}.
	 */
	public static final ClusterSize GEOMETRY_DETAIL_CLUSTER_SIZE = new ClusterSize(GEOMETRY_DETAIL_SHIFT);

}
