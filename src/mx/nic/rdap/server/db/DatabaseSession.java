package mx.nic.rdap.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

/**
 * Just a container of the database connections pool.
 *
 * @author aleiva
 */
public class DatabaseSession {

	private static BasicDataSource ds;

	public static void init(Properties config) throws SQLException {
		ds = new BasicDataSource();
		ds.setDriverClassName(config.getProperty("driverClassName"));
		ds.setUrl(config.getProperty("url"));
		ds.setUsername(config.getProperty("userName"));
		ds.setPassword(config.getProperty("password"));
		ds.setDefaultAutoCommit(Boolean.parseBoolean(config.getProperty("autoCommit")));

		testDatabase();
	}

	private static void testDatabase() throws SQLException {
		// http://stackoverflow.com/questions/3668506
		final String TEST_QUERY = "select 1";
		try(Connection connection = getConnection();Statement statement = connection.createStatement();){
		ResultSet resultSet = statement.executeQuery(TEST_QUERY);

		if (!resultSet.next()) {
			throw new SQLException("'" + TEST_QUERY + "' returned no rows.");
		}
		int result = resultSet.getInt(1);
		if (result != 1) {
			throw new SQLException("'" + TEST_QUERY + "' returned " + result);
		}}
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	public static void close() throws SQLException{
		ds.close();
	}
}
