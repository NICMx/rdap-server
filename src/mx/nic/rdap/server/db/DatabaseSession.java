package mx.nic.rdap.server.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

/**
 * Just a container of the database connections pool.
 *
 * @author aleiva
 */
public class DatabaseSession {

	private static DataSource dataSource;

	public static void init() throws SQLException {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/rdapdb");
		} catch (NamingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public static void close() throws SQLException {
		// TODO this is likely not the right way to do this.
		// But DataSource lacks a close() method... WTF?
		// I really have no idea.
		// If you remove this, you will notice several memory leak warnings
		// whenever tomcat republishes the service.
		if (dataSource instanceof BasicDataSource) {
			((BasicDataSource) dataSource).close();
		}
	}

}
