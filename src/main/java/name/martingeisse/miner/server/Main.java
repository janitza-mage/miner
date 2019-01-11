/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server;

import com.datastax.driver.core.Cluster;
import name.martingeisse.api.handler.DefaultMasterHandler;
import name.martingeisse.api.handler.misc.NotFoundHandler;
import name.martingeisse.api.request.ApiRequestCycle;
import name.martingeisse.api.servlet.ApiConfiguration;
import name.martingeisse.api.servlet.ApiLauncher;
import name.martingeisse.common.javascript.JavascriptAssembler;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.task.TaskSystem;
import name.martingeisse.miner.server.api.account.AccountApiHandler;
import name.martingeisse.miner.server.network.StackdNettyPipelineFactory;
import name.martingeisse.miner.server.util.database.postgres.PostgresService;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * The main class for the game server.
 */
public class Main {

	/**
	 * The main method.
	 *
	 * @param args command-line arguments (ignored)
	 * @throws Exception on errors
	 */
	public static void main(final String[] args) throws Exception {

		// core initialization
		parseCommandLine(args);
		initializeBase();

		// game server
		new Thread() {
			@Override
			public void run() {
				MinerServer minerServer = new MinerServer();
				final ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
				final ServerBootstrap bootstrap = new ServerBootstrap(factory);
				bootstrap.setPipelineFactory(new StackdNettyPipelineFactory<MinerSession>(minerServer));
				bootstrap.setOption("child.tcpNoDelay", true);
				bootstrap.setOption("child.keepAlive", true);
				bootstrap.bind(new InetSocketAddress(Constants.NETWORK_PORT));
			}
		}.start();

		// account API
		new Thread() {
			@Override
			public void run() {
				try {
					ApiRequestCycle.setUseSessions(false);
					DefaultMasterHandler masterHandler = new DefaultMasterHandler();
					masterHandler.setApplicationRequestHandler(new AccountApiHandler());
					masterHandler.getInterceptHandlers().put("/favicon.ico", new NotFoundHandler(false));
					ApiConfiguration configuration = new ApiConfiguration();
					configuration.setMasterRequestHandler(masterHandler);
					configuration.getLocalizationConfiguration().setGlobalFallback(Locale.US);
					ApiLauncher.launch(configuration);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}.start();

	}

	/**
	 * Parses the command line and sets static variables in this class.
	 */
	private static void parseCommandLine(final String[] args) throws IOException {
		if (args.length == 0) {
			Configuration.initializeFromClasspathConfig();
		} else if (args.length == 1) {
			Configuration.initializeFromConfigFile(new File(args[0]));
		} else {
			System.err.println("usage: Main [config.properties]");
			System.exit(1);
		}
	}

	/**
	 * Initializes URL handlers, time zones and the database.
	 * @throws IOException on I/O errors
	 */
	private static void initializeBase() throws IOException {

		// initialize time zone
		final DateTimeZone timeZone = DateTimeZone.UTC;
		JavascriptAssembler.defaultDateFormatter = DateTimeFormat.forPattern("YYYY-MM-dd").withZone(timeZone);
		JavascriptAssembler.defaultDateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss").withZone(timeZone);

		// initialize task system
		TaskSystem.initialize();

		// initialize SQL database
		Databases.main = new PostgresService();
		Databases.main.setPostgresHost("localhost");
		Databases.main.setPostgresDatabaseName("miner");
		Databases.main.setPostgresUser("postgres");
		Databases.main.setPostgresPassword("postgres");

		// initialize Cassandra database
		Databases.cassandraCluster = Cluster.builder().addContactPoint("localhost").build();
		Databases.world = Databases.cassandraCluster.connect("miner");

	}

}
