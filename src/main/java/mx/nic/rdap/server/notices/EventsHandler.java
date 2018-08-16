package mx.nic.rdap.server.notices;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import mx.nic.rdap.core.catalog.EventAction;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;

/**
 * SAX Handler for files that contains {@link Event}.
 */
public class EventsHandler extends DefaultHandler {

	private List<Event> eventsList;

	private String actualElement;

	private Event actualEvent;

	private Link actualLink;

	private static final String EMPTY_STRING = "";

	public EventsHandler() {
		eventsList = new ArrayList<>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		actualElement = qName;
		switch (qName) {
			case "event" :
				actualEvent = new Event();
				break;
			case "link" :
				actualLink = new Link();
				handleLinkAttributes(actualLink, attributes);
				break;
			default :
				break;
		}
	}

	private void handleLinkAttributes(Link link, Attributes attributes) {
		String value = attributes.getValue("rel");
		if (value != null && !value.isEmpty()) {
			link.setRel(value);
		}

		value = attributes.getValue("href");
		if (value != null && !value.isEmpty()) {
			link.setHref(value);
		}

		value = attributes.getValue("hreflang");
		if (value != null && !value.isEmpty()) {
			// TODO modify XSD to read multiple values of hreflang
			link.addHreflang(value);
		}

		value = attributes.getValue("title");
		if (value != null && !value.isEmpty()) {
			link.setTitle(value);
		}

		value = attributes.getValue("media");
		if (value != null && !value.isEmpty()) {
			link.setMedia(value);
		}

		value = attributes.getValue("type");
		if (value != null && !value.isEmpty()) {
			link.setType(value);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		switch (qName) {
			case "event" :
				eventsList.add(actualEvent);
				actualEvent = null;
				break;

			default :
				break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		String value;
		switch (actualElement) {
			case "eventAction" :
				value = new String(ch, start, length);
				actualEvent.setEventAction(EventAction.getByName(value.trim()));
				cleanActualElement();
				break;
			case "eventActor" :
				value = new String(ch, start, length);
				actualEvent.setEventActor(value.trim());
				cleanActualElement();
				break;
			case "eventDate" :
				value = new String(ch, start, length);
				value = value.trim();
//				LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME).toInstant(ZoneOffset.UTC);
				Date eventDate = Date.from(LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME).toInstant(ZoneOffset.UTC));
				actualEvent.setEventDate(eventDate);
				cleanActualElement();
				break;
			case "link" :
				value = new String(ch, start, length);
				actualLink.setValue(value.trim());
				actualEvent.getLinks().add(actualLink);
				actualLink = null;
				cleanActualElement();
				break;
		}
	}

	public List<Event> getEventList() {
		return eventsList;
	}

	private void cleanActualElement() {
		actualElement = EMPTY_STRING;
	}

}
