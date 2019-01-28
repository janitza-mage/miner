package name.martingeisse.miner.server.util.database.postgres;

import name.martingeisse.miner.common.util.UnexpectedExceptionException;
import name.martingeisse.miner.server.util.database.DatabaseService;
import org.postgresql.ds.PGPoolingDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 */
@Singleton
public class PostgresService implements DatabaseService/*, AnnotatedConfigurationParticipant*/ {

	private final PGPoolingDataSource dataSource;

	/**
	 * Constructor.
	 */
	@Inject
	public PostgresService() {
		dataSource = new PGPoolingDataSource();
		dataSource.setDataSourceName("Companion PostgreSQL Database");
		dataSource.setMaxConnections(100);
	}

	// @ConfigurationSetting(name = "postgresHost")
	public void setPostgresHost(String value) {
		dataSource.setServerName(value);
	}

	// @ConfigurationSetting(name = "postgresDatabase")
	public void setPostgresDatabaseName(String value) {
		dataSource.setDatabaseName(value);
	}

	// @ConfigurationSetting(name = "postgresUser")
	public void setPostgresUser(String value) {
		dataSource.setUser(value);
	}

	// @ConfigurationSetting(name = "postgresPassword")
	public void setPostgresPassword(String value) {
		dataSource.setPassword(value);
	}

	// override
	@Override
	public Connection newJdbcConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new UnexpectedExceptionException(e);
		}
	}

	// override
	@Override
	public PostgresConnection newConnection() {
		return new PostgresConnection(newJdbcConnection(), MyPostgresConfiguration.CONFIGURATION);
	}

}
