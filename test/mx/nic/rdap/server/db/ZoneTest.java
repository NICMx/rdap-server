/**
 * 
 */
package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mx.nic.rdap.server.db.model.ZoneModel;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Tests for the {@link ZoneModel}
 * 
 * @author evaldes
 *
 */
public class ZoneTest extends DatabaseTest {

	/**
	 * Connection for these tests
	 */
	private static Connection connection = null;

	@Before
	public void before() throws SQLException {
		connection = DatabaseSession.getRdapConnection();
	}

	@After
	public void after() throws SQLException {
		connection.rollback();
		connection.close();
	}

	@Test
	/**
	 * Creates a new Zone instance and stores it in the database, then it get an
	 * instance with the id generated
	 */
	public void insertAndGetBy() throws IOException, SQLException, RequiredValueNotFoundException {
		Random random = new Random();
		int randomInt = random.nextInt();

		String zoneName = "example" + randomInt + ".mx";
		Integer zoneId = null;

		try {
			zoneId = ZoneModel.storeToDatabase(zoneName, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		String byId = ZoneModel.getZoneNameById(zoneId);

		Assert.assertTrue("Get by Id fails", zoneName.equals(byId));
	}

}