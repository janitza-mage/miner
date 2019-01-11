/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import name.martingeisse.miner.server.util.database.postgres.PostgresService;

/**
 * Static access to all database descriptors.
 */
public class Databases {

	/**
	 * Prevent instantiation.
	 */
	private Databases() {
	}

	/**
	 * The main database.
	 */
	public static PostgresService main;

	/**
	 * The Cassandra-based database cluster (not usually used).
	 */
	public static Cluster cassandraCluster;

	/**
	 * The Cassandra database session for the world DB.
	 */
	public static Session world;

}
