package mx.nic.rdap.server.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseSession {

	public static final String RDAP_DB = "rdap";

	private static DataSource getEnvironmentDataSource(String name) {
		try {
			Context initContext = new InitialContext();
			return (DataSource) initContext.lookup("java:/comp/env/jdbc/" + name);
		} catch (NamingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Connection getRdapConnection() throws SQLException {
		return getEnvironmentDataSource(RDAP_DB).getConnection();
	}

}
