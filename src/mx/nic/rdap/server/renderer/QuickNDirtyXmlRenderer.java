package mx.nic.rdap.server.renderer;

import java.io.PrintWriter;
import java.util.Map.Entry;

import javax.json.JsonObject;
import javax.json.JsonValue;

import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.Renderer;

/**
 * Just a placeholder so I can test the Renderer framework.
 */
public class QuickNDirtyXmlRenderer implements Renderer {

	@Override
	public String[] getRequestContentTypes() {
		return new String[] { "application/xml" };
	}

	@Override
	public String getResponseContentType() {
		return "application/xml";
	}

	@Override
	public void render(RdapResult result, PrintWriter out) {
		out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		out.append("<thingy>\n");

		JsonObject json = result.toJson();
		for (Entry<String, JsonValue> entry : json.entrySet()) {
			out.append("\t<").append(entry.getKey()).append(">");
			// Not recursive :>
			out.append(entry.getValue().toString());
			out.append("</").append(entry.getKey()).append(">\n");
		}

		out.append("</thingy>");
	}

}
