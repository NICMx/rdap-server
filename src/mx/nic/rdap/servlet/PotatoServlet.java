package mx.nic.rdap.servlet;

import java.sql.Connection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.RdapResult;
import mx.nic.rdap.RdapServlet;
import mx.nic.rdap.result.PotatoResult;

@WebServlet(name = "rdap", urlPatterns = { "/potato/*" })
public class PotatoServlet extends RdapServlet {

	/** SHUT UP */
	private static final long serialVersionUID = 1L;

	@Override
	protected RdapResult doRdapGet(HttpServletRequest r, Connection c) {
		return new PotatoResult();
	}

	@Override
	protected RdapResult doRdapHead(HttpServletRequest r, Connection c) {
		return new PotatoResult();
	}

}
