---
title: Configuring Datasource
---

# Configuring a Datasource for _Red Dog_ Server in Apache Tomcat

Now that wwe already have created the database, as explained in this previous [document](database-schema.html "Database Schema"), we have to configure the connection between that database and your RDAP server, the procedure is the following:

> **TODO** typo: "wwe"

1.	Create the **META-INF/content.xml** file in the directory where you extract the project. In Windows, it should look  like this.

> **TODO** "it should look like this" es una verdad a medias.
> 
> La imagen incluye archivos de Eclipse (Workspace, .settings, target, src, main, y quizá otros como test), los cuales el usuario no va a tener.
> 
> Deberían quitar estos archivos de la imagen, porque van a causar confusión.
> 
> Recomiendo que generen el WAR con Maven para saber cuáles archivos va a tener el usuario.

> **TODO** La expresión "In Windows" sobra; el workspace no se va a ver significativamente diferente en otros sistemas operativos.
>
> Adicionalmente, mencionar a Windows es de "mala educación". A mucha gente (que son un buen porcentaje de nuestros usuarios) le cae mal. Es como si se pusieran a hablar del Corán en un libro de física.
> 
> Pero más importantemente, no creo que mucha gente vaya a levantar al server en Windows. La mayoría de los servers en la vida real son Unix.

	![DATASOURCE PATH](img\datasource-path.png)

> **TODO** por favor no usen backslashes en directorios; esta documentación va a ser servida por un Unix.

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

> **TODO** Creo que deberían configurar las cosas en este orden: driver, URL, user, y al final password.
> 
> Siento que se vería más intuitivo porque se estaría yendo de general a particular.

6.	Run the server to test the configuration above.

	1.	If everything is okay, you will see a screen like this:

		![SERVER OK IMAGE](img\server-ok-image.png)

> **TODO** Por favor a acostúmbense a usar la palabra "TODO" cuando dejen algo pendiente.
> 
> De lo contrario se les va a olvidar.

	2.	If something is wrong, you will see some error messages like this:

		![ERROR IMAGE](img\server-error-image.png)

> **TODO** Aquí también falta un TODO.

Some common mistakes are usually misspelled username/passwords or wrong database urls, so watch out for this and you can always check some extra information in the [Apache Tomcat 8 Datasource documentation] page.

That's all for the database configuration.

Note:

+	The validationQuery “`select 1 from dual`” could not work in other Database installations, like SqlServer, so you would have to specify an appropriate validationQuery just to see if everything is fine with your connection.

# Where to go next

At this time, you have a fully functional RDAP server, now; let's look for some advance configuration for the server behavior in this [document](behavior-configuration.html "Behavior Configuration")

[Apache Tomcat 8 Datasource documentation]: http://tomcat.apache.org/tomcat-8.0-doc/jndi-datasource-examples-howto.html "Apache Tomcat"

