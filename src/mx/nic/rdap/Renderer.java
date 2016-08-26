package mx.nic.rdap;

import java.io.PrintWriter;

public interface Renderer {

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
