package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.LinkModel;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Test for the class link
 * 
 * @author dalpuche
 *
 */
public class LinkTest {

	/** File from which we will load the database connection. */
	private static final String DATABASE_FILE = "database";

	@Test
	/**
	 * Store a link in the database
	 */
	public void insert() {
		try {

			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			Link link = new LinkDAO();
			link.setValue("spotify.com");
			link.setHref("test");
			try (Connection connection = DatabaseSession.getConnection()) {
				LinkModel.storeToDatabase(link, connection);
			}
			assert true;
		} catch (RequiredValueNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void getAll() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			try (Connection connection = DatabaseSession.getConnection()) {
				List<Link> links = LinkModel.getAll(connection);
				for (Link link : links) {
					System.out.println(((LinkDAO) link).toJson());
				}
				assert true;
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				e.printStackTrace();
				fail();
			}
		}

	}
}
