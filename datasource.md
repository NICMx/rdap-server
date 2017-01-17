---
title: Configuring Datasource
---

# Configuring a Datasource for _RedDog_ Server in Apache Tomcat

This document tells you how to set up a datasource connection for the _RedDog_ server.
1.	Create the **WEB-INF/content.xml** file in your installation directory.
2.	Add the following lines:
 
        <?xml version="1.0" encoding="UTF-8"?>
        <Context antiJARLocking="true" path="/rdap">
        
        <Resource name="jdbc/rdap" type="javax.sql.DataSource" auth="Container"
        driverClassName="<mydb_driver_class_name>" url="<mydb_url>"
        username="<mydb_user>" password="<mydb_pass>" validationQuery="select 1 from dual" />	
        </Context> 
        
3.	Replace _<mydb\_user>_ and _<mydb\_pass>_ with your actual database credentials.

4.	Replace _<mydb\_url>_ with the URL for your Database.For example:  
a.	**A localhost mysql database:** jdbc:mysql://localhost  
b.	**A remote mysql database:** jdbc:mysql://exampledb.com/rdap  
c.	**A remote Oracle database:** jdbc:oracle:thin:@example.mydb.com:1521:rdapdb  
  
5.	Replace _<mydb\_driver\_class\_name>_ with the Java class name of the JDBC drive to be use. For example:  
a.	**Oracle:** oracle.jdbc.OracleDriver  
b.	**MySql:** com.mysql.jdbc.Driver  

6.	Run the server and test the connection.
See the [Apache Tomcat 8 Datasource documentation] for more information.
Note:  
•	The validationQuery _“select 1 from dual”_ could not work in other Database installations, like SqlServer, so you would have to specify an appropriate validationQuery.  
•	In order to successfully  run the server, you must configure the server search request as explained in [this file].

[this file]: search-request.html "Search request configuration"
[Apache Tomcat 8 Datasource documentation]: http://tomcat.apache.org/tomcat-8.0-doc/jndi-datasource-examples-howto.html "Apache Tomcat"

