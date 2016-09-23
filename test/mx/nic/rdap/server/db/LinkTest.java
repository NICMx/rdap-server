package mx.nic.rdap.server.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

	/**
	 * Store nameserver's links
	 */
	@Test
	public void insertNameserverLinks() {
		try {

			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			Link link = new LinkDAO();
			link.setValue("spotify2.com");
			List<Link> links = new ArrayList<Link>();
			links.add(link);
			try (Connection connection = DatabaseSession.getConnection()) {
				LinkModel.storeNameserverLinksToDatabase(links, 6L, connection);
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
	/**
	 * Test that retrieve an array of links from a Nameserver id
	 */
	public void getByNameserverId() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			List<Link> links = new ArrayList<Link>();
			try (Connection connection = DatabaseSession.getConnection()) {
				links = LinkModel.getByNameServerId(6L, connection);
				for (Link link : links) {
					System.out.println(((LinkDAO) link));
				}

			}
			assert true;
		} catch (SQLException | IOException e) {
			assert false;
			e.printStackTrace();
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}
