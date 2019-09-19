package mx.nic.rdap.server.result;

import java.util.ArrayList;
import java.util.List;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.renderer.object.HelpResponse;
import mx.nic.rdap.server.notices.UserNotices;

/**
 * A Result from a help request.
 */
public class HelpResult extends RdapResult {

	private List<Remark> notices;

	public HelpResult(String header) {
		notices = new ArrayList<>();
		notices.addAll(UserNotices.getHelp(header));
		
		setResultType(ResultType.HELP);
		HelpResponse helpResponse = new HelpResponse();
		helpResponse.setNotices(notices);
		List<String> rdapConformance = new ArrayList<>();
		rdapConformance.add("rdap_level_0");
		helpResponse.setRdapConformance(rdapConformance);
		setRdapResponse(helpResponse);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#fillNotices()
	 */
	@Override
	public void fillNotices() {
		// At the moment, there is no notices for this request
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#validateResponse()
	 */
	@Override
	public void validateResponse() {
	}

}
