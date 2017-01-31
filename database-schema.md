---
title: Database Schema
---

# Setting up RDAP Database schema- MySQL

To facilitate the data migration from your old WHOIS server to a new RDAP server, in Red Dog we have defined a database table schema based on the old protocol, the Registry-Registrar relation and including the necessary adjustments to meet the new requirements. In this document we explain how to create the database; the procedure is the following:

1.	Run the Tables.sql script to create all the tables required.
2.	Run the Catalogs.sql script to fill the catalogs tables.

[![image1](img\tables-file.png)](https://github.com/NICMx/rdap-documentation/blob/master/database/catalogs.sql "SQL file with tables")
[![image2](img\catalogs-file.png)](https://github.com/NICMx/rdap-documentation/blob/master/database/catalogs.sql "SQL file with catalogs")

If you want to know more about the Red Dog database definition, like table names, column types and more, you can check out [this document](database-tables-definition.html "Database Tables Definiton").

# Where to go next 

The next step is configuring the connection with the database that you just created. The configuration process is explained in [this document](datasource.html "Datasource configuration").




