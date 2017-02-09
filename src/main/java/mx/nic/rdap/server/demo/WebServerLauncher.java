/**
 * 
 */
package mx.nic.rdap.server.demo;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.scan.StandardJarScanner;

public class WebServerLauncher {

	public static void main(String[] args) throws ServletException, LifecycleException {

		Tomcat tomcat = new Tomcat();
		// Set port
		String webPort = "8080";
		if (args.length > 0) {

			try {
				InetAddress.getByName(args[0]);
				tomcat.getConnector().setProperty("address", args[0].trim());

			} catch (UnknownHostException e) {
				throw new RuntimeException("Invalid address");
			}

			if (args.length > 1) {
				try {
					Integer.parseInt(args[1]);
					webPort = args[1].trim();
				} catch (NumberFormatException nfe) {
					throw new RuntimeException("Invalid port");
				}
			}
		}
		tomcat.getConnector().setProperty("port", webPort);

		String webappDirLocation = "src/main/webapp/";
		Path path = Paths.get("webapp").toAbsolutePath();

		if (Files.exists(path)) {
			webappDirLocation = path.toString();
		} else {
			webappDirLocation = new File(webappDirLocation).getAbsolutePath();
		}

		StandardContext ctx = (StandardContext) tomcat.addWebapp("/", webappDirLocation);

		JarScanner jarScanner = ctx.getJarScanner();
		((StandardJarScanner) jarScanner).setScanAllDirectories(true);
		// declare an alternate location for your "WEB-INF/classes" dir:

		WebResourceRoot resources = new StandardRoot(ctx);

		// File classes = new File("target/classes");
		// resources.addPreResources(new DirResourceSet(resources,
		// "/WEB-INF/classes", classes.getAbsolutePath(), "/"));

		resources.createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/WEB-INF/classes",
				"target/rdap-server-0.0.1-SNAPSHOT.jar", null, "/");

		ctx.setResources(resources);
		ctx.setPath("rdap-server");
		tomcat.start();
		tomcat.getServer().await();
	}

}
