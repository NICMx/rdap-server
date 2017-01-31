/**
 * 
 */
package mx.nic.rdap.server;

import java.io.File;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.scan.StandardJarScanner;

public class WebServerLauncher {

	public static void main(String[] args) throws ServletException, LifecycleException {
		String webappDirLocation = "src/main/webapp/";
		Tomcat tomcat = new Tomcat();
		// Set port
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8080";
		}
		tomcat.setPort(Integer.valueOf(webPort));
		// TODO get context to load all servlets
		StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
		((StandardJarScanner) ctx.getJarScanner()).setScanAllDirectories(true);
		// declare an alternate location for your "WEB-INF/classes" dir:
		File additionWebInfClasses = new File("target/classes");
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(
				new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
		ctx.setResources(resources);

		tomcat.start();
		tomcat.getServer().await();
	}

}
