package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.server.db.model.PublicIdModel;

public class PublicIdTest extends DatabaseTest {

	/**
	 * Connection for this tests
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
	public void insertAndGetByDomain() {
		Long domainId = 3L;

		Random random = new Random();
		Long rndPublicId = random.nextLong();
		List<PublicId> publicIds = new ArrayList<PublicId>();
		PublicIdDAO publicId = createPublicId("dummy" + rndPublicId, "dummy IETF");
		publicIds.add(publicId);
		try {
			PublicIdModel.storePublicIdByDomain(publicIds, domainId, connection);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		List<PublicId> byDomainId = new ArrayList<PublicId>();
		try {
			byDomainId = PublicIdModel.getByDomain(domainId, connection);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		publicId.equals(byDomainId.get(byDomainId.size() - 1));
	}

	public static PublicIdDAO createPublicId(String publicId, String type) {
		PublicIdDAO pi = new PublicIdDAO();
		pi.setPublicId(publicId);
		pi.setType(type);
		return pi;
	}

}
