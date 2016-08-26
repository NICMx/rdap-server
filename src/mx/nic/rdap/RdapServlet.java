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
import mx.nic.rdap.renderer.DefaultRenderer;

/**
 * Main class of the RDAP Servlet.
 */
@WebServlet(name = "rdap", urlPatterns = { "/*" })
public class RdapServlet extends HttpServlet {

	/** File from which we will load the request handlers. */
	private static final String HANDLERS_FILE = "META-INF/handlers.properties";
	/** File from which we will load the renderer. */
	private static final String RENDERERS_FILE = "META-INF/renderers.properties";

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
		RendererPool.loadRenderers(RENDERERS_FILE);
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
			httpResponse.sendError(404, e.getMessage());
			return;
		}

		/* Recognize the request type and retrieve the proper handler. */
		RdapRequestHandler handler = RequestHandlerPool.get(bareRequest.getResourceType());
		if (handler == null) {
			httpResponse.sendError(404, "There is no handler mapped to label '" + bareRequest.getResourceType() + "'");
			return;
		}

		/* Ask the handler to validate and handle the request. */
		RdapRequest request;
		try {
			request = handler.validate(bareRequest.getPayload());
		} catch (RequestValidationException e) {
			httpResponse.sendError(400, e.getMessage());
			return;
		}

		/* TODO get dababase connection here. */

		RdapResult result;
		try {
			result = handler.handle(request);
		} catch (RequestHandleException e) {
			httpResponse.sendError(400, e.getMessage());
			return;
		} finally {
			/* TODO return dababase connection here. */
		}

		/* Build the response. */
		Renderer renderer = RendererPool.get(httpRequest.getContentType());
		if (renderer == null) {
			renderer = new DefaultRenderer();
		}
		httpResponse.setContentType(renderer.getResponseContentType());
		renderer.render(result, httpResponse.getWriter());
	}

}
