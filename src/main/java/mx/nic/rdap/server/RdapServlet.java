package mx.nic.rdap.server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.PriorityQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.nic.rdap.db.exception.InvalidValueException;
import mx.nic.rdap.db.exception.ObjectNotFoundException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.server.AcceptHeaderFieldParser.Accept;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.renderer.DefaultRenderer;
import mx.nic.rdap.server.renderer.json.JsonUtil;

/**
 * Main class of the RDAP Servlet.
 */
public abstract class RdapServlet extends HttpServlet {

	/** This is just a warning shutupper. */
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doHead(HttpServletRequest, HttpServletResponse)
	 */
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handleRequest(request, response, (r) -> doRdapHead(r));
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handleRequest(request, response, (r) -> doRdapGet(r));
	}

	private void handleRequest(HttpServletRequest request, HttpServletResponse response, HandleAction predicate)
			throws ServletException, IOException {
		RdapResult result;
		try {
			result = predicate.handle(request);
		} catch (ObjectNotFoundException e) {
			response.sendError(404, e.getMessage());
			return;
		} catch (InvalidValueException e) {
			response.sendError(422, e.getMessage());
			return;
		} catch (SQLException | IOException | RdapDataAccessException e) {
			response.sendError(500, e.getMessage());
			return;
		} catch (RequestHandleException e) {
			response.sendError(e.getHttpResponseStatusCode(), e.getMessage());
			return;
		}

		if (!RdapConfiguration.getServerProfile().equals(OperationalProfile.NONE))
			JsonUtil.createTermsOfService(request.getServletContext().getRealPath(File.separator));

		Renderer renderer = findRenderer(request);
		response.setCharacterEncoding("UTF-8");
		response.setContentType(renderer.getResponseContentType());
		renderer.render(result, response.getWriter());
	}

	/**
	 * Handles the `request` GET request and builds a response. Think of it as a
	 * {@link HttpServlet#doGet(HttpServletRequest, HttpServletResponse)},
	 * except you don't have to grab a database connection and the response will
	 * be built for you.
	 * 
	 * @param request
	 *            request to the servlet.
	 * @param connection
	 *            Already initialized connection to the database.
	 * @return response to the user.
	 * @throws RequestHandleException
	 *             Errors found handling `request`.
	 */
	protected abstract RdapResult doRdapGet(HttpServletRequest request)
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException;

	/**
	 * Handles the `request` HEAD request and builds a response. Think of it as
	 * a {@link HttpServlet#doGet(HttpServletRequest, HttpServletResponse)},
	 * except you don't have to grab a database connection and the response will
	 * be built for you.
	 * 
	 * @param request
	 *            request to the servlet.
	 * @param connection
	 *            Already initialized connection to the database.
	 * @return response to the user.
	 * @throws RequestHandleException
	 *             Errors found handling `request`.
	 */
	protected abstract RdapResult doRdapHead(HttpServletRequest request)
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException;

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

	private interface HandleAction {
		RdapResult handle(HttpServletRequest request)
				throws IOException, SQLException, RequestHandleException, RdapDataAccessException;
	}

}
