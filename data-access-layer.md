---
title: Data Access Layer
---

# Data Access Layer

Within RDAP server projects package that you have downloaded, there is a project called **"rdap-sql-provider"**, this is our default implementation for the connection with our database implemented in MySQl, which we use as a Database server provider. In RedDog, we believe that some of you may want to use another database, with different table names and other rules, so we provide you a way to do it.

In general, the server must load a valid implementation of a data access layer, in other words, an appropiate project that extends from our **"rdap-db"** project and must add it to the classpath or, if you prefer to use Maven, to the project POM file.

Therefore, if you want to add your own data provider, you must implement each one of the following interfaces in your database project:

+	mx.nic.rdap.db.spi.AutnumSpi
+	mx.nic.rdap.db.spi.DomainSpi
+	mx.nic.rdap.db.spi.EntitySpi
+	mx.nic.rdap.db.spi.IpNetworkSpi
+	mx.nic.rdap.db.spi.NameserverSpi
+	mx.nic.rdap.db.spi.RdapUserSpi
+	mx.nic.rdap.db.spi.InitializerSpi

In addition, inside the **"META-INF/services"** server folder you must add a file called the same as each one of our interfaces, so the server can recognize your implementation as a service. These files must contain the full name of your interfaces implementation, in example, for an implementation for the **AutnumSpi** interface called **"MyCustomAutnumImp"** in the package **"com.example.rdap.provider"** there must  be a file in **"META-INF/services"** called **"mx.nic.rdap.db.spi.AutnumSpi"** with the text **"com.example.rdap.provider.MyCustomAutnumImp"** inside.

Each one of these interfaces define a basic set of functions, like gets and insert to database functions, that we think should help in the majority of cases, but if you don't want to implement an specific function, you can throw a **"NotImplementedException"** always, It will cause the server to send an **"http code 501""** error message.

Some restrictions about the implementations:

+	The RDAP must load only one data provider or it can throw a **"RuntimeException"**.

+	The implementation has to be thread-safe because the server will be used as a **Singleton**.

+	The **"InitializeSpi"** interface is optional to implement. The main purpose of this class is to send configuration info relevant for data access. If you prefer to avoid implementing this interface, you must omit the **META-INF/services/mx.nic.rdap.db.spi.InitializerSpi** file.

