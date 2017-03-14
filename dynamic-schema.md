---
title: Dynamic Schema
---

# Dynamic schema
## What do we call dynamic schema?

This feature is used to indice a set of database schemas where the most recent data will be store and read by the **migrator** and the **server**, respectively

## Why do we think that you need this dynamic schema?

The first time that you migrate your data with our RDAP-migrator tool to the Red Dog data base, it will be stored in the default schema named “RDAP”.As our RDAP-migrator tool performs only full migration ,it will erase all your data in your current Red Dog data base,then read your original data base and fill Red Dog data base,once again.We decide to implements this dynamic schema to avoid the lack of information on the server data base while the migrator do its job.

## How do dynamic schema works?

You need to provide some extra information to the RDAP-migrator tool and the Red Dog server **configuration.properties** files which is in the“config/” folder and “WEB-INF/” folder respectively. The need properties are the following:

| Property name | Type | Default value | Required | Use | Description |
|:-------------|:----|:-------------|:--------|:---|:-----------|
| is_dynamic_schema | Boolean | false | false | server and migrator | Indicates if the dynamic schema mode is active|
| schema | string | “rdap” | false | server and migrator | The main schema of the Red dog server |
| migration_schema | string | null | true | migrator | schema that the migrator will migrate the data if the previous “schema” is in use|
| schema_timer_time | long | null | true if is_dynamic_schema is true | server | Time in seconds when a thread will check if it is necessary to change schemas.|

> In Red Dog database schema, you will find a "configuration" table, it contains a property named **“schema”** that indicates the current schema that the Red Dog server is using for operation. After the migraton ends,this property will be updated with the name of the schema where the data was stored.

In order to enable dynamic schema feature in the RDAP-migrator tool and Red Dog server, it will be necessary to write a property in the **configuration.properties** file.The property name is **“is_dynamic_schema”**, the default value is false, you need to set it to **“true”**.

## RDAP-migrator
When you run RDAP-migrator with dynamic schema mode active, it will need two properties in the **configuration.properties** file. The properties are **“schema”** and **“migration_schema”**; these properties indicate the name of the two data base schemas that the RDAP-migrator tool will use to migrate the data. The **“schema”** property has a default value, so it is not necessary to provide that property but if you main schema name is different from “rdap”, you need to overwrite this value. 

Before the RDAP-migrator tool begins to migrate the data, it will check first the configuration table of the schema that was set on the **“schema”** property in the **configuration.properties** file. If the configuration table does not have any value in the **“schema”** row or is not equals than the schema property that was read it from the file, it will start migrate on the schema that was read from the properties file.

On the other hand,if the value of **“schema”** in the configuration table is equals to the value of **“schema”** in the properties file, the RDAP-migrator will migrate all the data to the schema set in **“migrator_schema”** property from the **configuration.properties** file.


## RDAP-server
In Red Dog server, the dynamic schema mode needs another property in the **configuration.properties** file, the property is named **“schema_timer_time”** and has no default value, is only required when dynamic schema mode is active. This property value is a number that represents seconds. This seconds is the time between the thread will see the configuration table and check if the schema name in the table is different from the schema name in use, if schema name changes, the thread changes all the SQL queries in order to do the request to the other schema that will have more recent data.
