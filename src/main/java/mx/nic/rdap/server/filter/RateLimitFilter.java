package mx.nic.rdap.server.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * If active, prevents any IP from doing too many requests at once.
 */
public class RateLimitFilter implements Filter {

	private final static Logger logger = Logger.getLogger(RateLimitFilter.class.getName());

	/**
	 * Clients doing requests RIGHT NOW. I haven't set an upper limit to how
	 * much this can grow because Tomcat's maxConnections already does it.
	 */
	private HashMap<InetAddress, ClientRequestCount> clients = new HashMap<>();
	/** Maximum number of requests any client can send simultaneously. */
	private int limit;

	@Override
	public void init(FilterConfig config) throws ServletException {
		String limit = config.getInitParameter("limit");
		this.limit = (limit == null) ? 20 : Integer.parseInt(limit);
		logger.info("Going to allow  " + this.limit + " simultaneous requests per client.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		InetAddress remoteAddr = InetAddress.getByName(request.getRemoteAddr());
		logger.info("Received a request from " + request.getRemoteAddr() + ".");

		ClientRequestCount requestCount = new ClientRequestCount();
		synchronized (clients) {
			ClientRequestCount oldRequestCount = clients.putIfAbsent(remoteAddr, requestCount);
			if (oldRequestCount != null) {
				if (oldRequestCount.getCount() >= limit) {
					refuse(response);
					return;
				}

				oldRequestCount.plusPlus();
				requestCount = oldRequestCount;
			}

			logger.info("Simultaneous requests: " + requestCount);
		}

		chain.doFilter(request, response);

		synchronized (clients) {
			logger.info("Substracting from this client's simultaneous connections.");
			int count = requestCount.minusMinus();
			if (count == 0) {
				logger.info("Ok, I can now forget about this client.");
				clients.remove(remoteAddr);
			}
			logger.info("Number of clients: " + clients.size());
		}
	}

	private void refuse(ServletResponse response) throws IOException {
		logger.info("Client has too many requests. Refusing.");

		if (!(response instanceof HttpServletResponse)) {
			logger.info("Response is not HTTP.");
			return; // I have no clue.
		}

		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.sendError(429, "Too many simultaneous requests!");
		logger.info("Sent 429.");
	}

	@Override
	public void destroy() {
		// Nothing needed.
	}

}
