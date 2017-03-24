package mx.nic.rdap.server;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;

/**
 * SAX Handler for files that contains {@link Remark}.
 */
public class NoticesHandler extends DefaultHandler {

	private List<Remark> noticesList;

	private String actualElement;

	private Remark actualRemark;

	private Link actualLink;

	private static final String EMPTY_STRING = "";

	public NoticesHandler() {
		noticesList = new ArrayList<>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		actualElement = qName;
		switch (qName) {
		case "notice":
			actualRemark = new Remark();
			break;
		case "link":
			actualLink = new Link();
			handleLinkAttributes(actualLink, attributes);
			break;
		default:
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
			link.setHreflag(value);
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
		case "notice":
			noticesList.add(actualRemark);
			actualRemark = null;
			break;

		default:
			break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		String value;
		switch (actualElement) {
		case "title":
			value = new String(ch, start, length);
			actualRemark.setTitle(value.trim());
			cleanActualElement();
			break;
		case "type":
			value = new String(ch, start, length);
			actualRemark.setType(value.trim());
			cleanActualElement();
			break;
		case "line":
			RemarkDescription line = new RemarkDescription();
			value = new String(ch, start, length);
			line.setDescription(value.trim());
			actualRemark.getDescriptions().add(line);
			cleanActualElement();
			break;
		case "link":
			value = new String(ch, start, length);
			actualLink.setValue(value.trim());
			actualRemark.getLinks().add(actualLink);
			actualLink = null;
			cleanActualElement();
			break;
		}
	}

	public List<Remark> getNoticesList() {
		return noticesList;
	}

	private void cleanActualElement() {
		actualElement = EMPTY_STRING;
	}

}
