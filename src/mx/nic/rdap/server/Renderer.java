package mx.nic.rdap.server;

import java.io.PrintWriter;

/**
 * A response formatter. Transforms a {@link RdapResult} to something the user
 * can parse.
 *
 * @author aleiva
 */
public interface Renderer {

	/** Content types that trigger this renderer. */
	public String[] getRequestContentTypes();

	/** Content type this renderer returns. */
	public String getResponseContentType();

	/**
	 * Prints `result` in `printWriter`.
	 * 
	 * @param result
	 *            the response we're building to the user, object version.
	 * @param printWriter
	 *            the response we're building to the user, stream/writer
	 *            version.
	 */
	public void render(RdapResult result, PrintWriter printWriter);

}
