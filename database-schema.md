---
title: Database Schema
---

# Setting up RDAP Database schema- MySQL

> **TODO** Me parece que este procedimiento tiene más pasos de los necesarios.
> 
> Si el usuario no va a modificar la base de datos directamente, ¿por qué le pedimos que corra dos scripts en lugar de uno?
> 
> No sería más limpio pedirle que ejecute un solo script y que eso ya deje la base de datos lista?

> **TODO** a como este documento está redactado, da la impresión de que estamos inicializando las tablas y los catálogos, pero no el esquema. Da la impresión de que estamos asumiendo de que el usuario ya tiene el esquema listo y solo falta generar las tablas dentro.
> 
> Sin embargo, el script "tables.sql" también genera el esquema.
> 
> Por lo tanto, el nombre "tables.sql" me parece incorrecto.

To facilitate the data migration from your old WHOIS server to a new RDAP server, in Red Dog we have defined a database table schema based on the old protocol, the Registry-Registrar relation and including the necessary adjustments to meet the new requirements. In this document we explain how to create the database; the procedure is the following:

> **TODO** ¿Qué significa la palabra "facilitate" en el párrafo anterior? Me parece una contradicción porque el resto del enunciado suena a que está hablando de algo complicado.

> **TODO** ¿A qué se refieren con "old protocol"? ¿Whois? Rescatar de Whois lo que sirve, ¿No se supone que era trabajo del RFC y no de nuestro diseño de BD?

1.	Run the Tables.sql script to create all the tables required.
2.	Run the Catalogs.sql script to fill the catalogs tables.

> **TODO** la palabra "catalog" significa algo que [*no* es lo que este documento sugiere](https://en.wikipedia.org/wiki/Database_catalog). Si realmente es necesario incluirlo, hay que reemplazarlo por algo más apropiado.

[![image1](img\tables-file.png)](https://github.com/NICMx/rdap-documentation/blob/master/database/catalogs.sql "SQL file with tables")
[![image2](img\catalogs-file.png)](https://github.com/NICMx/rdap-documentation/blob/master/database/catalogs.sql "SQL file with catalogs")

> **TODO** el enlace de tables.sql arriba apunta hacia catalogs.sql.

If you want to know more about the Red Dog database definition, like table names, column types and more, you can check out [this document](database-tables-definition.html "Database Tables Definiton").

# Where to go next 

The next step is configuring the connection with the database that you just created. The configuration process is explained in [this document](datasource.html "Datasource configuration").




