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

import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.VariantRelationModel;
import mx.nix.rdap.core.catalog.VariantRelation;

public class VariantRelationTest {
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

	/**
	 * Stores a list of variant´s relations
	 */
	@Test
	public void storeByVariantId() {
		Long variantId = 61L;
		List<VariantRelation> relations = new ArrayList<VariantRelation>();

		VariantRelation relation1 = VariantRelation.REGISTERED;
		VariantRelation relation2 = VariantRelation.CONJOINED;

		relations.add(relation1);
		relations.add(relation2);

		try {
			VariantRelationModel.storeVariantRelations(relations, variantId, connection);
			// TODO Ignore if repeated?
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Gets a list of relation based on the variant´s id
	 */
	@Test
	public void getByVariantId() {
		Long variantId = 61L;

		try {
			VariantRelationModel.getByVariantId(variantId, connection);
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
