package mx.nic.rdap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

public class RendererPool {

	private static HashMap<String, Renderer> renderers = new HashMap<>();

	public static void loadRenderers(String file)
			throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		InputStream handlerStream = RdapServlet.class.getClassLoader().getResourceAsStream(file);
		if (handlerStream == null) {
			throw new IOException("File '" + file + "' not found.");
		}

		Properties properties = new Properties();
		properties.load(handlerStream);

		String rendererNames[] = properties.getProperty("renderers").split("\\s*,\\s*");
		for (String name : rendererNames) {
			Class<?> clazz = Class.forName(properties.getProperty(name + ".class"));
			Constructor<?> constructor = clazz.getConstructor();
			Renderer renderer = (Renderer) constructor.newInstance();
			for (String contentType : renderer.getRequestContentTypes()) {
				renderers.put(contentType, renderer);
			}
		}
	}

	public static Renderer get(String name) {
		return renderers.get(name);
	}

}
