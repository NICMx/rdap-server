/**
 * 
 */
package mx.nic.rdap.server.db;

import org.junit.Test;

import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.ZoneModel;

/**
 * Test for the class Zone
 * @author evaldes
 *
 */
public class ZoneTest {
	/**
	 * File from which we will load the database connection.
	 */
	private static final String DATABASE_FILE = "database";
	
	@Test
	/**
	 * 
	 */
	public void insert(){
		//TODO 
		try{
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			ZoneDAO zone = new ZoneDAO();
			zone.setId(1);
			zone.setZoneName(".test.mx");
			ZoneModel.storeToDatabase(zone);
		}
		catch (Exception e){
			//TODO
		}
	}
	
	public void getZoneName(){
		//TODO Needs domain model
	}
}
