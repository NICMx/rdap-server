package mx.nic.rdap.server.filter;

/**
 * {@link Integer}s are immutable, which is bad for {@link RateLimitFilter}.
 * 
 * Here's a more performant (for this particular application) boxed request
 * counter, I hope.
 * 
 * Assumes outside locking.
 */
public class ClientRequestCount {

	private int requestCount;

	public ClientRequestCount() {
		super();
		this.requestCount = 1;
	}

	public void plusPlus() {
		requestCount++;
	}

	public int minusMinus() {
		requestCount--;
		return requestCount;
	}

	public int getCount() {
		return requestCount;
	}

	@Override
	public String toString() {
		return String.valueOf(requestCount);
	}

}
