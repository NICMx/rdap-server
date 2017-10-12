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
import mx.nic.rdap.server.privacy.AutnumPrivacyFilter;
import mx.nic.rdap.server.privacy.DomainPrivacyFilter;
import mx.nic.rdap.server.privacy.EntityPrivacyFilter;
import mx.nic.rdap.server.privacy.IpNetworkPrivacyFilter;
import mx.nic.rdap.server.privacy.NameserverPrivacyFilter;
import mx.nic.rdap.server.renderer.RendererPool;
import mx.nic.rdap.server.result.RdapResult;
import mx.nic.rdap.server.servlet.AcceptHeaderFieldParser.Accept;
import mx.nic.rdap.server.util.PrivacyUtil;

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
		boolean wasFiltered = false;
		switch (result.getResultType()) {
		case AUTNUM:
			RequestResponse<Autnum> autnumRequestResponse = (RequestResponse<Autnum>) result.getRdapResponse();
			wasFiltered = AutnumPrivacyFilter.filterAutnum(autnumRequestResponse.getRdapObject());
			if (wasFiltered) {
				PrivacyUtil.addPrivacyRemarkAndStatus(autnumRequestResponse.getRdapObject());
			}
			renderer.renderAutnum(autnumRequestResponse, printWriter);
			break;
		case DOMAIN:
			RequestResponse<Domain> domainRequestResponse = (RequestResponse<Domain>) result.getRdapResponse();
			wasFiltered = DomainPrivacyFilter.filterDomain(domainRequestResponse.getRdapObject());
			if (wasFiltered) {
				PrivacyUtil.addPrivacyRemarkAndStatus(domainRequestResponse.getRdapObject());
			}
			renderer.renderDomain(domainRequestResponse, printWriter);
			break;
		case DOMAINS:
			SearchResponse<Domain> domainSearchResponse = (SearchResponse<Domain>) result.getRdapResponse();
			for (Domain domain : domainSearchResponse.getRdapObjects()) {
				wasFiltered = DomainPrivacyFilter.filterDomain(domain);
				if (wasFiltered) {
					PrivacyUtil.addPrivacyRemarkAndStatus(domain);
				}
			}
			renderer.renderDomains(domainSearchResponse, printWriter);
			break;
		case ENTITIES:
			SearchResponse<Entity> entitySearchResponse = (SearchResponse<Entity>) result.getRdapResponse();
			for (Entity entity : entitySearchResponse.getRdapObjects()) {
				wasFiltered = EntityPrivacyFilter.filterEntity(entity);
				if (wasFiltered) {
					PrivacyUtil.addPrivacyRemarkAndStatus(entity);
				}
			}
			renderer.renderEntities(entitySearchResponse, printWriter);
			break;
		case ENTITY:
			RequestResponse<Entity> entityRequestResponse = (RequestResponse<Entity>) result.getRdapResponse();
			wasFiltered = EntityPrivacyFilter.filterEntity(entityRequestResponse.getRdapObject());
			if (wasFiltered) {
				PrivacyUtil.addPrivacyRemarkAndStatus(entityRequestResponse.getRdapObject());
			}
			renderer.renderEntity(entityRequestResponse, printWriter);
			break;
		case EXCEPTION:
			renderer.renderException((ExceptionResponse) result.getRdapResponse(), printWriter);
			break;
		case HELP:
			renderer.renderHelp((HelpResponse) result.getRdapResponse(), printWriter);
			break;
		case IP:
			RequestResponse<IpNetwork> ipRequestResponse = (RequestResponse<IpNetwork>) result.getRdapResponse();
			wasFiltered = IpNetworkPrivacyFilter.filterIpNetwork(ipRequestResponse.getRdapObject());
			if (wasFiltered) {
				PrivacyUtil.addPrivacyRemarkAndStatus(ipRequestResponse.getRdapObject());
			}
			renderer.renderIpNetwork(ipRequestResponse, printWriter);
			break;
		case NAMESERVER:
			RequestResponse<Nameserver> nameserverRequestResponse = (RequestResponse<Nameserver>) result.getRdapResponse();
			wasFiltered = NameserverPrivacyFilter.filterNameserver(nameserverRequestResponse.getRdapObject());
			if (wasFiltered) {
				PrivacyUtil.addPrivacyRemarkAndStatus(nameserverRequestResponse.getRdapObject());
			}
			renderer.renderNameserver(nameserverRequestResponse, printWriter);
			break;
		case NAMESERVERS:
			SearchResponse<Nameserver> nameserverSearchResponse = (SearchResponse<Nameserver>) result.getRdapResponse();
			for (Nameserver nameserver : nameserverSearchResponse.getRdapObjects()) {
				wasFiltered = NameserverPrivacyFilter.filterNameserver(nameserver);
				if (wasFiltered) {
					PrivacyUtil.addPrivacyRemarkAndStatus(nameserver);
				}
			}
			renderer.renderNameservers(nameserverSearchResponse, printWriter);
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
