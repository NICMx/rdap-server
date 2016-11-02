package mx.nic.rdap.server.result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.db.LinkDAO;
import mx.nic.rdap.server.db.RemarkDAO;
import mx.nic.rdap.server.exception.InvalidadDataStructure;

/**
 * 
 * @author dalpuche
 *
 */
public class HelpResult implements RdapResult {

	private List<RemarkDAO> notices = new ArrayList<>();
	public static String helpFolderPath;

	public HelpResult(ServletContext servletContext) throws FileNotFoundException {
		helpFolderPath = servletContext.getRealPath(File.separator) + "\\WEB-INF\\classes\\META-INF\\help\\";
		this.notices = readNoticesFromFiles();

	}

	@SuppressWarnings("unchecked")
	private List<RemarkDAO> readNoticesFromFiles() throws FileNotFoundException {
		List<List<Object>> noticesData = readFiles();
		for (List<Object> noticeData : noticesData) {
			RemarkDAO notice = new RemarkDAO();
			List<String> descriptions = (List<String>) noticeData.get(1);
			notice.setTitle((String) noticeData.get(0));
			for (String descriptionString : descriptions) {
				RemarkDescription description = new RemarkDescription();
				description.setDescription(descriptionString);
				notice.getDescriptions().add(description);
			}

			List<Link> links = new ArrayList<Link>();
			List<String> linksData = (List<String>) noticeData.get(2);
			for (String linkData : linksData) {
				LinkDAO link;
				try {
					link = parseLink(linkData);
					notice.setLinks(links);
					links.add(link);
				} catch (InvalidadDataStructure e) {
					e.printStackTrace();
				}
			}

			notices.add(notice);
		}

		return notices;

	}

	/**
	 * Converts from string to LinObject
	 * 
	 * @param linkData
	 *            Link data must be in the format:
	 *            "value|rel|href|hreflang|title|media|type". In case of null
	 *            object there must be an space between the two pipes of the
	 *            null object.
	 * @return
	 * @throws InvalidadDataStructure
	 */
	private LinkDAO parseLink(String linkData) throws InvalidadDataStructure {
		LinkDAO link = new LinkDAO();
		if (linkData != null && !linkData.trim().isEmpty()) {
			List<String> linkList = Arrays.asList(linkData.split("\\|"));
			if (linkList.size() == 7) {
				link.setValue(linkList.get(0).trim());
				link.setRel(linkList.get(1).trim());
				link.setHref(linkList.get(2).trim());
				link.setHreflag(linkList.get(3).trim());
				link.setTitle(linkList.get(4).trim());
				link.setMedia(linkList.get(5).trim());
				link.setType(linkList.get(6).trim());
			} else {
				throw new InvalidadDataStructure();
			}
		}
		return link;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (RemarkDAO notice : notices) {
			builder.add(notice.toJson());
		}
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
		objectBuilder.add("notices", builder.build());
		return objectBuilder.build();
	}

	/**
	 * method which reads all files in help folder
	 * 
	 * @return
	 */
	private static List<List<Object>> readFiles() {

		File folder = new File(helpFolderPath);

		List<File> txtList = Arrays.asList(folder.listFiles());
		Collections.sort(txtList);
		List<List<Object>> noticesData = new ArrayList<List<Object>>();

		if (txtList.size() != 0) {

			for (File file : txtList) {
				List<Object> notice = readFileContent(file);
				noticesData.add(notice);
			}
		}

		else {
			throw new RuntimeException("There are no text files on help folder.");
		}

		return noticesData;
	}

	/**
	 * method which reads a file, line by line
	 * 
	 * @param file
	 * @return
	 */
	private static List<Object> readFileContent(File file) {
		boolean descriptionChecker = false;
		List<Object> notice = new ArrayList<Object>();
		String title = "";
		List<String> descriptions = new ArrayList<String>();
		List<String> links = new ArrayList<String>();
		String description = "";

		try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
			Iterator<String> iterator = stream.iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				if (line.trim().startsWith("title")) {
					title = line.substring(line.indexOf("=") + 1).trim();
				}
				if (line.trim().startsWith("description")) {
					line = line.substring(line.indexOf("=") + 1).trim();
					descriptionChecker = true;
				}
				if (descriptionChecker) {
					do {
						do {
							description = description.trim() + " " + line;
							line = iterator.next().trim();
							description = description.trim();
						} while (!line.isEmpty() && !line.startsWith("link1"));
						if (!description.equals(" ") || !description.isEmpty()) {
							descriptions.add(description);
							description = "";
						}
					} while (!line.startsWith("link1"));
				}
				if (line.trim().startsWith("link1")) {
					descriptionChecker = false;
				}
				if (line.trim().startsWith("link")) {
					links.add(line.substring(line.indexOf("=") + 1).trim());
				}
			}
			descriptions.removeAll(Arrays.asList(" ", "", null));
			notice.add(title);
			notice.add(descriptions);
			notice.add(links);
			return notice;
		}

		catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
