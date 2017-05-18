package mx.nic.rdap.server;

import java.util.PriorityQueue;

/**
 * Rather bafflingly, the servlets API does not parse the "Accept" field of the
 * HTTP header. So we do it ourselves, here.
 */
public class AcceptHeaderFieldParser {

	/**
	 * Holds the client's preferred content types is decreasing preferred order.
	 * 
	 * Reminder: Do NOT assume the queue's iterator will return the elements in
	 * any particular order; use {@link PriorityQueue#remove()} instead.
	 * 
	 * (One use only, of course.)
	 */
	private PriorityQueue<Accept> queue;

	/**
	 * @param acceptField
	 *            the HTTP header's "Accept" field.
	 */
	public AcceptHeaderFieldParser(String acceptField) {
		this.queue = new PriorityQueue<>();

		if (acceptField == null) {
			return;
		}

		String[] mediaTypes = acceptField.split(",");
		for (String mediaType : mediaTypes) {
			queue.add(new Accept(mediaType));
		}
	}

	/**
	 * @see #queue
	 */
	public PriorityQueue<Accept> getQueue() {
		return queue;
	}

	protected class Accept implements Comparable<Accept> {
		private static final String QVALUE_PREFIX = "q=";

		private String mediaRange;
		private float qvalue;

		/**
		 * Remember to catch {@link NumberFormatException} and
		 * {@link IllegalArgumentException}.
		 */
		private Accept(String acceptString) {
			acceptString = acceptString.replaceAll("\\s+", "");
			String[] accept = acceptString.split(";");

			this.mediaRange = accept[0];

			for (int i = 1; i < accept.length; i++) {
				if (accept[i].startsWith(QVALUE_PREFIX)) {
					this.qvalue = parseQ(accept[i]);
					return;
				}
			}

			this.qvalue = 1f;
		}

		private float parseQ(String string) {
			String q = string.substring(QVALUE_PREFIX.length() + 1);
			return Float.parseFloat(q);
		}

		public String getMediaRange() {
			return mediaRange;
		}

		@Override
		public int compareTo(Accept other) {
			return -compare(other);
		}

		private int compare(Accept other) {
			int result = Float.compare(qvalue, other.qvalue);
			if (result != 0) {
				return result;
			}

			if (this.equals(other)) {
				return 0;
			}

			String[] thisTokens = mediaRange.split("/");
			String[] otherTokens = other.mediaRange.split("/");

			result = compareTokens(thisTokens[0], otherTokens[0]);
			if (result != 0) {
				return result;
			}

			if (thisTokens.length < 2 && otherTokens.length < 2) {
				return 0;
			}

			if (thisTokens.length < 2) {
				return compareTokens("*", otherTokens[1]);
			}
			if (otherTokens.length < 2) {
				return compareTokens(thisTokens[1], "*");
			}

			return compareTokens(thisTokens[1], otherTokens[1]);
		}

		private int compareTokens(String t1, String t2) {
			if (t1.equals(t2)) {
				return 0;
			}
			if ("*".equals(t1)) {
				return -1;
			}
			if ("*".equals(t2)) {
				return 1;
			}
			return t1.compareTo(t2);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Accept)) {
				return false;
			}

			Accept other = (Accept) obj;
			return mediaRange.equals(other.mediaRange) && qvalue == other.qvalue;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((mediaRange == null) ? 0 : mediaRange.hashCode());
			result = prime * result + (Float.valueOf(qvalue).hashCode());
			return result;
		}

		@Override
		public String toString() {
			return mediaRange + " (" + qvalue + ")";
		}
	}

}
