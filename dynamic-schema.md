---
title: Dynamic Schema
---

# Dynamic schema
## What do we call dynamic schema?

In the **server**, to the ability to know, every certain period, if exists a new schema with more recent data, and switch to that schema.

In the **migrator**, to the ability to store data in another Red Dog schema that are not used by an application.

## Why do we think that you need this dynamic schema?

The first time that you migrate your data with our RDAP-migrator tool to the Red Dog DB, it will be stored in the default schema named “RDAP”. 
As our RDAP-migrator tool performs only full migration, what will happen if you run it once again?, It will erase all your data in your current Red Dog DB and migrate your data to the Red Dog DB once again. What will happen with your server? You will don’t have any information to respond to the users request because the RDAP-migrator erase all the data, and your users will have to wait until you finish your migration to respond with more recent data. That is the reason to we decide to implements this dynamic schema.

## How do dynamic schema works?

You need to provide some extra configuration in RDAP-migrator tool and Red Dog server, these configurations are write in the **configuration.properties** files (“config/” folder and “WEB-INF/” folder respectively). 

Here is a table with a description of the needed properties

| Property name | Type | Default value | Required | Use | Description |
|:-------------|:----|:-------------|:--------|:---|:-----------|
| is_dynamic_schema | Boolean | false | false | server and migrator | Indicates if the dynamic schema mode is active|
| schema | string | “rdap” | false | server and migrator | The main schema of the Red dog server |
| migration_schema | string | null | true | migrator | schema that the migrator will migrate the data if the previous “schema” is in use|
| schema_timer_time | long | null | true if is_dynamic_schema is true | server | Time in seconds when a thread will check if it is necessary to change schemas.|

It will exist a configuration table for each schema that **Red Dog Server** and **RDAP-migrator** tool will use. The purpose of the table is to add configuration of our RDAP-SQL-provider, for **dynamic schema mode** purposes it contains a property named **“schema”** that indicates the current schema that the Red Dog server is using for operation.

To enable dynamic schema mode in the RDAP-migrator tool and Red Dog server, it will be necessary to write a property in the **configuration.properties** file. The property name is **“is_dynamic_schema”**, the default value is false, you need to set it to **“true”**.

When you run RDAP-migrator with dynamic schema mode active, it will need two properties in the **configuration.properties** file. The properties are **“schema”** and **“migration_schema”**; both properties indicate the name of the two DB schemas that the RDAP-migrator tool will use to migrate the data. The **“schema”** property has a default value, so it is not necessary to provide that property but if you main schema name is different from “rdap”, you need to overwrite this value. 

Before the RDAP-migrator tool begins to migrate the data, it will check in which schema the RDAP-migrator has to migrate the data. 
The behavior is the RDAP-migrator will check first the configuration table of the schema that was set on the **“schema”** property in the **configuration.properties** file. If the configuration table does not have any value in the **“schema”** row or is not equals than the schema property that was read it from the file, it will start migrate on the schema that was read from the properties file.

If the value of **“schema”** in the configuration table is equals to the value of **“schema”** in the properties file, the RDAP-migrator will migrate all the data to the schema set in **“migrator_schema”** property from the **configuration.properties** file.

At the end, the RDAP-migrator will write or overwrite the value for **“schema”** in the configuration table of all schemas it knows (**“schema”** and **“migration_Schema”** properties). The value that will be write is the schema that the RDAP-migrator migrate the recent data.

In Red Dog server, the dynamic schema mode needs another property in the **configuration.properties** file, the property is named **“schema_timer_time”** and has no default value, is only required when dynamic schema mode is active. The value in property **“schema_timer_time”** is a number that represents seconds. This seconds is the time between the thread will see the configuration table and check if the schema name in the table is different from the schema name in use, if schema name changes, the thread changes all the SQL queries in order to do the request to the other schema that will have more recent data.
