package mx.nic.rdap;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.nic.rdap.exception.RequestHandleException;
import mx.nic.rdap.exception.RequestValidationException;

/**
 * Main class of the RDAP Servlet.
 */
@WebServlet(name = "rdap", urlPatterns = { "/*" })
public class RdapServlet extends HttpServlet {

	/** File from which we will load the request handlers. */
	private static final String HANDLERS_FILE = "META-INF/handlers.properties";
	/** File from which we will load the renderer. */
	private static final String RENDERER_FILE = "META-INF/renderer.properties";

	/** This is just a warning shutupper. */
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RdapServlet()
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		super();
		RequestHandlerPool.loadHandlers(HANDLERS_FILE);
		RendererPool.loadRenderers(RENDERER_FILE);
	}

	/**
	 * @see HttpServlet#doHead(HttpServletRequest, HttpServletResponse)
	 */
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws ServletException, IOException {

		/* Parse the URI into an object. */
		BareRequest bareRequest;
		try {
			bareRequest = new BareRequest(httpRequest.getRequestURI());
		} catch (RequestValidationException e) {
			writeError(httpResponse, e.getMessage());
			return;
		}

		/* Recognize the request type and retrieve the proper handler. */
		RdapRequestHandler handler = RequestHandlerPool.get(bareRequest.getResourceType());
		if (handler == null) {
			writeError(httpResponse, "There is no handler mapped to label '" + bareRequest.getResourceType() + "'");
			return;
		}

		/* Ask the handler to validate and handle the request. */
		RdapRequest request;
		try {
			request = handler.validate(bareRequest.getPayload());
		} catch (RequestValidationException e) {
			writeError(httpResponse, e.getMessage());
			return;
		}
		RdapResult result;
		try {
			result = handler.handle(request);
		} catch (RequestHandleException e) {
			writeError(httpResponse, e.getMessage());
			return;
		}

		/* Build the response. */
		Renderer renderer = RendererPool.getActiveRenderer();
		renderer.render(result, httpResponse.getWriter());
	}

	private void writeError(HttpServletResponse httpResponse, String error) throws IOException {
		httpResponse.getWriter().append(error);
	}

}
