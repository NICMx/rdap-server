package mx.nic.rdap.server.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.RemarkModel;

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
			List<mx.nic.rdap.core.db.RemarkDescription> descriptions = new ArrayList<mx.nic.rdap.core.db.RemarkDescription>();
			descriptions.add(description1);
			descriptions.add(description2);
			remark.setDescriptions(descriptions);
			RemarkModel.storeToDatabase(remark);
			assert true;
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			assert false;
			e.printStackTrace();
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
			assert !RemarkModel.getAll().isEmpty();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
