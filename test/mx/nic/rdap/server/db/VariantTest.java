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
import org.junit.Assert;
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
	public void insertAndGetSimpleVariant() {
		Variant variant = new VariantDAO();

		List<VariantRelation> relations = variant.getRelations();
		List<VariantName> names = variant.getVariantNames();

		relations.add(VariantRelation.REGISTERED);
		relations.add(VariantRelation.CONJOINED);

		names.add(createVariantName("xn--fo-cka.example"));
		names.add(createVariantName("xn--fo-fka.example"));

		variant.setDomainId(3L);

		Long variantId = null;
		try {
			variantId = VariantModel.storeToDatabase(variant, connection);
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			fail();
		}

		Variant byId = null;
		try {
			byId = VariantModel.getById(variantId, connection);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail();
		}

		Assert.assertTrue("getById Fails", variant.equals(byId));

	}

	@Test
	public void insertAndGetByDomainId() {
		Long domainId = 3L;

		// Generates array of new variants with relations and variant names
		List<Variant> variants = new ArrayList<Variant>();

		List<VariantRelation> relations1 = new ArrayList<VariantRelation>();
		relations1.add(VariantRelation.REGISTERED);
		relations1.add(VariantRelation.CONJOINED);
		List<VariantName> variantNames1 = new ArrayList<VariantName>();
		variantNames1.add(createVariantName("xn--fo-cka.example"));
		variantNames1.add(createVariantName("xn--fo-fka.example"));

		List<VariantRelation> relations2 = new ArrayList<VariantRelation>();
		relations2.add(VariantRelation.UNREGISTERED);
		relations2.add(VariantRelation.REGISTRATION_RESTRICTED);
		List<VariantName> variantNames2 = new ArrayList<VariantName>();
		variantNames2.add(createVariantName("xn--fo-8ja.example"));

		variants.add(createVariant(null, relations1, variantNames1, domainId, null));
		variants.add(createVariant(null, relations2, variantNames2, domainId, ".EXAMPLE Spanish"));

		// Stores variants to database
		try {
			VariantModel.storeAllToDatabase(variants, domainId, connection);
			// TODO check why variantId is not auto-incremental
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Retrieving stored variants
		List<Variant> byDomId = null;
		try {
			byDomId = VariantModel.getByDomainId(domainId, connection);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		Assert.assertTrue("getByDomainId Fails", variants.size() == byDomId.size() && variants.containsAll(byDomId));
	}

	public static VariantDAO createVariant(Long id, List<VariantRelation> relations, List<VariantName> variantNames,
			Long domainId, String idnTable) {
		VariantDAO variant = new VariantDAO();
		variant.setId(id);
		variant.setIdnTable(idnTable);
		variant.getRelations().addAll(relations);
		variant.getVariantNames().addAll(variantNames);
		variant.setDomainId(domainId);
		return variant;
	}

	public static VariantName createVariantName(String punycode) {
		VariantName variantName = new VariantName();
		variantName.setLdhName(punycode);
		return variantName;
	}

}
