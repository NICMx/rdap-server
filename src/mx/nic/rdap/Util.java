package mx.nic.rdap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Random miscellaneous functions useful anywhere.
 *
 * @author aleiva
 */
public class Util {

	/**
	 * Loads the properties configuration file <code>META-INF/fileName</code>
	 * and returns it.
	 * 
	 * @param fileName
	 *            name of the configuration file you want to load.
	 * @return configuration requested.
	 * @throws IOException
	 *             Error attempting to read the configuration out of the
	 *             classpath.
	 */
	public static Properties loadProperties(String fileName) throws IOException {
		Properties result = new Properties();
		InputStream configStream = DatabaseSession.class.getClassLoader().getResourceAsStream("META-INF/" + fileName);
		try {
			result.load(configStream);
		} finally {
			configStream.close();
		}
		return result;
	}

}
