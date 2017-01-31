---
title: Server Installation
---

# Server Installation

## Index

1. [Installation and Deployment](#installation-and-deployment)
2. [Configuration](#configuration)

## Installation and Deployment

There's not much to say here. The server is your typical servlet Java WAR using a MySQL database. Toss it into your favorite servlet container and watch the fireworks go. The following is an improvised sample on how you might do this; if you favor other means, knock yourself out.

<div class="codeblock-menu">
	<span class="code-selector-tab" onclick="showCodeBlock(this);">Tomcat</span>
	<span class="code-selector-tab" onclick="showCodeBlock(this);">Glassfish/Payara</span>
	<span class="code-selector-tab" onclick="showCodeBlock(this);">JBoss</span>
	<span class="code-selector-tab" onclick="showCodeBlock(this);">Jetty</span>
</div>

<!-- Tomcat -->
{% highlight bash %}
# Install and start MySQL if you haven't already.
sudo apt-get install mysql-server

# Install Tomcat if you haven't already.
# I'm not using the Ubuntu repositories because their Tomcat is rather old.
wget www-us.apache.org/dist/tomcat/tomcat-8/v8.5.9/bin/apache-tomcat-8.5.9.zip
unzip apache-tomcat-8.5.9.zip
CATALINA_HOME=$(pwd)/apache-tomcat-8.5.9
chmod +x $(CATALINA_HOME)/bin/*.sh
JRE_HOME=/usr/lib/jvm/java-8-oracle/jre

# Install the MySQL driver on Tomcat.
cd $CATALINA_HOME/lib
# https://dev.mysql.com/downloads/connector/j/
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.40.zip
unzip -j mysql-connector-java-5.1.40.zip mysql-connector-java-5.1.40/mysql-connector-java-5.1.40-bin.jar

# Install Red Dog on Tomcat.
cd $CATALINA_BASE/webapps
# www.reddog.mx/download.html
wget www.reddog.mx/download/reddog-server.war

# Start Tomcat.
$CATALINA_HOME/bin/startup.sh
{% endhighlight %}

<!-- Glassfish/Payara -->
{% highlight bash %}
unzip payara-4.1.1.164.zip
cp mysql-connector-java-5.1.39-bin.jar payara41/glassfish/domains/domain1/lib	
payara41/bin/asadmin start-domain

payara41/bin/asadmin create-jdbc-connection-pool --datasourceclassname com.mysql.jdbc.jdbc2.optional.MysqlDataSource --restype javax.sql.DataSource --property user=client:password=cSK2Qv8McT7rHvsh:url="jdbc\:mysql\://200.34.22.164/rdap" RdapPool
payara41/bin/asadmin create-jdbc-resource --connectionpoolid RdapPool jdbc/rdap
payara41/bin/asadmin ping-connection-pool RdapPool

payara41/bin/asadmin deploy ~/Downloads/rdap.war

...

payara41/bin/asadmin undeploy rdap
payara41/bin/asadmin stop-domain
{% endhighlight %}

<!-- Jboss -->
{% highlight bash %}
TODO
{% endhighlight %}

<!-- Jetty -->
{% highlight bash %}
TODO
{% endhighlight %}

Your Red Dog server is now running.

![TODO - Sample Firefox screenshot](Sample)

It doesn't yet have any information to serve, though. Keep reading to learn how to configure it.

## Configuration

Blah blah blah server.xml and context.xml.

The next step is to [populate the database](migration.html).

