package mx.nic.rdap.renderer;

/**
 * This is the renderer for requests lacking a content type.
 * 
 * It writes responses in Json anyway, but returns a content type browsers have
 * no trouble displaying verbatim.
 * 
 * @author aleiva
 */
public class DefaultRenderer extends JsonRenderer {

	@Override
	public String[] getRequestContentTypes() {
		return null;
	}

	@Override
	public String getResponseContentType() {
		return "text/plain";
	}

}
