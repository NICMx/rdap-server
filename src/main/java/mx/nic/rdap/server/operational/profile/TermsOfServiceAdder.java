package mx.nic.rdap.server.operational.profile;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.Util;

public class TermsOfServiceAdder {

	private static List<Remark> termsOfService = new ArrayList<>();
	private static String helpFolderPath;

	public static List<Remark> listWithTerms(String realPath, List<Remark> oldRemarks) throws FileNotFoundException {
		if (termsOfService == null || termsOfService.isEmpty()) {
			helpFolderPath = realPath + "\\WEB-INF\\terms-of-service\\";
			termsOfService = Util.readNoticesFromFiles(helpFolderPath);
		}
		List<Remark> newRemarks = new ArrayList<Remark>();
		newRemarks.add(termsOfService.get(0));
		newRemarks.addAll(oldRemarks);
		return newRemarks;
	}

}
