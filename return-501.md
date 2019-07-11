---
title: Configuring a Not Implemented Servlet
breadcrums: ["Documentation", "documentation.html", "Further Custom Configuration", "documentation.html#further-custom-configuration"]
wheretogo: ["User Authentication", "authentication.html"]
---

# {{ page.title }}

In RedDog server you are allowed to configure a servlet in order to restrict its implementation and return a 501 error as stated in RFC7482 

`“… Server implementations are free to support only a subset of these features depending on local requirements.  Servers MUST return an HTTP 501 (Not Implemented) [RFC7231] response to inform clients of unsupported query types.”`

There are two ways to restrict unsupported query types :

## Via web.xml file

The first way is to map an integrated servlet ([NotImplementedServlet](https://github.com/NICMx/rdap-server/blob/master/src/main/java/mx/nic/rdap/server/servlet/NotImplementedServlet.java)) to the servlet you want to avoid using the web.xml file.
This way is intended for those who implement the reference provider ([rdap-sql-provider](http://10.2.245.80:4000/data-access-configuration.html)) and do not want to modify the java code.

E.g. the next part of the configuration is what is added in the web.xml file to send the 501 error to the ip and autnum requests.
```
	.
	.
	.
	<servlet>
		<servlet-name>ip</servlet-name>
		<servlet-class>mx.nic.rdap.server.servlet.NotImplementedServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>ip</servlet-name>
		<url-pattern>/ip/*</url-pattern>
	</servlet-mapping>
	<servlet>
		<servlet-name>autnum</servlet-name>
		<servlet-class>mx.nic.rdap.server.servlet.NotImplementedServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>autnum</servlet-name>
		<url-pattern>/autnum/*</url-pattern>
	</servlet-mapping>
	.
	.
	.
```

## Via Code

If you are programming your own data-access provider (See more at [data-access-layer](data-access-layer.html#example)), in the [implementation hub class](https://github.com/NICMx/rdap-data-access-api/blob/master/src/main/java/mx/nic/rdap/db/spi/DataAccessImplementation.java) you can throw a [NotImplementedException](https://github.com/NICMx/rdap-data-access-api/blob/master/src/main/java/mx/nic/rdap/db/exception/http/NotImplementedException.java) in the respective DAO function to tell to the RedDog server to throw a 501 Error.

If you want to add a custom message, just put a message while throwing the NotImplementedException.