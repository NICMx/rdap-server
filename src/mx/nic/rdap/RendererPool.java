package mx.nic.rdap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class RendererPool {

	private static Renderer renderer;

	public static void loadRenderers(String file)
			throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		InputStream handlerStream = RdapServlet.class.getClassLoader().getResourceAsStream(file);
		if (handlerStream == null) {
			throw new IOException("File '" + file + "' not found.");
		}
		
		Properties properties = new Properties();
		properties.load(handlerStream);

		Class<?> clazz = Class.forName(properties.getProperty("renderer"));
		Constructor<?> constructor = clazz.getConstructor();
		Object handlerObject = constructor.newInstance();
		renderer = (Renderer) handlerObject;
	}

	public static Renderer getActiveRenderer() {
		return renderer;
	}

}
