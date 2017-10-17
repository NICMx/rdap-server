package mx.nic.rdap.server.renderer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import mx.nic.rdap.renderer.Renderer;

/**
 * The singleton that keeps track of the loaded {@link Renderer}s.
 */
public class RendererPool {

	/** The renderers. */
	private static HashMap<String, RendererWrapper> renderers = new HashMap<>();

	/**
	 * Default renderer property name.
	 */
	private static final String DEFAULT_RENDERER_NAME_KEY = "default_renderer";

	/**
	 * Default Renderer instance.
	 */
	private static RendererWrapper defaultRenderer;

	private final static String SPLIT_PATTERN = "\\s*,\\s*";
	private static Pattern PATTERN = Pattern.compile(SPLIT_PATTERN);

	private final static String MIME_TYPE_REGEX = "([^\\s\\/]+)(\\/)[^\\s]([^\\/]+)";
	// XXX: Search a better way to validate the MIME type.
	private static Pattern mimeValidationPattern = Pattern.compile(MIME_TYPE_REGEX);

	/**
	 * Loads and stores the renderers described in <code>properties</code>.
	 *
	 * @param file
	 *            configuration that describes the renderers we're supposed to load.
	 */
	public static void loadRenderers(Properties properties)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String defaultRendererName = properties.getProperty(DEFAULT_RENDERER_NAME_KEY);
		if (defaultRendererName == null || defaultRendererName.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"No '" + DEFAULT_RENDERER_NAME_KEY + "' value was provided in 'renderers.properties'.");
		}
		defaultRendererName = defaultRendererName.trim();

		String renderersNameProperty = properties.getProperty("renderers");
		if (renderersNameProperty == null || renderersNameProperty.trim().isEmpty()) {
			throw new IllegalArgumentException("No 'renderers' value was provided in 'renderers.properties'.");
		}

		String rendererNames[] = PATTERN.split(renderersNameProperty);
		for (String name : rendererNames) {
			String classNameProperty = properties.getProperty(name + ".class");
			if (classNameProperty == null || classNameProperty.trim().isEmpty()) {
				throw new IllegalArgumentException(
						"No '" + name + ".class' value was provided in 'renderers.properties'.");
			}
			Class<?> clazz = Class.forName(classNameProperty);
			Constructor<?> constructor = clazz.getConstructor();
			Renderer renderer = (Renderer) constructor.newInstance();

			String mainMime = properties.getProperty(name + ".main_mime");
			if (mainMime == null || mainMime.isEmpty()) {
				throw new IllegalArgumentException(
						"No '" + name + ".main_mime' value was provided in 'renderers.properties'.");
			}

			mainMime = mainMime.trim().toLowerCase();
			validateMimeType(mainMime);

			RendererWrapper rendererWrapper = new RendererWrapper(renderer, mainMime);
			RendererWrapper previousRenderer = renderers.put(mainMime, rendererWrapper);
			if (previousRenderer != null) {
				throw new IllegalArgumentException("A renderer with '" + mainMime + "' already exists.");
			}

			Set<String> mimeTypes = getMimeTypes(properties.getProperty(name + ".mimes"));

			for (String contentType : mimeTypes) {
				previousRenderer = renderers.put(contentType, rendererWrapper);
				if (previousRenderer != null && !previousRenderer.equals(rendererWrapper)) {
					throw new IllegalArgumentException("A renderer with " + contentType + " already exists.");
				}
			}

			if (name.equals(defaultRendererName)) {
				defaultRenderer = rendererWrapper;
			}
		}

		if (defaultRenderer == null) {
			throw new IllegalArgumentException(
					"Default renderer '" + defaultRendererName + "' not found in renderers.");
		}
	}

	private static void validateMimeType(String mimeType) {
		boolean matches = mimeValidationPattern.matcher(mimeType).matches();
		if (!matches) {
			throw new IllegalArgumentException("Invalid MIME type: " + mimeType);
		}
	}

	private static Set<String> getMimeTypes(String unparsedMimeFromProperties) {
		String[] rendererObjectMimes = null;
		if (unparsedMimeFromProperties != null && !unparsedMimeFromProperties.isEmpty()) {
			rendererObjectMimes = PATTERN.split(unparsedMimeFromProperties);
		}

		if (rendererObjectMimes == null || rendererObjectMimes.length <= 0) {
			return Collections.emptySet();
		}

		Set<String> result = new HashSet<>();

		for (String mime : rendererObjectMimes) {
			if (mime == null) {
				continue;
			}

			mime = mime.trim();

			if (mime.isEmpty()) {
				continue;
			}

			mime = mime.toLowerCase();
			validateMimeType(mime);
			result.add(mime);
		}

		return result;
	}

	/**
	 * Returns the renderer that handles MIME <code>mime</code>.
	 *
	 * @param resourceType
	 *            A content type the user accepts. (Example: "text/plain",
	 *            "application/xml", etc.)
	 * @return the renderer that handles MIME <code>mime</code>.
	 */
	public static RendererWrapper get(String mime) {
		if (mime == null) {
			return null;
		}

		return renderers.get(mime.trim().toLowerCase());
	}

	/**
	 * @return The default renderer for the server
	 */
	public static RendererWrapper getDefaultRenderer() {
		return defaultRenderer;
	}

}
