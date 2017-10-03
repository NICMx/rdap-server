package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.renderer.Renderer;
import mx.nic.rdap.renderer.object.ExceptionResponse;
import mx.nic.rdap.renderer.object.HelpResponse;
import mx.nic.rdap.renderer.object.RequestResponse;
import mx.nic.rdap.renderer.object.SearchResponse;
import mx.nic.rdap.server.renderer.RendererPool;
import mx.nic.rdap.server.result.RdapResult;
import mx.nic.rdap.server.servlet.AcceptHeaderFieldParser.Accept;

/**
 * Base class of all RDAP servlets.
 */
public abstract class RdapServlet extends HttpServlet {

	/** This is just a warning shutupper. */
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(RdapServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		RdapResult result;

		try {
			result = doRdapGet(request);
		} catch (HttpException e) {
			response.sendError(e.getHttpResponseStatusCode(), e.getMessage());
			return;
		} catch (RdapDataAccessException e) {
			// Handled as an "Internal Server Error", it probably has some good things to log
			logger.log(Level.SEVERE, e.getMessage(), e);
			response.sendError(500, e.getMessage());
			return;
		}

		if (result == null) {
			response.sendError(404);
			return;
		}

		Renderer renderer = findRenderer(request);
		response.setCharacterEncoding("UTF-8");
		response.setContentType(renderer.getResponseContentType());
		// Recommendation of RFC 7480 section 5.6
		response.setHeader("Access-Control-Allow-Origin", "*");
		renderResult(renderer, result, response.getWriter());
	}

	@SuppressWarnings("unchecked")
	private void renderResult(Renderer renderer, RdapResult result, PrintWriter printWriter) {
		switch (result.getResultType()) {
		case AUTNUM:
			renderer.renderAutnum((RequestResponse<Autnum>) result.getRdapResponse(), printWriter);
			break;
		case DOMAIN:
			renderer.renderDomain((RequestResponse<Domain>) result.getRdapResponse(), printWriter);
			break;
		case DOMAINS:
			renderer.renderDomains((SearchResponse<Domain>) result.getRdapResponse(), printWriter);
			break;
		case ENTITIES:
			renderer.renderEntities((SearchResponse<Entity>) result.getRdapResponse(), printWriter);
			break;
		case ENTITY:
			renderer.renderEntity((RequestResponse<Entity>) result.getRdapResponse(), printWriter);
			break;
		case EXCEPTION:
			renderer.renderException((ExceptionResponse) result.getRdapResponse(), printWriter);
			break;
		case HELP:
			renderer.renderHelp((HelpResponse) result.getRdapResponse(), printWriter);
			break;
		case IP:
			renderer.renderIpNetwork((RequestResponse<IpNetwork>) result.getRdapResponse(), printWriter);
			break;
		case NAMESERVER:
			renderer.renderNameserver((RequestResponse<Nameserver>) result.getRdapResponse(), printWriter);
			break;
		case NAMESERVERS:
			renderer.renderNameservers((SearchResponse<Nameserver>) result.getRdapResponse(), printWriter);
			break;
		default:
			break;
		}

	}

	/**
	 * Handles the `request` GET request and builds a response. Think of it as a
	 * {@link HttpServlet#doGet(HttpServletRequest, HttpServletResponse)}, except
	 * the response will be built for you.
	 * 
	 * @param request
	 *            request to the servlet.
	 * @param connection
	 *            Already initialized connection to the database.
	 * @return response to the user.
	 * @throws HttpException
	 *             Errors found handling `request`.
	 */
	protected abstract RdapResult doRdapGet(HttpServletRequest request) throws HttpException, RdapDataAccessException;

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

		return RendererPool.getDefaultRenderer();
	}

}
