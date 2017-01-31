---
title: Configuring Datasource
---

# Configuring a Datasource for _Red Dog_ Server in Apache Tomcat

Now that wwe already have created the database, as explained in this previous [document](database-schema.html "Database Schema"), we have to configure the connection between that database and your RDAP server, the procedure is the following:

1.	Create the **META-INF/content.xml** file in the directory where you extract the project. In Windows, it should look  like this.

	![DATASOURCE PATH](img\datasource-path.png)

2.	Add the following lines:
 
        <?xml version="1.0" encoding="UTF-8"?>
        <Context antiJARLocking="true" path="/rdap">
	        <Resource 
			name="jdbc/rdap" 
			type="javax.sql.DataSource" 
			auth="Container"
			driverClassName="<mydb_driver_class_name>" 
			url="<mydb_url>"
			username="<mydb_user>" 
			password="<mydb_pass>" 
			validationQuery="select 1 from dual" />	
        </Context> 
        
3.	Replace `<mydb_user>` and `<mydb_pass>` with your actual database credentials.

4.	Replace `<mydb_url>` with the URL for your Database. For example:

	1.	**A localhost mysql database:** jdbc:mysql://localhost
	2.	**A remote mysql database:** jdbc:mysql://exampledb.com/rdap
	3.	**A remote Oracle database:** jdbc:oracle:thin:@example.mydb.com:1521:rdapdb
  
5.	Replace `<mydb_driver_class_name>` with the Java class name of the JDBC drive to be use. For example:

	1.	**Oracle:** oracle.jdbc.OracleDriver 
	2.	**MySql:** com.mysql.jdbc.Driver


6.	Run the server to test the configuration above.

	1.	If everything is okay, you will see a screen like this:

		![SERVER OK IMAGE](img\server-ok-image.png)

	2.	If something is wrong, you will see some error messages like this:

		![ERROR IMAGE](img\server-error-image.png)

Some common mistakes are usually misspelled username/passwords or wrong database urls, so watch out for this and you can always check some extra information in the [Apache Tomcat 8 Datasource documentation] page.

That's all for the database configuration.

Note:

+	The validationQuery “`select 1 from dual`” could not work in other Database installations, like SqlServer, so you would have to specify an appropriate validationQuery just to see if everything is fine with your connection.

# Where to go next

At this time, you have a fully functional RDAP server, now; let's look for some advance configuration for the server behavior in this [document](behavior-configuration.html "Behavior Configuration")

[Apache Tomcat 8 Datasource documentation]: http://tomcat.apache.org/tomcat-8.0-doc/jndi-datasource-examples-howto.html "Apache Tomcat"

