package mx.nic.rdap.server.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.db.RemarkDAO;
import mx.nic.rdap.db.RemarkDescriptionDAO;
import mx.nic.rdap.db.exception.InvalidadDataStructure;

/**
 * Random miscellaneous functions useful anywhere.
 */
public class Util {

	/**
	 * Loads the properties configuration file
	 * <code>META-INF/fileName.properties</code> and returns it.
	 * 
	 * @param fileName
	 *            name of the configuration file you want to load.
	 * @return configuration requested.
	 * @throws IOException
	 *             Error attempting to read the configuration out of the
	 *             classpath.
	 */
	public static Properties loadProperties(String fileName) throws IOException {
		fileName = "META-INF/" + fileName + ".properties";
		Properties result = new Properties();
		try (InputStream configStream = Util.class.getClassLoader().getResourceAsStream(fileName)) {
			result.load(configStream);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<Remark> readNoticesFromFiles(String filePath) throws FileNotFoundException {
		List<Remark> notices = new ArrayList<Remark>();
		List<List<Object>> noticesData = readFiles(filePath);
		for (List<Object> noticeData : noticesData) {
			Remark notice = new Remark();
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
				Link link;
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

	private static Link parseLink(String linkData) throws InvalidadDataStructure {
		Link link = new Link();
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

	private static List<List<Object>> readFiles(String folderPath) {

		File folder = new File(folderPath);

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
			throw new RuntimeException("There are no text files on folder " + folderPath);
		}

		return noticesData;
	}

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
						} while (!line.isEmpty() && !line.startsWith("link"));
						if (!description.equals(" ") || !description.isEmpty()) {
							descriptions.add(description);
							description = "";
						}
					} while (!line.startsWith("link"));
				}
				if (line.trim().startsWith("link")) {
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

	public static Remark getOperationalProfileRemark() {
		RemarkDAO remark = new RemarkDAO();
		RemarkDescriptionDAO description = new RemarkDescriptionDAO();
		description.setDescription(
				"This response conforms to the RDAP Operational Profile for gTLD Registries and Registrars version 1.0");
		remark.getDescriptions().add(description);
		return remark;
	}

	public static Remark getEppInformationRemark() {
		RemarkDAO remark = new RemarkDAO();
		remark.setTitle("EPP Status Codes");
		RemarkDescriptionDAO description = new RemarkDescriptionDAO();
		description.setDescription("For more information on domain status codes, please visit https://icann.org/epp");
		remark.getDescriptions().add(description);
		Link link = new Link();
		link.setHref("https://icann.org/epp");
		remark.getLinks().add(link);
		return remark;
	}

	public static Remark getWhoisInaccuracyComplaintFormRemark() {
		RemarkDAO remark = new RemarkDAO();
		RemarkDescriptionDAO description = new RemarkDescriptionDAO();
		remark.setTitle("Whois Inaccuracy Complaint Form");
		description.setDescription("URL of the ICANN Whois Inaccuracy Complaint Form: https://www.icann.org/wicf");
		remark.getDescriptions().add(description);
		Link link = new Link();
		link.setHref("https://www.icann.org/wicf");
		remark.getLinks().add(link);
		return remark;
	}

	public static Remark getTermsOfServiceNotice(String filePath) throws FileNotFoundException {
		return Util.readNoticesFromFiles(filePath).get(0);
	}
}
