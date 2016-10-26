package mx.nic.rdap.server.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseSession {

	private static final String RDAP_DB = "jdbc/rdap";
	private static final String MIGRATION_DB = "jdbc/migration";

	private static DataSource getEnvironmentDataSource(String name) {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			return (DataSource) envContext.lookup(name);
		} catch (NamingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Connection getRdapConnection() throws SQLException {
		return getEnvironmentDataSource(RDAP_DB).getConnection();
	}
	
	public static Connection getMigrationConnection() throws SQLException {
		return getEnvironmentDataSource(MIGRATION_DB).getConnection();
	}

}
