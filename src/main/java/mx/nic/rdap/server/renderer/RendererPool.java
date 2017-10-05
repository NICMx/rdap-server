package mx.nic.rdap.server.renderer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

import mx.nic.rdap.renderer.Renderer;

/**
 * The singleton that keeps track of the loaded {@link Renderer}s.
 */
public class RendererPool {

	/** The renderers. */
	private static HashMap<String, Renderer> renderers = new HashMap<>();

	/**
	 * Default renderer property name.
	 */
	private static final String DEFAULT_RENDERER_CLASS_KEY = "default";

	/**
	 * Default Renderer instance.
	 */
	private static Renderer defaultRenderer;

	/**
	 * Loads and stores the renderers described in <code>properties</code>.
	 *
	 * @param file
	 *            configuration that describes the renderers we're supposed to load.
	 */
	public static void loadRenderers(Properties properties)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String defaultRendererClass = properties.getProperty(DEFAULT_RENDERER_CLASS_KEY);
		setDefaultRenderer(defaultRendererClass);

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

	private static void setDefaultRenderer(String defaultRendererClassName)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (defaultRendererClassName == null || defaultRendererClassName.isEmpty()) {
			throw new IllegalArgumentException("No 'default' renderer value was provided in 'renderers.properties'.");
		}

		defaultRendererClassName = defaultRendererClassName.trim();
		Class<?> clazz = Class.forName(defaultRendererClassName);
		Constructor<?> constructor = clazz.getConstructor();
		defaultRenderer = (Renderer) constructor.newInstance();
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

	/**
	 * @return The default renderer for the server
	 */
	public static Renderer getDefaultRenderer() {
		return defaultRenderer;
	}

}
