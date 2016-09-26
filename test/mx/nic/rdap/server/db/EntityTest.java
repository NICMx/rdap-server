package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Registrar;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.EntityModel;
import mx.nic.rdap.server.db.model.RegistrarModel;

/**
 * Tests for the {@link EntityModel}
 * 
 * @author dhfelix
 *
 */
public class EntityTest {

	/** File from which we will load the database connection. */
	private static final String DATABASE_FILE = "database";

	/**
	 * Connection for this tests
	 */
	private static Connection connection = null;

	/**
	 * To see if autoCommit is set in the connection.
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

	/**
	 * Creates a new instance and stores it in the database, then get an
	 * instance with the id generated and compares it to see if they match.
	 */
	@Test
	public void insertAndGetEntity() {
		// Entity base data
		Random random = new Random();
		int randomInt = random.nextInt();

		// Create local instances
		Entity entity = createEntity(null, "ent_dhfelix" + randomInt, null, null, null);
		Registrar registrar = RegistrarTest.createRegistrar(null, "rar_dhrar" + randomInt,
				"www.dhrar" + randomInt + ".com.mx");
		Long registrarId = null;
		try {
			registrarId = RegistrarModel.storeToDatabase(registrar, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		entity.setRarId(registrarId);
		entity.setRegistrar(registrar);

		VCard vCard = VCardTest.createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		List<VCardPostalInfo> postalInfoList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoList.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX",
					"monterrey", "Luis Elizondo", null, null, "NL", "66666"));
		}
		vCard.setPostalInfo(postalInfoList);
		entity.setVCard(vCard);

		// Store it in the database
		Long entId = null;
		try {
			entId = EntityModel.storeToDatabase(entity, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Query the database
		Entity byId = null;
		try {
			byId = EntityModel.getById(entId, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		Entity byHandle = null;
		try {
			byHandle = EntityModel.getByHandle(entity.getHandle(), connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Compares the results
		Assert.assertTrue("getById fails", entity.equals(byId));
		Assert.assertTrue("getByHandle fails", entity.equals(byHandle));

	}

	/**
	 * Create a new instance, and set the incoming parameters. (Does not store
	 * the instance in the Database).
	 * 
	 * @param id
	 *            The id of the entity
	 * @param handle
	 *            roid of the entity
	 * @param port43
	 * @param rarId
	 * @param vCardId
	 * @return
	 */
	public static EntityDAO createEntity(Long id, String handle, String port43, Long rarId, Long vCardId) {
		EntityDAO e = new EntityDAO();
		e.setId(id);
		e.setHandle(handle);
		e.setPort43(port43);
		e.setRarId(rarId);
		e.setVCardId(vCardId);
		return e;
	}

}
