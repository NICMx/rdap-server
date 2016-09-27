/**
 * 
 */
package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mx.nic.rdap.core.db.Zone;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.ZoneModel;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Tests for the {@link ZoneModel}
 * 
 * @author evaldes
 *
 */

public class ZoneTest {
	/**
	 * File from which we will load the database connection.
	 */
	private static final String DATABASE_FILE = "database";

	/**
	 * Connection for these tests
	 */
	private static Connection connection = null;

	/**
	 * To verify autoCommit is in the connection
	 */
	private static boolean autoCommit = false;

	@BeforeClass
	public static void init() {
		try {
			Properties properties = Util.loadProperties(DATABASE_FILE);
			autoCommit = Boolean.parseBoolean(properties.getProperty("autoCommit"));
			DatabaseSession.init(properties);
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Before
	public void before() {
		try {
			connection = DatabaseSession.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@After
	public void after() {
		try {
			if (!autoCommit)
				connection.rollback();
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterClass
	public static void end() {
		try {
			DatabaseSession.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	/**
	 * Creates a new Zone instance and stores it in the database, then it get an
	 * instance with the id generated
	 */
	public void insertAndGetBy() throws IOException, SQLException, RequiredValueNotFoundException {
		Random random = new Random();
		int randomInt = random.nextInt();

		Zone zone = createZone(null, "example" + randomInt + ".mx");
		Integer zoneId;
		System.out.println(zone.getZoneName() + zone.getId());
		try {
			zoneId = ZoneModel.storeToDatabase(zone, connection);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		System.out.println("" + zoneId);
		Zone byId = new Zone();

		try {
			byId = ZoneModel.getByZoneId(zoneId, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		System.out.println(byId.getZoneName() + " " + byId.getId());
		Assert.assertTrue("Get by Id fails", zone.equals(byId));
	}

	@Test
	public void getAll() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			try (Connection connection = DatabaseSession.getConnection()) {
				ZoneModel.getAll(connection);
				assert true;
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public ZoneDAO createZone(Integer id, String zoneName) {
		ZoneDAO zone = new ZoneDAO();
		zone.setId(id);
		zone.setZoneName(zoneName);
		return zone;
	}

}