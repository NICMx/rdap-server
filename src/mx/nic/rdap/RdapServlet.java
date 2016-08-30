package mx.nic.rdap;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.PriorityQueue;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.nic.rdap.AcceptHeaderFieldParser.Accept;
import mx.nic.rdap.exception.RequestHandleException;
import mx.nic.rdap.exception.RequestValidationException;
import mx.nic.rdap.renderer.DefaultRenderer;

/**
 * Main class of the RDAP Servlet.
 */
@WebServlet(name = "rdap", urlPatterns = { "/*" })
public class RdapServlet extends HttpServlet {

	/** File from which we will load the request handlers. */
	private static final String HANDLERS_FILE = "handlers.properties";
	/** File from which we will load the renderer. */
	private static final String RENDERERS_FILE = "renderers.properties";
	/** File from which we will load the database connection. */
	private static final String DATABASE_FILE = "database.properties";

	/** This is just a warning shutupper. */
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RdapServlet()
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, SQLException {
		super();
		DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
		RequestHandlerPool.loadHandlers(Util.loadProperties(HANDLERS_FILE));
		RendererPool.loadRenderers(Util.loadProperties(RENDERERS_FILE));
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

		RdapResult result;
		try {
			result = handleRequest(handler, request);
		} catch (SQLException e1) {
			httpResponse.sendError(500, e1.getMessage());
			return;
		} catch (RequestHandleException e) {
			httpResponse.sendError(e.getHttpResponseStatusCode(), e.getMessage());
			return;
		}

		/* Build the response. */
		Renderer renderer = findRenderer(httpRequest);
		httpResponse.setContentType(renderer.getResponseContentType());
		renderer.render(result, httpResponse.getWriter());
	}

	/**
	 * Offsets the handling of <code>request</code> to <code>handler</code>.
	 *
	 * It's really just a one-liner to prevent nested try-catches from bleeding
	 * my eyes.
	 */
	private RdapResult handleRequest(RdapRequestHandler handler, RdapRequest request)
			throws SQLException, RequestHandleException {
		Connection connection = DatabaseSession.getConnection();
		try {
			return handler.handle(request, connection);
		} finally {
			connection.close();
		}
	}

	/**
	 * Tries hard to find the best suitable renderer for
	 * <code>httpRequest</code>.
	 */
	private Renderer findRenderer(HttpServletRequest httpRequest) {
		Renderer renderer;

		AcceptHeaderFieldParser parser = new AcceptHeaderFieldParser(httpRequest.getHeader("Accept"));
		PriorityQueue<Accept> accepts = parser.getQueue();

		while (!accepts.isEmpty()) {
			renderer = RendererPool.get(accepts.remove().getMediaRange());
			if (renderer != null) {
				return renderer;
			}
		}

		// TODO return 406 if none of the content types yield a renderer?
		return new DefaultRenderer();
	}

}
