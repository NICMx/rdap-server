package mx.nic.rdap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

public class RequestHandlerPool {

	private static HashMap<String, RdapRequestHandler> handlers = new HashMap<>();

	public static void loadHandlers(String file)
			throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		InputStream handlerStream = RdapServlet.class.getClassLoader().getResourceAsStream(file);
		if (handlerStream == null) {
			throw new IOException("File '" + file + "' not found.");
		}

		Properties properties = new Properties();
		properties.load(handlerStream);

		String handlers[] = properties.getProperty("handlers").split("\\s*,\\s*");
		for (String handler : handlers) {
			Class<?> clazz = Class.forName(properties.getProperty(handler + ".class"));
			Constructor<?> constructor = clazz.getConstructor();
			Object handlerObject = constructor.newInstance();
			RequestHandlerPool.add((RdapRequestHandler) handlerObject);
		}
	}

	private static synchronized void add(RdapRequestHandler handler) {
		handlers.put(handler.getResourceType(), handler);
	}

	public static synchronized RdapRequestHandler get(String resourceType) {
		return handlers.get(resourceType);
	}

}
