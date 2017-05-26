---
title: Data Access Layer
---

# Data Access Layer

As stated [here](intro.html#what-is-red-dog), your implementation of the data access API will wrap whatever data storage you have to the RDAP server.

To create your own implementation, you need to provide the following classes:

1. At least one DAO ([Data-Access Object](https://en.wikipedia.org/wiki/Data_access_object)) class, whose methods will be queried by the rdap-server to retrieve RDAP objects from whatever data storage you are using. You only need to implement the DAOs that make sense for your business.
2. One implementation hub class, which will point the server to your DAOs. It must implement the [`mx.nic.rdap.db.spi.DataAccessImplementation`](https://github.com/NICMx/rdap-data-access-api/blob/v1.1.0/src/main/java/mx/nic/rdap/db/spi/DataAccessImplementation.java) interface.
3. (Optional; recommended) Your [service provider configuration file](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html#register-service-providers). (A file named `META-INF/services/mx.nic.rdap.db.spi.DataAccessImplementation` which contains the full name of your implementation hub class.)  
   The point of this file is to provide the server with a "default" pointer to your implementation. In other words, if your implementation is the only one in the classpath, this file will allow the server to find it without needing the user to configure it explicitly.

Every DAO will provide information regarding one specific object type. The available object types are ASNs, domains, entities, IP networks, nameservers and users. You can provide data access to each of these objects by implementing (respectively) the following interfaces:

1. [`mx.nic.rdap.db.spi.AutnumDAO`](https://github.com/NICMx/rdap-data-access-api/blob/v1.1.0/src/main/java/mx/nic/rdap/db/spi/AutnumDAO.java)
2. [`mx.nic.rdap.db.spi.DomainDAO`](https://github.com/NICMx/rdap-data-access-api/blob/v1.1.0/src/main/java/mx/nic/rdap/db/spi/DomainDAO.java)
3. [`mx.nic.rdap.db.spi.EntityDAO`](https://github.com/NICMx/rdap-data-access-api/blob/v1.1.0/src/main/java/mx/nic/rdap/db/spi/EntityDAO.java)
4. [`mx.nic.rdap.db.spi.IpNetworkDAO`](https://github.com/NICMx/rdap-data-access-api/blob/v1.1.0/src/main/java/mx/nic/rdap/db/spi/IpNetworkDAO.java)
5. [`mx.nic.rdap.db.spi.NameserverDAO`](https://github.com/NICMx/rdap-data-access-api/blob/v1.1.0/src/main/java/mx/nic/rdap/db/spi/NameserverDAO.java)
6. [`mx.nic.rdap.db.spi.RdapUserDAO`](https://github.com/NICMx/rdap-data-access-api/blob/v1.1.0/src/main/java/mx/nic/rdap/db/spi/RdapUserDAO.java)

Here you can find a simple sample implementation. See comments inline for details:

1. IP Network DAO: [`mx.nic.rdap.sample.SampleIpNetworkDaoImpl.java`](sample-code/SampleIpNetworkDaoImpl.java)
2. Hub class: [`mx.nic.rdap.sample.SampleHub.java`](sample-code/SampleHub.java)
3. Service provider configuration file: [`mx.nic.rdap.db.spi.DataAccessImplementation`](sample-code/mx.nic.rdap.db.spi.DataAccessImplementation)

You can download the full sample implementation (compiled into a jar file) [here](https://github.com/NICMx/releases/raw/master/RedDog/rdap-sample-daa-impl-{{ site.latest-sample-data-impl }}.jar). Instructions on how to append the custom implementation to the server can be found in the [next document](server-install-option-1.html).

