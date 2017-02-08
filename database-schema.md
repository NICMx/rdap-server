---
title: Database Schema
---

# Setting up RDAP Database schema- MySQL

To help you on the migration from your old WHOIS server to a new RDAP server, in Red Dog we have defined a database table schema based on the traditional Registry-Registrar relation and including the necessary adjustments to meet the new requirements. In this document we explain how to create the database; the procedure is the following:

1.	Run the database.sql file, this script will create the schema, database tables and inserts constants data used by the application, like country codes, event types, etc.

[![image1](img\database-file.png)](https://github.com/NICMx/rdap-documentation/blob/master/database/Database.sql "SQL file with tables")


If you want to know more about the Red Dog database definition, like table names, column types and more, you can check out [this document](database-tables-definition.html "Database Tables Definiton").

# Where to go next 

The next step is configuring the connection with the database that you just created. The configuration process is explained in [this document](datasource.html "Datasource configuration").




