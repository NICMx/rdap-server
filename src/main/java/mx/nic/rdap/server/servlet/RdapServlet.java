package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.renderer.Renderer;
import mx.nic.rdap.renderer.object.ExceptionResponse;
import mx.nic.rdap.renderer.object.HelpResponse;
import mx.nic.rdap.renderer.object.RdapResponse;
import mx.nic.rdap.renderer.object.RequestResponse;
import mx.nic.rdap.renderer.object.SearchResponse;
import mx.nic.rdap.server.configuration.RdapConfiguration;
import mx.nic.rdap.server.notices.RequestNotices;
import mx.nic.rdap.server.notices.UserEvents;
import mx.nic.rdap.server.notices.UserNotices;
import mx.nic.rdap.server.privacy.AutnumPrivacyFilter;
import mx.nic.rdap.server.privacy.DomainPrivacyFilter;
import mx.nic.rdap.server.privacy.EntityPrivacyFilter;
import mx.nic.rdap.server.privacy.IpNetworkPrivacyFilter;
import mx.nic.rdap.server.privacy.NameserverPrivacyFilter;
import mx.nic.rdap.server.renderer.RendererPool;
import mx.nic.rdap.server.renderer.RendererWrapper;
import mx.nic.rdap.server.result.NameserverResult;
import mx.nic.rdap.server.result.RdapResult;
import mx.nic.rdap.server.servlet.AcceptHeaderFieldParser.Accept;
import mx.nic.rdap.server.util.PrivacyUtil;
import mx.nic.rdap.server.util.Util;

/**
 * Base class of all RDAP servlets.
 */
public abstract class RdapServlet extends HttpServlet {

	/** This is just a warning shutupper. */
	private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(RdapServlet.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		RdapResult result;

		try {
			result = doRdapGet(request);
		} catch (HttpException e) {
			response.sendError(e.getHttpResponseStatusCode(), e.getMessage());
			return;
		} catch (RdapDataAccessException e) {
			// Handled as an "Internal Server Error", it probably has some good
			// things to
			// log
			logger.log(Level.SEVERE, e.getMessage(), e);
			response.sendError(500, e.getMessage());
			return;
		}

		if (result == null) {
			response.sendError(404);
			return;
		}

		RendererWrapper renderer = findRenderer(request);
		response.setCharacterEncoding("UTF-8");

		response.setContentType(renderer.getMimeType());
		// Recommendation of RFC 7480 section 5.6
		response.setHeader("Access-Control-Allow-Origin", "*");

		// Set the language
		if (result.getRdapResponse() instanceof RequestResponse) {
			RequestResponse<RdapObject> requestResponse = (RequestResponse<RdapObject>) result.getRdapResponse();
			requestResponse.getRdapObject().setLang(RdapConfiguration.getServerLanguage());
		} else if (result.getRdapResponse() instanceof SearchResponse) {
			SearchResponse<RdapObject> searchResponse = (SearchResponse<RdapObject>) result.getRdapResponse();
			searchResponse.getRdapObjects()
					.forEach(rdapObject -> rdapObject.setLang(RdapConfiguration.getServerLanguage()));
		}

		// Add nsSharingNameConformance
		if (RdapConfiguration.isNameserverSharingNameConformance()) {
			if (result.getRdapResponse().getRdapConformance() == null) {
				result.getRdapResponse().setRdapConformance(new ArrayList<>());
			}

			result.getRdapResponse().getRdapConformance().add("rdap_nameservers_sharing_name");
		}

		// Add customConformance
		if (!RdapConfiguration.getCustomConformances().isEmpty()) {
			if (result.getRdapResponse().getRdapConformance() == null) {
				result.getRdapResponse().setRdapConformance(new ArrayList<>());
			}

			for (String conformance : RdapConfiguration.getCustomConformances()) {
				result.getRdapResponse().getRdapConformance().add(conformance);
			}
		}
		
		// Add TOS notice if exists
		addNotices(result.getRdapResponse(), Util.getServerUrl(request));

		renderResult(renderer.getRenderer(), result, response.getWriter(), Util.getServerUrl(request));
	}

	@SuppressWarnings("unchecked")
	private void renderResult(Renderer renderer, RdapResult result, PrintWriter printWriter, String originalHeader) {
		// Filter objects according to privacy settings
		boolean wasFiltered = false;
		switch (result.getResultType()) {
			case AUTNUM :
				RequestResponse<Autnum> autnumRequestResponse = (RequestResponse<Autnum>) result.getRdapResponse();
				wasFiltered = AutnumPrivacyFilter.filterAutnum(autnumRequestResponse.getRdapObject());
				if (wasFiltered) {
					PrivacyUtil.addPrivacyRemarkAndStatus(autnumRequestResponse.getRdapObject());
				}
				handleAutnumPostFilter(result, autnumRequestResponse, originalHeader);
				renderer.renderAutnum(autnumRequestResponse, printWriter);
				break;
			case DOMAIN :
				RequestResponse<Domain> domainRequestResponse = (RequestResponse<Domain>) result.getRdapResponse();
				wasFiltered = DomainPrivacyFilter.filterDomain(domainRequestResponse.getRdapObject());
				if (wasFiltered) {
					PrivacyUtil.addPrivacyRemarkAndStatus(domainRequestResponse.getRdapObject());
				}
				if (RdapConfiguration.addEmailRemark())
					PrivacyUtil.addEmailRedactedForPrivacy(domainRequestResponse.getRdapObject());
				handleDomainPostFilter(result, domainRequestResponse, originalHeader);
				renderer.renderDomain(domainRequestResponse, printWriter);
				break;
			case DOMAINS :
				SearchResponse<Domain> domainSearchResponse = (SearchResponse<Domain>) result.getRdapResponse();
				for (Domain domain : domainSearchResponse.getRdapObjects()) {
					wasFiltered = DomainPrivacyFilter.filterDomain(domain);
					if (wasFiltered) {
						PrivacyUtil.addPrivacyRemarkAndStatus(domain);
					}
					if (RdapConfiguration.addEmailRemark())
						PrivacyUtil.addEmailRedactedForPrivacy(domain);
				}
				handleDomainsPostFilter(domainSearchResponse, originalHeader);
				renderer.renderDomains(domainSearchResponse, printWriter);
				break;
			case ENTITIES :
				SearchResponse<Entity> entitySearchResponse = (SearchResponse<Entity>) result.getRdapResponse();
				for (Entity entity : entitySearchResponse.getRdapObjects()) {
					wasFiltered = EntityPrivacyFilter.filterEntity(entity);
					if (wasFiltered) {
						PrivacyUtil.addPrivacyRemarkAndStatus(entity);
					}
				}
				handleEntitiesPostFilter(entitySearchResponse, originalHeader);
				renderer.renderEntities(entitySearchResponse, printWriter);
				break;
			case ENTITY :
				RequestResponse<Entity> entityRequestResponse = (RequestResponse<Entity>) result.getRdapResponse();
				wasFiltered = EntityPrivacyFilter.filterEntity(entityRequestResponse.getRdapObject());
				if (wasFiltered) {
					PrivacyUtil.addPrivacyRemarkAndStatus(entityRequestResponse.getRdapObject());
				}
				handleEntityPostFilter(result, entityRequestResponse, originalHeader);
				renderer.renderEntity(entityRequestResponse, printWriter);
				break;
			case EXCEPTION :
				renderer.renderException((ExceptionResponse) result.getRdapResponse(), printWriter);
				break;
			case HELP :
				renderer.renderHelp((HelpResponse) result.getRdapResponse(), printWriter);
				break;
			case IP :
				RequestResponse<IpNetwork> ipRequestResponse = (RequestResponse<IpNetwork>) result.getRdapResponse();
				wasFiltered = IpNetworkPrivacyFilter.filterIpNetwork(ipRequestResponse.getRdapObject());
				if (wasFiltered) {
					PrivacyUtil.addPrivacyRemarkAndStatus(ipRequestResponse.getRdapObject());
				}
				
				handleIpNetworkPostFilter(result, ipRequestResponse, originalHeader);
				renderer.renderIpNetwork(ipRequestResponse, printWriter);
				break;
			case NAMESERVER :
				RequestResponse<Nameserver> nameserverRequestResponse = (RequestResponse<Nameserver>) result
						.getRdapResponse();
				wasFiltered = NameserverPrivacyFilter.filterNameserver(nameserverRequestResponse.getRdapObject());
				if (wasFiltered) {
					PrivacyUtil.addPrivacyRemarkAndStatus(nameserverRequestResponse.getRdapObject());
				}
				handleNameserverPostFilter(result, nameserverRequestResponse, originalHeader);
				renderer.renderNameserver(nameserverRequestResponse, printWriter);
				break;
			case NAMESERVERS :
				SearchResponse<Nameserver> nameserverSearchResponse = (SearchResponse<Nameserver>) result
						.getRdapResponse();
				for (Nameserver nameserver : nameserverSearchResponse.getRdapObjects()) {
					wasFiltered = NameserverPrivacyFilter.filterNameserver(nameserver);
					if (wasFiltered) {
						PrivacyUtil.addPrivacyRemarkAndStatus(nameserver);
					}
				}
				handleNameserversPostFilter(nameserverSearchResponse, originalHeader);
				renderer.renderNameservers(nameserverSearchResponse, printWriter);
				break;
			default :
				break;
		}

	}

	private void addNotices(RdapResponse response, String header) {
		List<Remark> tos = UserNotices.getTos(header);
		if (tos != null && !tos.isEmpty()) {
			if (response.getNotices() == null) {
				response.setNotices(new ArrayList<>());
			}
			response.getNotices().addAll(tos);
		}

		List<Remark> userNotices = UserNotices.getNotices(header);
		if (userNotices != null && !userNotices.isEmpty()) {
			if (response.getNotices() == null) {
				response.setNotices(new ArrayList<>());
			}
			response.getNotices().addAll(userNotices);
		}

		return;
	}

	private void handleNameserverPostFilter(RdapResult result, RequestResponse<Nameserver> response, String header) {
		addToRequestEventsAndNotices(response, RequestNotices.getNsNotices(header));
		handleCountryProperty(response.getRdapObject());

		if (!RdapConfiguration.isNameserverSharingNameConformance()) {
			return;
		}

		Nameserver ns = response.getRdapObject();
		NameserverResult nsResult = (NameserverResult) result;

		Link searchOtherNS = nsResult.getSearchOtherNS();
		if (searchOtherNS == null) {
			return;
		}

		if (ns.getLinks() == null) {
			ns.setLinks(new ArrayList<>());
		}

		ns.getLinks().add(searchOtherNS);
	}

	private void handleDomainPostFilter(RdapResult result, RequestResponse<Domain> response, String header) {
		addToRequestEventsAndNotices(response, RequestNotices.getDomainNotices(header));
		handleCountryProperty(response.getRdapObject());
	}

	private void handleEntityPostFilter(RdapResult result, RequestResponse<Entity> response, String header) {
		addToRequestEventsAndNotices(response, RequestNotices.getEntityNotices(header));
		handleCountryProperty(response.getRdapObject());
	}

	private void handleIpNetworkPostFilter(RdapResult result, RequestResponse<IpNetwork> response, String header) {
		addToRequestEventsAndNotices(response, RequestNotices.getIpNotices(header));
		handleCountryProperty(response.getRdapObject());
	}

	private void handleAutnumPostFilter(RdapResult result, RequestResponse<Autnum> response, String header) {
		addToRequestEventsAndNotices(response, RequestNotices.getAutnumNotices(header));
		handleCountryProperty(response.getRdapObject());
	}
	
	private void handleDomainsPostFilter(SearchResponse<Domain> searchResponse, String header) {
		addToSearchEventsAndNotices(searchResponse, RequestNotices.getDomainNotices(header));
		for (RdapObject rdapObject : searchResponse.getRdapObjects()) {
			handleCountryProperty(rdapObject);
		}

	}
	
	private void handleNameserversPostFilter(SearchResponse<Nameserver> searchResponse, String header) {
		addToSearchEventsAndNotices(searchResponse, RequestNotices.getNsNotices(header));
		for (RdapObject rdapObject : searchResponse.getRdapObjects()) {
			handleCountryProperty(rdapObject);
		}
	}
	
	private void handleEntitiesPostFilter(SearchResponse<Entity> searchResponse, String header) {
		addToSearchEventsAndNotices(searchResponse, RequestNotices.getEntityNotices(header));
		for (RdapObject rdapObject : searchResponse.getRdapObjects()) {
			handleCountryProperty(rdapObject);
		}
	}

	private void handleCountryProperty(RdapObject rdapObj) {
		if (rdapObj.getEntities() != null) {
			for (Entity e : rdapObj.getEntities()) {
				handleCountryProperty(e);
			}
		}

		if (rdapObj instanceof Domain) {
			Domain d = (Domain) rdapObj;
			if (d.getNameServers() != null) {
				for (Nameserver ns : d.getNameServers()) {
					handleCountryProperty(ns);
				}
			}

			if (d.getIpNetwork() != null) {
				handleCountryProperty(d.getIpNetwork());
			}
		}
		
		if (!(rdapObj instanceof Entity)) {
			return;
		}

		Entity e = (Entity) rdapObj;

		if (e.getIpNetworks() != null) {
			for (IpNetwork ip : e.getIpNetworks()) {
				handleCountryProperty(ip);
			}
		}

		if (e.getAutnums() != null) {
			for (Autnum a : e.getAutnums()) {
				handleCountryProperty(a);
			}
		}

		if (e.getVCardList() == null || e.getVCardList().isEmpty()) {
			/* nothing to do */
			return;
		}

		VCard vCard = e.getVCardList().get(0);
		if (vCard.getPostalInfo() == null || vCard.getPostalInfo().isEmpty()) {
			return;
		}

		for (VCardPostalInfo postalInfo : vCard.getPostalInfo()) {
			if (RdapConfiguration.isCountryCodeReleased()) {
				postalInfo.setCountry(null);
			} else {
				postalInfo.setCountryCode(null);
			}
		}
	}

	
	private List<Event> mergeEvents(List<Event> userEvents, List<Event> objectEvents) {
		int userEventsSize = userEvents.size();
		boolean equals = false;
		List<Event> mergedEvents = new ArrayList<>(userEvents);

		for (Event oe : objectEvents) {
			/* The Event action could be hidden in privacy filter. */
			if (oe.getEventAction() == null) {
				mergedEvents.add(oe);
				continue;
			}

			for (int i = 0 ; i < userEventsSize ; i++) {
				Event ue = userEvents.get(i);
				equals = ue.getEventAction().equals(oe.getEventAction());
				if (equals) {
					break;
				}
			}
			if (!equals)
				mergedEvents.add(oe);
		}

		return mergedEvents;
	}
	/**
	 * Function to add to a single request response the events and notices configured in the application
	 * 
	 * @param response The object that receive the data
	 * @param requestNotices The notices that will be added
	 */
	private void addToRequestEventsAndNotices(RequestResponse<? extends RdapObject> response,
			List<Remark> requestNotices) {
		RdapObject rdapObject = response.getRdapObject();
		List<Event> events = UserEvents.getEvents();

		if (rdapObject != null) {
			if (events != null && !events.isEmpty()) {
				if (rdapObject.getEvents() == null) {
					rdapObject.setEvents(events);
				} else {
					List<Event> mergedEvents = mergeEvents(events, rdapObject.getEvents());
					rdapObject.setEvents(mergedEvents);
				}
			}

			/*
			 * Set the current timestamp on "last update of RDAP database" event if
			 * configured.
			 */
			if (RdapConfiguration.isDbDataLive()) {
				if (rdapObject.getEvents() == null) {
					rdapObject.setEvents(new ArrayList<>());
				}
				UserEvents.setCurrentTimestamp(rdapObject.getEvents());
			}
		}

		if (requestNotices != null && !requestNotices.isEmpty()) {
			if (response.getNotices() == null) {
				response.setNotices(new ArrayList<>());
			}
			response.getNotices().addAll(requestNotices);
		}
	}
	
	/**
	 * Function to add to a SearchResponse the events and notices configured in the application.
	 * 
	 * @param searchResponse The object that receive the data
	 * @param requestNotices The notices that will be added
	 */
	private void addToSearchEventsAndNotices(SearchResponse<? extends RdapObject> searchResponse,
			List<Remark> requestNotices) {
		List<? extends RdapObject> rdapObjects = searchResponse.getRdapObjects();
		List<Event> userEvents = UserEvents.getEvents();

		if (rdapObjects != null) {
			if (userEvents != null && !userEvents.isEmpty()) {
				for (RdapObject rdapObject : rdapObjects) {
					if (rdapObject.getEvents() == null) {
						rdapObject.setEvents(new ArrayList<>(userEvents));
					} else {
						List<Event> mergedEvents = mergeEvents(userEvents, rdapObject.getEvents());
						rdapObject.setEvents(mergedEvents);
					}
				}
			}

			/*
			 * Set the current timestamp on "last update of RDAP database" event if
			 * configured.
			 */
			if (RdapConfiguration.isDbDataLive()) {
				for (RdapObject rdapObject : rdapObjects) {
					if (rdapObject.getEvents() == null) {
						rdapObject.setEvents(new ArrayList<>());
					}
					UserEvents.setCurrentTimestamp(rdapObject.getEvents());
				}
			}
		}
		
		if (requestNotices != null && !requestNotices.isEmpty()) {
			if (searchResponse.getNotices() == null) {
				searchResponse.setNotices(new ArrayList<>());
			}
			searchResponse.getNotices().addAll(requestNotices);
		}
	}

	/**
	 * Handles the `request` GET request and builds a response. Think of it as a
	 * {@link HttpServlet#doGet(HttpServletRequest, HttpServletResponse)}, except the
	 * response will be built for you.
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
	 * Tries hard to find the best suitable renderer for <code>httpRequest</code>.
	 */
	private RendererWrapper findRenderer(HttpServletRequest httpRequest) {
		RendererWrapper renderer;

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
