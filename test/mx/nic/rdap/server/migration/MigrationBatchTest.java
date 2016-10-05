package mx.nic.rdap.server.migration;

import java.sql.SQLException;

import org.junit.Test;

/**
 * @author L00000185
 *
 */
public class MigrationBatchTest {

	@Test
	public void test() {
		MigrationBatch batch = new MigrationBatch();
		try {
			batch.migrate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(0);

	}
}
