package mx.nic.rdap.server.renderer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

/**
 * The singleton that keeps track of the loaded {@link Renderer}s.
 */
public class RendererPool {

	/** The renderers. */
	private static HashMap<String, Renderer> renderers = new HashMap<>();

	/**
	 * Loads and stores the renderers described in <code>properties</code>.
	 *
	 * @param file
	 *            configuration that describes the renderers we're supposed to
	 *            load.
	 */
	public static void loadRenderers(Properties properties)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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

	/**
	 * Returns the renderer that handles MIME <code>mime</code>.
	 * 
	 * @param resourceType
	 *            A content type the user accepts. (Example: "text/plain",
	 *            "application/xml", etc.)
	 * @return the renderer that handles MIME <code>mime</code>.
	 */
	public static Renderer get(String mime) {
		return renderers.get(mime);
	}

}
