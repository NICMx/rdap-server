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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.VariantModel;
import mx.nix.rdap.core.catalog.VariantRelation;

public class VariantTest {

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

	@Test
	public void insertAndGetByDomainId() {

		Random random = new Random();
		Long domainId = 3L;

		Long variantId1 = random.nextLong();
		Long variantId2 = random.nextLong();

		// Generates array of new variants with relations and variant names
		List<Variant> variants = new ArrayList<Variant>();
		List<VariantRelation> relations = new ArrayList<VariantRelation>();

		VariantRelation relation = VariantRelation.REGISTERED;
		relations.add(relation);

		List<VariantName> variantNames1 = new ArrayList<VariantName>();
		VariantName variantName1 = VariantNameTest.createVariantName("xn--fo-8ja.example" + variantId1, null);
		variantNames1.add(variantName1);
		List<VariantName> variantNames2 = new ArrayList<VariantName>();
		VariantName variantName2 = VariantNameTest.createVariantName("xn--fo-9ja.example" + variantId2, null);
		variantNames2.add(variantName2);

		Variant variant1 = createVariant(null, relations, variantNames1, 1L);
		Variant variant2 = createVariant(null, relations, variantNames2, 1L);
		variants.add(variant1);
		variants.add(variant2);
		System.out.println("" + variants.get(0).getId());
		System.out.println("" + variants.get(1).getId());

		// Stores variants to database
		try {
			VariantModel.storeAllToDatabase(variants, domainId, connection);
			// TODO check why variantId is not auto-incremental
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Retrieving stored variants
		List<Variant> byDomId = new ArrayList<Variant>();
		System.out.println("" + domainId);
		try {
			byDomId = VariantModel.getByDomainId(domainId, connection);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// TODO equals
	}

	public static VariantDAO createVariant(Long id, List<VariantRelation> relations, List<VariantName> variantNames,
			Long domainId) {
		VariantDAO variant = new VariantDAO();
		variant.setId(id);
		variant.setIdnTable(".test  Spanish");
		variant.setRelations(relations);
		variant.setVariantNames(variantNames);
		variant.setDomainId(domainId);
		return variant;
	}

}
