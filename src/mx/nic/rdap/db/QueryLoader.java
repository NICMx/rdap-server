package mx.nic.rdap.db;

import java.io.IOException;
import java.util.HashMap;

/**
 * Loads {@link QueryGroup}s into memory and caches them.
 *
 * @author aleiva
 */
public class QueryLoader {

	/** {@link QueryGroup} cache. */
	private static HashMap<String, QueryGroup> groups = new HashMap<>();

	/**
	 * Returns the {@link QueryGroup} that corresponds to resource type
	 * <code>groupName</code>.
	 * 
	 * @param groupName
	 *            resource type of the queries needed.
	 * @return the {@link QueryGroup} that corresponds to resource type
	 *         <code>groupName</code>.
	 * @throws IOException
	 *             Problem reading the resource type's SQL query file.
	 */
	public static synchronized QueryGroup getQueryGroup(String groupName) throws IOException {
		QueryGroup result = groups.get(groupName);
		if (result == null) {
			result = new QueryGroup(groupName);
			groups.put(groupName, result);
		}
		return result;
	}

}
