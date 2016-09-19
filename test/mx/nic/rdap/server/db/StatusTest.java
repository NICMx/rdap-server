package mx.nic.rdap.server.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.StatusModel;
import mx.nix.rdap.core.catalog.Status;

/**
 * Test for the class Status
 * 
 * @author dalpuche
 *
 */
public class StatusTest {

	/** File from which we will load the database connection. */
	private static final String DATABASE_FILE = "database";

	@Test
	/**
	 * Test the store of a nameserver status in the database
	 */
	public void storeNameserverStatusToDatabase() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			Status status = Status.ACTIVE;
			Status status2 = Status.INACTIVE;
			List<Status> statusList=new ArrayList<Status>();
			statusList.add(status);
			statusList.add(status2);
			StatusModel.storeNameserverStatusToDatabase(statusList, 1l);
			assert true;
		} catch (IOException | SQLException e) {
			assert false;
			e.printStackTrace();
		}

	}

	@Test
	/**
	 * Test that retrieve an array of Status from a Nameserver id
	 */
	public void getByNameserverId() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			List<Status> status = StatusModel.getByNameServerId(1L);
			assert !status.isEmpty();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
