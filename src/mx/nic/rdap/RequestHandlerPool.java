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

		String handlerNames[] = properties.getProperty("handlers").split("\\s*,\\s*");
		for (String name : handlerNames) {
			Class<?> clazz = Class.forName(properties.getProperty(name + ".class"));
			Constructor<?> constructor = clazz.getConstructor();
			RdapRequestHandler handler = (RdapRequestHandler) constructor.newInstance();
			handlers.put(handler.getResourceType(), handler);
		}
	}

	public static RdapRequestHandler get(String resourceType) {
		return handlers.get(resourceType);
	}

}
