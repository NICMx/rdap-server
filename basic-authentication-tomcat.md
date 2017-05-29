---
title: Configuring Basic user authentication for Red Dog in Apache Tomcat
---

# Configuring Basic user authentication for Red Dog in Apache Tomcat

In Red Dog we care about the information privacy, so our RDAP server provides the option to use a basic authorization schema (user/pass) to the server resources that you may want to protect or grant access only to a group of users. This document tells you how to set up that basic authentication for the user request on the Red Dog server.

1.	Open the META-INF/context.xml file in your installation directory. In Windows, it should look like this:

	![DATASOURCE PATH](img\datasource-path.png)

2.	Add the following lines:
 
	```
	<Realm className="org.apache.catalina.realm.DataSourceRealm"
	userTable="rdap.rdap_user" 
	userNameCol="rus_name" 
	userCredCol="rus_pass"
	userRoleTable="rdap.rdap_user_role" 
	roleNameCol="rur_name" 
	localDataSource="true"
	dataSourceName="jdbc/rdap" />
	```

	Some relevant notes for the lines above:

	* In the rdap database, the one created previously, exists a table, **rdap_user**, referenced below as the users table, that should contain one row for each one of your server users.

	* **The rdap_user** table contains the columns (it contains more but that will be covered on other documents):

		+  **rus_name** as the username column to be recognized by Tomcat when the user logs in.
		+  **rus_pass** as the password column to be recognized by Tomcat when the user logs in. This value must be cleartext.

	* There is a table, **rdap_user_role**, referenced below as the user roles table, that contains one row for every valid role that is assigned to a particular user. It is legal for a user to have zero, one, or more than one valid role. In example, you can assign to some users a Rol of Registrar and grant them access to advanced features, like regex searches and a large amount of results in responses.

	* **The rdap_user_role** table contain two columns:

		+ **rus_name** to be recognized by Tomcat (same value as is specified in the users table).
		+ **rur_name** as role name column,  a valid role associated with this user.

	* See the [Apache Tomcat 8 Realm configuration documentation](https://tomcat.apache.org/tomcat-8.0-doc/realm-howto.html) for more information.

3.	Find the **WEB-INF/web.xml** file in your installation directory.
4.	Add the following lines:
 
	```
	<security-constraint>
	    <web-resource-collection>
		<web-resource-name>Wildcard means whole app requires authentication</web-resource-name>
		<url-pattern>/nameservers</url-pattern>
		<url-pattern>/domains</url-pattern>
		<url-pattern>/entities</url-pattern>
	    </web-resource-collection>
	    <auth-constraint>
		<role-name>AUTHENTICATED</role-name>
	    </auth-constraint>
	</security-constraint>
	<security-role>
	    <role-name>AUTHENTICATED</role-name>
	</security-role>
	<login-config>
	    <auth-method>BASIC</auth-method>
	</login-config>
	```

	Some relevant notes for the lines above:
	* The `url-pattern` element define the protected resource which requires an user login. In this case its specifies the search urls.
	* The `auth-constraint` element specifies the role, AUTHENTICATED, that can access to the resources specified by the `url-pattern`.You must verify that roles assigned to user have the access that you want them to have. Checkout the rdap\_user\_role table in the rdap database.
	* See the [Oracle Java EE 6 Declaring security roles tutorial](https://docs.oracle.com/cd/E19798-01/821-1841/bncav/index.html "Oracle Java Declaring Security Roles") for more information.

5. Run the server and make a search request to test the configuration.

Other relevant notes:
* When a user attempts to access a protected resource for the first time, Tomcat will call the `authenticate()` method of this Realm. Once a user has been authenticated, the user (and his or her associated roles) are cached within Tomcat for the duration of the user's login, that means until the user closes their browser. The cached user is not saved and restored across sessions serialisations. Any changes to the database information for an already authenticated user will not be reflected until the next time that user logs on again.
* Administrating the information in the users and user roles table is the responsibility of your own implementation. 

# Where to go next

The next document in the _User Authentication_ section is [Response Privacy](response-privacy.html "Response Privacy Configuration").

