package mx.nic.rdap.server.servlet;

import java.util.PriorityQueue;

import org.junit.Test;

import junit.framework.TestCase;
import mx.nic.rdap.server.servlet.AcceptHeaderFieldParser;
import mx.nic.rdap.server.servlet.AcceptHeaderFieldParser.Accept;

public class AcceptHeaderTest extends TestCase {

	/**
	 * First example of https://tools.ietf.org/html/rfc7231#section-5.3.2
	 */
	@Test
	public void testFirstExample() {
		String string = "audio/*; q=0.2, audio/basic";
		AcceptHeaderFieldParser header = new AcceptHeaderFieldParser(string);
		PriorityQueue<Accept> accepts = header.getQueue();

		assertEquals("audio/basic", accepts.remove().getMediaRange());
		assertEquals("audio/*", accepts.remove().getMediaRange());
		assertTrue(accepts.isEmpty());
	}

	/**
	 * Second example of https://tools.ietf.org/html/rfc7231#section-5.3.2
	 */
	@Test
	public void testMoreElaborateExample() {
		String string = "text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c";
		AcceptHeaderFieldParser header = new AcceptHeaderFieldParser(string);
		PriorityQueue<Accept> accepts = header.getQueue();

		String first = accepts.remove().getMediaRange();
		String second = accepts.remove().getMediaRange();
		assertTrue("text/html".equals(first) || "text/html".equals(second));
		assertTrue("text/x-c".equals(first) || "text/x-c".equals(second));
		assertEquals("text/x-dvi", accepts.remove().getMediaRange());
		assertEquals("text/plain", accepts.remove().getMediaRange());
		assertTrue(accepts.isEmpty());
	}

	/**
	 * Third example of https://tools.ietf.org/html/rfc7231#section-5.3.2
	 */
	@Test
	public void testOverrides() {
		String string = "text/*, text/plain, */*";
		// No "format". We don't care about params other than q yet.
		AcceptHeaderFieldParser header = new AcceptHeaderFieldParser(string);
		PriorityQueue<Accept> accepts = header.getQueue();

		assertEquals("text/plain", accepts.remove().getMediaRange());
		assertEquals("text/*", accepts.remove().getMediaRange());
		assertEquals("*/*", accepts.remove().getMediaRange());
		assertTrue(accepts.isEmpty());
	}

	/**
	 * More self-aware attempts to break the iteration order.
	 */
	@Test
	public void testMoreOrder() {
		String string = "*/*, text/*, text/plain, */*, text/*, text/plain, */*";
		AcceptHeaderFieldParser header = new AcceptHeaderFieldParser(string);
		PriorityQueue<Accept> accepts = header.getQueue();

		assertEquals("text/plain", accepts.remove().getMediaRange());
		assertEquals("text/plain", accepts.remove().getMediaRange());
		assertEquals("text/*", accepts.remove().getMediaRange());
		assertEquals("text/*", accepts.remove().getMediaRange());
		assertEquals("*/*", accepts.remove().getMediaRange());
		assertEquals("*/*", accepts.remove().getMediaRange());
		assertEquals("*/*", accepts.remove().getMediaRange());
		assertTrue(accepts.isEmpty());
	}

	/**
	 * This one is probably pointless; I'm only keeping it because it's easy.
	 */
	@Test
	public void testMoreRandom() {
		String string = "text/html,application/xml;q=0.5,*/*;q=0.8,application/json";
		AcceptHeaderFieldParser header = new AcceptHeaderFieldParser(string);
		PriorityQueue<Accept> accepts = header.getQueue();

		assertEquals("text/html", accepts.remove().getMediaRange());
		assertEquals("application/json", accepts.remove().getMediaRange());
		assertEquals("*/*", accepts.remove().getMediaRange());
		assertEquals("application/xml", accepts.remove().getMediaRange());
		assertTrue(accepts.isEmpty());
	}

}
