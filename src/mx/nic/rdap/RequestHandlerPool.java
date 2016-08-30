package mx.nic.rdap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

/**
 * The singleton that keeps track of the loaded {@link RdapRequestHandler}s.
 *
 * @author aleiva
 */
public class RequestHandlerPool {

	/** The request handlers. */
	private static HashMap<String, RdapRequestHandler> handlers = new HashMap<>();

	/**
	 * Loads and stores the handlers described in <code>properties</code>.
	 *
	 * @param file
	 *            configuration that describes the handlers we're supposed to
	 *            load.
	 */
	public static void loadHandlers(Properties properties)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String handlerNames[] = properties.getProperty("handlers").split("\\s*,\\s*");
		for (String name : handlerNames) {
			Class<?> clazz = Class.forName(properties.getProperty(name + ".class"));
			Constructor<?> constructor = clazz.getConstructor();
			RdapRequestHandler handler = (RdapRequestHandler) constructor.newInstance();
			handlers.put(handler.getResourceType(), handler);
		}
	}

	/**
	 * Returns the handler that responds to resources whose type is
	 * <code>resourceType</code>.
	 * 
	 * @param resourceType
	 *            if the request was /rdap/ip/192.2.0.2/24, the resource type is
	 *            "ip".
	 * @return the handler that responds to resources whose type is
	 *         <code>resourceType</code>.
	 */
	public static RdapRequestHandler get(String resourceType) {
		return handlers.get(resourceType);
	}

}
