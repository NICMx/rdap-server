package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.VariantNameModel;

public class VariantNameTest {
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
	 * Creates a list of VariantName and stores it in the database, the it gets
	 * the same instance from the database and compares to see if they still
	 * match.
	 */

	@Test
	public void insertAndGetVariantName() {
		// Random random = new Random();
		Long variantId = 6L;
		// random.nextLong();

		List<VariantName> variantNames = new ArrayList<VariantName>();
		VariantName variantName1 = createVariantName("xn--fo-8ja.example" + variantId, variantId);
		VariantName variantName2 = createVariantName("xn--fo-9ja.example" + variantId, variantId);

		variantNames.add(variantName1);
		variantNames.add(variantName2);

		System.out.println("" + variantNames.get(0).getLdhName());
		System.out.println("" + variantNames.get(1).getLdhName());
		try {
			VariantNameModel.storeAllToDatabase(variantNames, variantId, connection);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		List<VariantName> byVariantId = new ArrayList<VariantName>();// TODO
																		// equals
		try {
			byVariantId = VariantNameModel.getByVariantId(variantId, connection);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Creates instance and sets all parameters.
	 * 
	 * @param ldhName
	 * @param variantId
	 * @return
	 */
	public static VariantNameDAO createVariantName(String ldhName, Long variantId) {
		VariantNameDAO variantName = new VariantNameDAO();
		variantName.setLdhName(ldhName);
		variantName.setVariantId(variantId);
		return variantName;
	}

}
