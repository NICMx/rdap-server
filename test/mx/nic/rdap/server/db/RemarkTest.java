package mx.nic.rdap.server.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.RemarkModel;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Test for the class Remark
 * 
 * @author dalpuche
 *
 */
public class RemarkTest {

	/** File from which we will load the database connection. */
	private static final String DATABASE_FILE = "database";

	@Test
	/**
	 * Test the insert of remarks and remark descriptions in the database
	 */
	public void insert() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));

			RemarkDAO remark = new RemarkDAO();
			Double testId = Math.random();
			remark.setTitle("Test " + testId);
			remark.setType("Test");

			RemarkDescriptionDAO description1 = new RemarkDescriptionDAO();
			description1.setDescription("First description of the remark " + testId);
			description1.setRemarkId(remark.getId());
			description1.setOrder(1);

			RemarkDescriptionDAO description2 = new RemarkDescriptionDAO();
			description2.setDescription("Second description of the remark" + testId);
			description2.setRemarkId(remark.getId());
			description2.setOrder(2);

			List<RemarkDescription> descriptions = new ArrayList<RemarkDescription>();
			descriptions.add(description1);
			descriptions.add(description2);
			remark.setDescriptions(descriptions);

			List<Link> links = new ArrayList<Link>();
			Link link = new LinkDAO();
			link.setHref("remarkLink1");
			link.setValue("remarkLink1.com");

			Link link2 = new LinkDAO();
			link2.setValue("remarkLink2.com");
			link2.setHref("remarkLink2");

			links.add(link);
			links.add(link2);

			remark.setLinks(links);
			try (Connection connection = DatabaseSession.getConnection()) {
				RemarkModel.storeToDatabase(remark, connection);
				System.out.println(remark);
			}
			assert true;
		} catch (RequiredValueNotFoundException | SQLException | IOException e) {
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

	@Test
	/**
	 * Test the insert of nameserver's remarks
	 */
	public void insertNameserverRemarks() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));

			RemarkDAO remark = new RemarkDAO();
			Double testId = Math.random();
			remark.setTitle("Nameserver remarks test" + testId);
			remark.setType("Nameserver remark");
			RemarkDescriptionDAO description1 = new RemarkDescriptionDAO();
			description1.setDescription("First description of the ns remark " + testId);
			description1.setRemarkId(remark.getId());
			description1.setOrder(1);
			RemarkDescriptionDAO description2 = new RemarkDescriptionDAO();
			description2.setDescription("Second description of the ns remark" + testId);
			description2.setRemarkId(remark.getId());
			description2.setOrder(2);
			List<RemarkDescription> descriptions = new ArrayList<RemarkDescription>();
			descriptions.add(description1);
			descriptions.add(description2);
			remark.setDescriptions(descriptions);
			List<Remark> remarks = new ArrayList<Remark>();
			remarks.add(remark);
			try (Connection connection = DatabaseSession.getConnection()) {
				RemarkModel.storeNameserverRemarksToDatabase(remarks, 1L, connection);
			}
			assert true;
		} catch (RequiredValueNotFoundException | SQLException | IOException e) {
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

	@Test
	public void getAll() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			try (Connection connection = DatabaseSession.getConnection()) {
				RemarkModel.getAll(connection);
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

}
