---
title: Server Installation
---

# Server Installation

The server is your typical servlet Java WAR; simply toss it into your favorite servlet container. The following is an improvised example on how you might do this in Ubuntu/Tomcat; if you favor other means or environments, knock yourself out.

## Install Java

	sudo apt-get install openjdk-8-jre

## Install Tomcat

	# I'm not using the Ubuntu repositories because their Tomcat is rather old.
	# You will probably need to adapt this link because it keeps changing.
	# See www-us.apache.org/dist/tomcat/tomcat-8
	wget www-us.apache.org/dist/tomcat/tomcat-8/v8.5.11/bin/apache-tomcat-8.5.11.zip
	unzip apache-tomcat-8.5.11.zip
	CATALINA_HOME=$(pwd)/apache-tomcat-8.5.11
	chmod +x $CATALINA_HOME/bin/*.sh
	JRE_HOME=/usr/lib/jvm/java-8-oracle/jre

## Add a dummy (but valid) data source on Tomcat

> ![Warning!](img/warning.svg) This step is only necessary due to an implementation oversight (a redundant dependency) that will be worked on in the near future. The database is actually never queried by Red Dog.
> 
> Once issue TODO is fixed, this step will no longer be required.

A relatively straightforward way to do this is to add an in-memory database so you don't actually need an SQL server.

Download the H2 driver and add it to Tomcat's classpath:

	# See http://www.h2database.com/html/download.html,
	# "Jar File" section.
	wget http://repo2.maven.org/maven2/com/h2database/h2/1.4.193/h2-1.4.193.jar
	mv h2-1.4.193.jar $CATALINA_HOME/lib

Add the following tag to `<GlobalNamingResources>` in `$CATALINA_HOME/conf/server.xml`:

	<Resource name="jdbc/rdap"
	    type="javax.sql.DataSource"
	    auth="Container"
	    driverClassName="org.h2.Driver"
	    url="jdbc:h2:mem:rdap"
	    username=""
	    password="" />

## Install Red Dog on Tomcat

	cd $CATALINA_HOME/webapps
	# www.reddog.mx/download.html
	wget www.reddog.mx/download/reddog-server.war

## Replace the default Data Access API for your own

> ![Warning!](img/warning.svg) You will notice that this step is rather convoluted. This is due to an implementation oversight that will be worked on in the near future.
> 
> `rdap-server` requires only one implementation to be present in the classpath. The fact that it ships with a default implementation makes this awkward.
> 
> In the future, `rdap-server` will allow multiple implementations in the classpath and will allow you to specify the active one via configuration.
> 
> See issue TODO.

Make sure Red Dog's WAR is expanded.

	$CATALINA_HOME/bin/startup.sh

We're going to be moving jars around, so make sure Tomcat is down.

	$CATALINA_HOME/bin/shutdown.sh

Replace the default implementation for your own.

	rm $CATALINA_HOME/webapps/rdap-server/lib/rdap-sql-provider.jar
	mv my-implementation.jar $CATALINA_HOME/webapps/rdap-server/lib

If your implementation requires configuration, now would be a good time to tweak it.

# Start Tomcat

	$CATALINA_HOME/bin/startup.sh

Your Red Dog server is now running and serving data provided by your implementation.

![Sample Firefox screenshot](img/index-html-firefox.jpg)

