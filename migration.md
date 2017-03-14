---
title: Migration
---

# **UNDER DEVELOPMENT**

# Conventions used in this document:
  
The application: Red Dog migration tool.

Main objects: Entity, Domain, Nameserver, IpNetwork, Autonomous System number.

Common objects: Events, Remarks, Links.

RDAP objects: The main objects and the common objects.


# Red Dog migration tool

The purpose of this document is help you in the data migration process from your Whois server to the Red Dog RDAP server.

In the following sections we explain the attributes of each RDAP objects, as defined in the RFC7483, and how is mapped to the Red Dog RDAP database. Red Dog’s database uses a relational database design, so the main objects are generally distributed in more than one table.

# Dynamic Schema mode

We know that full migration can take a long time, and may affect the second time you want to migrate your data to update them. In order not to affect the operation of the active schema we have decided to add a "dynamic schema" mode to the migrator, in order to migrate the information in up to two different schemas in the database, for more information go to the next page [Dynamic schema](dynamic-schema.html).

# Migration process

The process of migration using the Red Dog migration tool is:
1.	You build the “select” statements with exactly the same types that we define.
2.	The application reads the SQL file for each main object.
3.	The application executes that SELECT statements in your database.
4.	The application maps the resultSet of the statements to RDAP objects and validate the data types.
5.	The application stores the objects in the Red Dog RDAP database.

Some relevant points:
1.	The id requested in the querys are only to associated the objects that are going to be stored in the database; The Red Dog RDAP database assign automatic ids to the main objects.
2.	The common objects that belongs to the main objects are optional.

> The migrator will commit the changes in the database only if all the statements are successfully executed, if something fails between the execution, the migrator will stop and will do a rollback to this changes.

# Configuration

This section tells you how to set up the batch configuration and the database connections.

1.	Configure the batch behavior. Find the **config/configuration.properties** file in your installation directory, it contains the following lines:

        #Optional. Boolean value to activate the migration of users. Default value=false
        migrate_users =
        #Optional. Boolean value to indicate if the nameserver is used as a Domain Attribute. Default value = 	false (is used as object)
        nameserver_as_domain_attribute =
		#Optional. Name of the default schema of the rdap database. Default value = rdap
		default_schema =
		#Required. Name of the schema that will contain the updated data if the previous schema is actually used.
		migration_schema =
		#Optional. Indicates if the application will use 2 schemas. Default value = false
		dynamic_schema =

 

2.	Configure the origin database connection, which is the Database containing the information that will be migrated to the RDAP database. Find the **config/origin_database.properties** file, it contains the following lines:
 
        #The java class name of the JDBC drive to be use in the connection
        driverClassName=<mydb_driver_class_name> 
        #The url for your database
        url= <mydb_url> 
        #Your database credentials. We recommend create an user for the migrator with only select grants.
        userName=<mydb_user> 
        password=<mydb_pass>

3.	Replace _<mydb\_user>_ and _<mydb\_pass>_ with your actual database credentials.

4.	Replace _<mydb\_url>_ with the URL for your Database. For example:

	1.	**A localhost mysql database**: jdbc:mysql://localhost
	2.	**A remote mysql database**: jdbc:mysql://exampledb.com/mydb
	3.	**A remote Oracle database**: jdbc:oracle:thin:@example.mydb.com:1521:db

5.	Replace `<mydb_driver_class_name>` with the Java class name of the JDBC drive to be use. For example:

	1.	**Oracle**: oracle.jdbc.OracleDriver
	2.	**MySql**: com.mysql.jdbc.Driver

6.	Configure the destination database connection, which is the Database that will contain the information that will be migrated from the origin database. Find the **config/destination_database.properties** file, it contains the same structure from the **origin_database.properties** file, so you have to repeat steps  3 to 6 using the data for the rdap database. 
7.	Create the file that will contains the selects statements for the migration. Create the **config/migration.sql** with the following structure:

        #user
        SELECT usersData FROM yourUserTable;
        
        #entity
        SELECT entitiesData FROM yourEntityTable;
        
        #nameserver
        SELECT nameserversData FROM yourNameserverTable;
        
        #domain
        SELECT domainsData FROM yourDomainData;
        
        #autnum
        SELECT autnumsData FROM yourAutnumsData;

        #ip_network
        SELECT ipNetworksData FROM yourIpNetworkData;

8.	Replace the select statements with the correct statements, which must have the structure defined in the following sections.

You can test the configuration of the batch using the samples of select statements defined on the following sections or using the META-INF/sample.migration.sql file in your installation directory.

# Objects

### Users
This object class represents an Entity that has authority to use the server.


| Attribute| Type  | Description|
| -------- |-------| :----------|
|Id        | Long | A unique identifier assigned by the RDAP database |
|name      |String|A string representing an unique name for the user|
pass|	String|	Password used for user’s login|
maxSearchResults|	Integer|	Max number of results for the user’s searches|
userRole|	userRoleDAO|	An object containing the role defined for the user. Different from catalog Rol|

### Migration
The migration of users is an optional process and can be setup in the configuration file for the migrator. To migrate your user’s data from your database to RDAP database you have to provide us the information in a single SELECT statement, like the following one:
        
    SELECT rus_name, rus_pass, rus_max_search_results, rur_name FROM myDB.users;

The following table describe each value of the select statement above:

|Column name   |    Required   |    Description   |   Example|
| :----------- |:--------------| :----------------| :--------|
rus_name|	Yes|	A string representing an unique name for the user |	Myuserusedforlogin1|
rus_pass|	Yes|	Password used for user’s login|	ads45asd1cxa|
rus_max_search_results|	No|	The number of max searches results for the defined user.|	5|
rur_name|	Yes|	String containing the role assigned for the user when logged in.|	AUTHENTICATED|

Example Select statement:

    SELECT "Myuserusedforlogin1" AS rus_name ,”ads45asd1cxa” AS rus_pass ,1 AS rus_max_search_results, “AUTHENTICATED” AS rur_name FROM dual;

>You must verify that roles assigned to user have the access that you want them to have. Check out the web.xml file in the WEB-INF folder on the server

### Entity

This object class represents the information of organizations, corporations, governments, non-profits, clubs, individual persons, and informal groups of people.

|Attribute|	Type   |	Description|
|:-------|:------:|----------------|
|id	|Long|	A unique identifier assigned by the RDAP database|
handle	|String|	A string representing a registry unique identifier of the entity|
vCard|	Vcard|	A jCard with the entity’s contact information|
publicIds|	List\<PublicId\>|	An array of Public Id objects |
roles|	List\<Rol\>|	An array of Rol objects|
entities|	List\<Entity\>|	An array of Entity objects|
status|	List\<Status\>|	An array of Status objects|
remarks	|List\<Remark\>|	An array of Remarks objects |
links|	List\<Link\>|	An array of Links objects |
port43|	String|	a string containing the fully qualified host name or IP address of the WHOIS server where the containing object instance may be found|
events|	List\<Event\>	|An array of Events objects |

### Migration
To migrate your data from your database to RDAP database you have to provide us the information in a single SELECT statement, like the following one:

    SELECT handle, port43, rdap_status, epp_status, events, entities, public_ids, vcard FROM myDB.entities;

The following table describe each value of the select statement above:

|Column name|	Required|	Description|	Example|
|-----------|:---------:|--------------|-----------|
|handle	|Yes|	The entity’s id assigned in your database|	XXXX|
|port43	|Yes|	The host or ip address of the WHOIS server where the entity instance may be found|	whois.example.net|
|rdap_status|	No	|A String containing the status list of the entity. The structure must have the form: <br /> “rdap_status, rdap_status, …”  [(See  Status section)]|active,validate|
|epp_status|	No|	String containing the status list of the entity. The structure must have the form: <br /> “epp_status, epp_status, …” [(See Status section)]|linked,ok|
|events|	No|	String containing the event list of the entity. The structure must have the form:“eventData1, eventData2”[(See EventData section)]  | registration\| 2011-12-31T23:59:59Z\| XXX1, reregistration: 2012-12-01T23:59:59Z: XXX1
|entitites	|No	|String containing the entities list of the entity. The structure must have the form:<br /> “entityData1, entityData2” [(See EntityData section)] | XXX1\|registrar, XXX2\|reseller
|public_ids	|No|	String containing the public id list of the entity. The structure must have the form: <br /> “publicIdData1,publicIdData2” [(See PublicIdData section)] | 1\|IANA Registrar ID, 2\|NIC ID | 
|vcard|	No|	String containing the vCard of the entity. The structure must have the form defined in the section [VCardData] | Joe Jobs \|Orange \|www.orange.mx \|jj@orange.mx \|81 8818181 \|81 8181818181 \|248.697.0908 \|Engineer \|local \|Mexico \|Monterrey \|Nuevo Leon \|Altavista \|100 \|Av. Luis Elizondo \|64000 |

Example Select statement:

    SELECT "XXXX" AS handle ,"whois.example.net" AS port43 ,"active,validate" AS rdap_status ,"linked,ok" AS epp_status ,"registration| 2011-12-31T23:59:59Z| XXX1, reregistration| 2012-12-01T23:59:59Z | XXX1" AS events ,"XXX1 |registrar, XXX2 |reseller" AS entitites ,"1|IANA Registrar ID, 2|NIC ID " AS public_ids ,"Joe Jobs |Orange |www.orange.mx |jj@orange.mx |81 8818181 |81 8181818181 |248.697.0908 |Engineer |local |Mexico |Monterrey |Nuevo Leon |Altavista |100 |Av. Luis Elizondo |64000" AS vcard FROM dual;
    
### Nameserver
The nameserver object class represents information regarding DNS nameservers used in both forward and reverse DNS.

The attributes of the nameserver object are:

|Attribute	|Type	|Description|
|-----------|-------|-----------|
|id|	Long|	A unique identifier assigned by the RDAP database|
|handle	|String|	A string representing a registry unique identifier of the nameserver|
|ldhName|	String	|A string containing the LDH (letters, digits, hyphen) name of the nameserver|
|unicodeName|	String|	A string containing a DNS Unicode name of the nameserver|
|ipAddresses|	NameserverIpAddressesStruct|	An object containing the following members:<br/>**ipv6Adresses**: an array of IpAddress object containing IPv6 addresses of the nameserver<br/>**ipv4Adresses**: an array of IpAddress object containing IPv4 addresses of the nameserver|
|entities	|List\<Entity\>|	An array of Entity objects |
|status|	List\<Status\>|	An array of Status objects|
|remarks|	List\<Remark\>|	An array of Remarks objects |
|links|	List\<Link\>|	An array of Links objects |
|port43|	String|	A string containing the fully qualified host name or IP address of the WHOIS server where the containing object instance may be found|
|events	|List\<Event\>|	An array of Events objects |

### Migration
To migrate your data from your database to RDAP database you have to provide us the information in a single SELECT statement, like the following one:

    SELECT handle, ldh_name, port43, rdap_status, epp_status, events,ip_addresses,entities FROM myDB.nameserver;

The following table describe each value of the select statement above:

|Column name	|Required|	Description	|Example|
|---------------|:------:|--------------|-------|
|handle	|Yes|	The nameserver’s id assigned in your database|	XXXX|
|ldh_name|	Yes|	The nameserver's name in ldh (letters, digits and hyphen) form or Unicode. |	ns1.xn--fo-5ja.example|
|port43	|Yes|	The host or ip address of the WHOIS server where the nameserver instance may be found|	whois.example.net|
|rdap_status|	No	|A String containing the status list of the nameserver. The structure must have the form: <br /> “rdap_status, rdap_status, …”  [(See  Status section)] |active,validate|
|epp_status|	No|	String containing the status list of the nameserver. The structure must have the form: <br /> “epp_status, epp_status, …” [(See Status section)] |linked,ok|
|events|	No|	String containing the event list of the entity. The structure must have the form:“eventData1, eventData2”[(See EventData section)] | registration\| 2011-12-31T23:59:59Z\| XXX1, reregistration: 2012-12-01T23:59:59Z: XXX1
|ip_addresses|	No	|String containing the ip addresses of the nameserver. The structure must have the form: <br /> “ipAddressData1, ipAddressData2” [(See IpAddressData section)] | 4\| 192.0.2.1, 6\| 2001:db8::2:1
|entitites	|No	|String containing the entities list of the entity. The structure must have the form:<br /> “entityData1, entityData2” [(See EntityData section)] | XXX1\|registrar, XXX2\|reseller


Example Select statement:

    SELECT "XXXX" AS handle,"ns1.xn--fo-5ja.example" AS ldh_name ,"whois.example.net" AS port43 ,"active,validate" AS rdap_status ,"linked,ok" AS epp_status ,"registration| 2011-12-31T23:59:59Z| XXX1, reregistration| 2012-12-01T23:59:59Z| XXX1" AS events ,"4| 192.0.2.1, 6| 2001:db8::2:1" AS ip_addresses ,"XXX1|registrar, XXX2|reseller" AS entitites FROM  dual;

### Domain

>The zones will be created auntomatically within the domain migration process. When a new zone is found, it will be created. E.g., if the domains “mydomain.com”, “mydomain2.lat”, “mydomain3.mx” and “mydomain4.com” will be migrated, the zones created are “com”, “lat” and “mx”. Check out the configuration file in the META-INF folder on the server for setup the managed zones.

The domain object class represents a DNS name and point of delegation. For RIRs, these delegation points are in the reverse DNS tree, whereas for DNRs, these delegation points are in the forward DNS tree.

The attributes of the Domain object are:

|Attribute	|Type	|Description|
|-----------|-------|-----------|
|id|	Long|	A unique identifier assigned by the RDAP database|
|handle|	String|	A string representing a registry unique identifier of the domain|
|ldhName|	String|	A string containing the LDH (letters, digits, hyphen) name of the domain.|
|unicodeName|	String|	A string containing a DNS Unicode name of the domain|
|variants|	List\<Variant\>	|An array of objects, each containing the following values:<br />**relation**: an array of strings, with each string denoting the relationship between the variants and the containing domain object.<br />**idnTable**: the name of the Internationalized Domain Name (IDN) table of codepoints.<br />**variantNames**: an array of objects, with each object containing an "ldhName"  member and a "unicodeName" member|
|nameservers|	List\<Nameserver\>|	An array of Nameserver objects|
|secureDNS|	SecureDNS|	An object that represents secure DNS information about domain names|
|entities|	List\<Entity\>|	An array of Entity objects |
|status|	List\<Status\>|	An array of Status objects|
|publicIds|	List\<PublicId\>|	An array of Public Id objects |
|remarks|	List\<Remark\>|	An array of Remarks objects |
|links|	List\<Link\>|	An array of Links objects |
|port43|	String|	A string containing the fully qualified host name or IP address of the WHOIS server where the containing object instance may be found|
|events|	List\<Event\>	|An array of Events objects |
|network|	IpNetwork|	An IP network object|

### Migration
To migrate your data from your database to RDAP database you have to provide us the information in a single SELECT statement, like the following one:

    SELECT handle, ldh_name, port43, rdap_status, epp_status, events, entities, variants, namerservers, secureDNS, dsData,keyData, publicIds FROM myDB.domain;

>Some Registries, use the nameserver data as a domain attribute; if you want to operate this way, you must active the 'nameserver.as.domain.attribute' is 'true', the 'host' value is expected, otherwise the nameservers value.

The following table describe each value of the select statement above:

|Column name	|Required	|Description	|Example|
|---------------|:---------:|---------------|-------|
|handle	|Yes|	The domain’s id assigned in your database|	XXXX|
|ldh_name|	Yes	|The domain’s name in ldh (letters, digits and hyphen) form or Unicode.| 	0.2.192.in-addr.arpa|
|port43	|Yes	|The host or ip address of the WHOIS server where the domain instance may be found|	whois.example.net|
|rdap_status|	No|	A String containing the status list of the domain. The structure must have the form: <br /> “rdap_status, rdap_status, …”  [(See Status section)] | active,validate|
|epp_status|	No|	String containing the status list of the domain. The structure must have the form: <br /> “epp_status, epp_status, …” [(See Status section)]  | linked,ok |
|events	|No	|String containing the status list of the domain. The structure must have the form: <br /> “eventData1, eventData2” [(See EventData section)] | registration\| 2011-12-31T23:59:59Z \| XXX1, reregistration\| 2012-12-01T23:59:59Z\| XXX1|
|entities|	No	|String containing the entities list of the domain. The structure must have the form: <br /> “entityData1, entityData2” [(See EntityData section)] | XXX1\|registrar, XXX2\|reseller     |
|variants|	No|	String containing the variants list of the domain. The structure must have the form: “variantData1, variantData2” [(See VariantData section)] | .EXAMPLE Spanish \|{xn--fo-cka.example, xn--fo-fka.example} \|{unregistered, registration_restricted} |
|nameservers|	No|	String containing the nameserver list of the domain. The structure must have the form: <br /> “nameserver1Handle, nameserver2Handle” |	XXX1,XXX2|
|host|No|String containing the host list of the domain. The structure must have the form: "hostname\|(ip1\|ip2...), hostname2\|(ip1\|ip2...)" | My-ns-1.mx\|(127.0.0.1),My-ns-2.mx\|(127.0.0.2\|2001:db8::2:1)|
|secure_dns|	No|	String containing the secureDNS data of the domain. [(See SecureDNSData section)] | true\|true\|12345 | 
|ds_data|	No|	String containing the dsData list of the secureDNS. The structure must have the form: <br /> “dsData1, dsData2” [(See DSData section)] | 12345\|3\|49FD46E6C4B45C55D4AC\|1 |
|key_data|	No|	String containing the keyData list of the secureDNS. The structure must have the form: <br /> “keyData1, keyData2”  [(See KeyData section)] | 257\|3\|1\|AQPJ///4Q== |
|public_ids|	No|	String containing the public id list of the domain. The structure must have the form: <br /> “publicIdData1,publicIdData2” [(See PublicIdData section)] | 1\|IANA Registrar ID, 2\|NIC ID 
|ip_network|	No| String containing the handle of the IP network object. Just one handle per domain | ipNetHandleX1

Example Select statement:

    SELECT "XXXX" AS handle,"0.2.192.in-addr.arpa" AS ldh_name, "whois.example.net" AS port43,"active,validate" AS rdap_status, "linked,ok" AS epp_status, "registration| 2011-12-31T23:59:59Z | XXX1, reregistration| 2012-12-01T23:59:59Z| XXX1" AS events, "XXX1|registrar, XXX2|reseller" AS entities, ".EXAMPLE Spanish |{xn--fo-cka.example, xn--fo-fka.example} |{unregistered, registration_restricted}" AS variants,  "XXX1,XXX2" AS namerservers, "true|true|12345" AS secure_dns, "12345|3|49FD46E6C4B45C55D4AC|1" AS ds_data,"257|3|1|AQPJ///4Q== " as key_data, "1|IANA Registrar ID, 2|NIC ID " AS public_ids, "ipNetHandleX1" as ip_network FROM dual;
    
### Autonomous System Number

This object class models Autonomous System number registrations found in RIRs.

|Attribute|	Type|	Description|
|---------|-----|--------------|
|id	|Long|	A unique identifier assigned by the RDAP database|
|handle	|String|	A string representing a registry unique identifier of the entity|
|startAutnum|	Long|	A number representing the starting number in the block of Autonomous System numbers|
|endAutnum	|Long|	A number representing the ending number in the block of Autonomous System numbers|
|name|	String|	An identifier assigned to the autnum registration by the registration holder|
|type|	String|	A string containing an RIR-specific classification of the autnum|
|status|	List\<Status\>|	An array of Status objects|
|country|	Integer|	An integer containing the id of the country code of the autnum|
|entities|	List\<Entity\>|	An array of Entity objects |
|remarks|	List\<Remark\>|	An array of Remarks objects |
|links|	List\<Link\>|	An array of Links objects |
|port43|	String|	A string containing the fully qualified host name or IP address of the WHOIS server where the containing object instance may be found.|
|events|	List\<Event\>|	An array of Events objects |

### Migration

To migrate your data from your database to RDAP database you have to provide us the information in a single SELECT statement, like the following one:

    SELECT handle, startAutnum, endAutnum, country, name, type, port43, rdap_status, events, entities FROM myDB.autnums;

The following table describe each value of the select statement above:

|Column name|	Required|	Description|	Example|
|-----------|:---------:|--------------|-----------|
|handle	|Yes|	The domain’s id assigned in your database|	XXXX|
|startAutnum|	Yes|	A number representing the starting number in the block of Autonomous System numbers|	1|
|endAutnum	|Yes|	A number representing the ending number in the block of Autonomous System numbers|	10|
|country|	Yes|	An integer containing the id of the country code of the autnum. [(See Country code section)] | 484 |
|name|	No|	An identifier given to the autnum registration by the registration holder|	myasn|
|type|	No	|a string containing an RIR-specific classification of the autnum|	myasntype|
|port43|	Yes|	The host or ip address of the WHOIS server where the domain instance may be found|	whois.example.net|
|rdap_status|	No|	A String containing the status list of the domain. The structure must have the form: <br /> “rdap_status, rdap_status, …”  [(See Status section)] | active,validate |
|events|	No|	String containing the status list of the domain. The structure must have the form: <br /> “eventData1, eventData2” [(See EventData section)] | registration\| 2011-12-31T23:59:59Z \| XXX1, reregistration\| 2012-12-01T23:59:59Z\| XXX1 |
|entities|	No|	String containing the entities list of the domain. The structure must have the form: <br /> “entityData1, entityData2” [(See EntityData section)] | XXX1\|registrar, XXX2\|reseller |

Example Select statement:

    SELECT "XXXX" AS handle, 1 AS startAutnum, 2 AS endAutnum,"myasn" AS name, "myasntype" AS type, "whois.example.net" AS port43,"active,validate" AS rdap_status, "registration| 2011-12-31T23:59:59Z | XXX1, reregistration| 2012-12-01T23:59:59Z| XXX1" AS events, "XXX1|registrar, XXX2|reseller" AS entities FROM dual;
    
### IP Network

This object class models IP network registrations found in RIRs and contains information about the network registration and entities related to the IP network.

|Attribute|	Type|	Description|
|---------|-----|--------------|
|id	|Long|	A unique identifier assigned by the RDAP database|
|handle|	String|	A String representing a registry unique identifier of the entity|
|ipVersion|	IpVersion|	An Enum containing the type of the ip: 4 or 6|
|startAddress|	InetAddress|	The starting IP address of the network, either IPv4 or IPv6|
|endAddress|	InetAddress	|The ending IP address of the network, either IPv4 or IPv6|
|name|	String|	An identifier assigned to the network registration by the registration holder|
|type|	String|	A string containing an RIR-specific classification of the network|
|country|	Integer|	A string containing the two-character country code of the network|
|parentHandle|	String|	A String containing an RIR-unique identifier of the parent network of this network registration|
|cidr|	Integer	| An integer which defines a subnet size|
|status|	List\<Status\>	|An array of Status objects|
|entities|	List\<Entity\>|	An array of Entity objects |
|remarks|	List\<Remark\>|	An array of Remarks objects |
|Links|	List\<Link\>|	An array of Links objects |
|port43|	String|	a string containing the fully qualified host name or IP address of the WHOIS server where the containing object instance may be found.|
|events|	List\<Event\>|	An array of Events objects |

### Migration

To migrate your data from your database to RDAP database you have to provide us the information in a single SELECT statement, like the following one:

    SELECT handle, startAddress, endAddress, name, type, country, parentHandle, cidr, port43, rdap_status, events, entities FROM myDB.ipnetworks;

The following table describe each value of the select statement above:

|Column name|	Required|	Description|	Example|
|-----------|:---------:|--------------|-----------|
|handle|	Yes|	The domain’s id assigned in your database|	XXXX|
|start_address|	Yes	|The starting IP address of the network, either IPv4 or IPv6|	192.10.0.0
|end_address|	Yes|	The ending IP address of the network, either IPv4 or IPv6|	192.10.0.255
|name|No	|	An identifier assigned to the network registration by the registration holder	| ipName|
|type	|No|	A string containing an RIR-specific classification of the network	|
|country|Yes	|	An integer containing the id of the country code of the autnum. [(See Country code section)]  | 484|
|parent_handle| No |	A String containing an RIR-unique identifier of the parent network of this network registration| parentId |
|cidr|Yes	|	An integer containing the Classless Inter-Domain Routing| 24|
|port43|	Yes|	The host or ip address of the WHOIS server where the domain instance may be found|	whois.example.net|
|rdap_status|	No|	A String containing the status list of the domain. The structure must have the form: <br /> “rdap_status, rdap_status, …”  [(See Status section)] | active,validate |
|events	|No	|String containing the status list of the domain. The structure must have the form: <br /> “eventData1, eventData2” [(See EventData section)] | registration\| 2011-12-31T23:59:59Z \| XXX1, reregistration\| 2012-12-01T23:59:59Z\| XXX1 |
|entities|	No|	String containing the entities list of the domain. The structure must have the form: <br /> “entityData1, entityData2” [(See EntityData section)] | XXX1\|registrar, XXX2\|reseller |

Example Select statement:

    SELECT "ipNetHandleX1" AS handle, "192.168.1.0"  AS start_Address, "192.168.1.255 "  AS end_address,"some name" AS name, "nir-LAT" AS type, "4" as ip_version, "MX" AS country, "24" AS cidr, "whois.example.net" AS port43,"active,validated" AS rdap_status, "registration| 2011-12-31T23:59:59Z | XXX1, reregistration| 2012-12-01T23:59:59Z| XXX1" AS events, "XXXssXw|registrar" AS entities FROM dual;

### Data structures
Each of the RDAP main objects uses common data structures which are described in the following sections.

>If you don't want to migrate an optional attribute, you MUST leave the field empty and use the pipe structure.
>
>I.E.: In a structure “value1Required\|value2Optional\|value3Required” when you will not pass the “value2”, you should pass “value1Required\|\|value3Required”.

### EventData
This data structure represents events that have occurred on an instance of an object. Each event data must have the following form:

        “EventAction | eventDate | eventActor” 

The events attributes are described in the following table:

|Attribute|	Required|	Description|	Example|
|---------|:-------:|--------------|-----------|
|eventAction|	Yes	|The name of the event. <br /> [(See Events section)] | registration |
|eventDate|	Yes|	The time and date the event occurred|	2011-12-31T23:59:59Z|
|eventActor|	No|	Handle of the entity who did the event|	XXXX|

### IpaddressData
This data structure represents the ip addresses of a nameserver. Each ipAddressData must have the following form:

        “ipAddressType | IpAddress”
    
The ip address attributes are described in the following table:

|Attribute|	Required|	Description|	Example|
|---------|:-------:|--------------|-----------|
|ipAddressType|	Yes	|The type of the ip:4 or 6|	4|
|IpAddress|	Yes	|The address|	192.0.2.1|

### EntityData

This data structure represents the entities [(see Entity section)] and their roles [(see Rol section)] in reference with the containing object. Each entityData must have the following form:

        “handle | rol”
    
The entityData attributes are described in the following table:

|Attribute|	Required|	Description|	Example|
|---------|:-------:|--------------|-----------|
|handle|	Yes|	The entity’s handle|	XXXX|
|rol|	Yes	|The entity’s role|	registrar|

### PublicIdData

This data structure maps a public identifier to an object class. Each publicIdData must have the following form:

    “publicId  |  type”

The public ids attributes are described in the following table:

|Attribute	|Required	|Description	|Example|
|-----------|:---------:|---------------|-------|
|publicId|	Yes|	A public identifier of the type denoted by "type"|	1
|type|	Yes|	A string denoting the type of public identifier|	IANA Registrar ID|

### VCardData

This data structure represents contact information, such as postal addresses, email addresses, phone numbers and names of organizations and individuals. Each vCardData must have the following form:

    “name | companyName | companyUrl | email | voice | cellphone | fax | jobTitle | addressType | country | city | state | street1 | street2 | street3 | postalCode”

The vcard attributes are described in the following table:

|Attribute|	Required	|Description|	Example|
|---------|:-----------:|-----------|----------|
|name|	Yes	|Entity’s name|	Joe Jobs|
|companyName|	No|	Entity’s company name|	Orange|
|companyUrl|	No|	Entity’s company url|	www.orange.mx|
|email|	No|	Entity’s email|	jj@orange.mx|
|voice|	No|	Entity’s voice number|	81 8818181|
|cellphone|	No|	Entity’s cellphone|	81 8181818181|
|fax|	No|	Entity’s fax |	248.697.0908|
|jobTitle|	No|	Entity’s Job title|	Engineer|
|addressType	|No	|Entity’s address type (international or local)	|local|
|country	|No	|Entity’s address country|	Mexico|
|city	|No	|Entity’s address city	|Monterrey|
|state	|No	|Entity’s address state|	Nuevo Leon|
|street1	|No	|Entity’s address street |	Altavista|
|street2	|No	|Entity’s address street|	100|
|street3	|No|	Entity’s address street	Av. |Luis Elizondo|
|postalCode	|No|	Entity’s address postal code|	64000|

### VariantData

Each VariantData must have the following form:

    “idnTable | {variantName1,variantName2…}| {variantRelation1,variantRelation2…}”

The Variant attributes are described in the following table:

|Attribute|	Required	|Description	|Example|
|---------|:-----------:|---------------|-------|
|idnTable|	Yes	|The name of the Internationalized Domain Name  (IDN) table of codepoints|	.EXAMPLE Spanish|
|variantNames|	Yes|	A list of variant names	|xn--fo-cka.example, xn--fo-fka.example|
|variantRelations|	Yes	|A list of variant relations|	unregistered, registration_restricted|

### SecureDNSData

Each SecureDNSData must have the following form:

    “zoneSigned|delegationSigned|maxSigLife”

The SecureDNS attributes are described in the following table:

|Attribute|	Required|	Description	|Example|
|---------|:-------:|---------------|-------|
|zoneSigned	|No	|true if the zone has been signed, false otherwise|	true
|delegationSigned|	No|	true if there are DS records in the parent, false otherwise|	true|
|maxSigLife|	No|	the signature lifetime in seconds|	12345

### DsData
Each DSData must have the following form:

    “keyTag|algorithm|digest|digestType”

The DSData attributes are described in the following table:

|Attribute	|Required|	Description	|Example|
|-----------|:------:|--------------|-------|
|keyTag	|Yes|The key tag field of a DNS DS record|	12345|
|algorithm|Yes	|	An integer as specified by the algorithm field of a DNS DS record|	3|
|digest|	Yes|	A string specified by the digest field of a DNS DS record|	49FD46E6C4B45C55D4AC|
|digestType|	Yes|	An integer as specified by the digest type field of a DNS DS record|	1|

### KeyData
Each KeyData must have the following form:

    flags|protocol|publicKey|algorithm

The KeyData attributes are described in the following table:

|Attribute	|Required|	Description	|Example|
|-----------|:------:|--------------|-------|
|flags	|Yes	|An integer representing the flags field value in the DNSKEY record|	257|
|protocol|	Yes|	An integer representation of the protocol field value of the DNSKEY record|	3|
|publicKey|	Yes|	A string representation of the public key in the DNSKEY record|	AQPJ////4Q==|
|algorithm|	Yes|	An integer as specified by the algorithm field of a DNSKEY record as specified by|	1|

# Catalogs

IANA has defined stand-alone registry labeled "RDAP JSON Values". This new registry is to be used in notices and remarks, status, role, event action, and domain variant relation fields specified in RDAP.

### Status

The following values have been registered in the "RDAP JSON Values" registry for the Status of the RDAP objects and their EPP status equivalent ([DRAFT](https://tools.ietf.org/html/draft-ietf-regext-epp-rdap-status-mapping-01)):

|EPP Status|	RDAP Status name|	Status description|
|:--------:|:------------------:|---------------------|
|	|validated|	Signifies that the data of the object instance has been found to be accurate. This type of status is usually found on entity object instances to note the validity of identifying contact information.|
||	renew prohibited|	Renewal or reregistration of the object instance is forbidden.|
||	update prohibited|	Updates to the object instance are forbidden.|
|	|transfer prohibited|	Transfers of the registration from one registrar to another are forbidden.|
|	|delete prohibited|	Deletion of the registration of the object instance is forbidden.|
||	proxy|	The registration of the object instance has been performed by a third party.|
||	private	|The information of the object instance is not designated for public consumption.|
||	removed|	Some of the information of the object instance has not been made available and has been removed.|
|	|obscured|	Some of the information of the object instance has been altered for the purposes of not readily revealing the actual information of the object instance.|
|linked	|associated|	The object instance is associated with other object instances in the registry.|
|ok	|active|	The object instance is in use. For domain names, it signifies that the domain name is published in DNS. For network and autnum registrations, it signifies that they are allocated or assigned for use in operational networks. |
|inactive|	inactive|	The object instance is not in use.|
||	locked	|Changes to the object instance cannot be made, including the association of other object instances.|
|pendingCreate|	pending create|	A request has been received for the creation of the object instance, but this action is not yet complete.|
|pendingRenew	|pending renew|	A request has been received for the renewal of the object instance, but this action is not yet complete.|
|pendingTransfer|	pending transfer|	A request has been received for the transfer of the object instance, but this action is not yet complete.|
|pendingUpdate|	pending update|	A request has been received for the update or modification of the object instance, but this action is not yet complete.|
||	Pending delete |	A request has been received for the deletion or removal of the object instance, but this action is not yet complete. For domains, this might mean that the name is no longer published in DNS but has not yet been purged from the registry database.|



If you give us an epp_status column in the Select statements of an object, Red Dog migration tool will map it to their RDAP equivalent if exist.  

### EventAction

The following values have been registered in the "RDAP JSON Values" registry for the EventAction of the RDAP objects:

|EventAction name|	EventAction description|
|----------------|-------------------------|
|registration|	The object instance was initially registered.|
|reregistration |	The object instance was registered subsequently to initial registration.|
|last changed|	An action noting when the information in the object instance was last changed.|
|expiration	|The object instance has been removed or will be removed at a predetermined date and time from the registry.|
|deletion|	The object instance was removed from the registry at a point in time that was not predetermined
|reinstantiation|	The object instance was reregistered after having been removed from the registry.|
|transfer|	The object instance was transferred from one registrant to another.|
|locked|	The object instance was locked.|
|unlocked|	The object instance was unlocked.|

### Remark and notices types

The following values have been registered in the "RDAP JSON Values" registry for the Remark and notices types:

|Type name|	Type description|
|---------|-----------------|
|result set truncated due to authorization|	The list of results does not contain all results due to lack of authorization. This may indicate to some clients that proper authorization will yield a longer result set.|
|result set truncated due to excessive load|	The list of results does not contain all results due to an excessively heavy load on the server. This may indicate to some clients that requerying at a later time will yield a longer result set.|
|result set truncated due to unexplainable reasons|	The list of results does not contain all results for an unexplainable reason. This may indicate to some clients that requerying for any reason will not yield a longer result set.|
|object truncated due to authorization|	The object does not contain all data due to lack of authorization.|
|object truncated due to excessive load|	The object does not contain all data due to an excessively heavy load on the server. This may indicate to some clients that requerying at a later time will yield all data of the object.|
|object truncated due to unexplainable reasons|	The object does not contain all data for an unexplainable reason.|

This types are only used in the notices objects, so you don’t have to worry about use them.

### Rol
The following values have been registered in the "RDAP JSON Values" registry for Rol:

|Rol name|	Rol description|
|:------:|-----------------|
|registrant	|The entity object instance is the registrant of the registration. In some registries, this is known as a maintainer.|
|technical|	The entity object instance is a technical contact for the registration.|
|administrative|	The entity object instance is an administrative contact for the registration.|
|abuse	|The entity object instance handles network abuse issues on behalf of the registrant of the registration.|
|billing|	The entity object instance handles payment and billing issues on behalf of the registrant of the registration.|
|registrar|	The entity object instance represents the authority responsible for the registration in the registry.|
|reseller|	The entity object instance represents a third party through which the registration was conducted (i.e., not the registry or registrar).|
|sponsor|	The entity object instance represents a domain policy sponsor, such as an ICANN-approved sponsor.|
|proxy|	The entity object instance represents a proxy for another entity object, such as a registrant.|
|notifications |	An entity object instance designated to receive notifications about association object instances.|
|noc|	The entity object instance handles communications related to a network operations center (NOC).|

### VariantRelation

The following values have been registered in the "RDAP JSON Values" registry for VariantRelations:

|VariantRelation name	|VariantRelation description|
|-----------------------|---------------------------|
|registered	|The variant names are registered in the registry.|
|unregistered|	The variant names are not found in the registry.|
|registration restricted|	Registration of the variant names is restricted to certain parties or within certain rules.|
|open registration|	Registration of the variant names is available to generally qualified registrants.|
|conjoined|	Registration of the variant names occurs automatically with the registration of the containing domain registration.|


### Country codes

Red Dog uses the standard for area codes used by the [United Nations Statistics Division](https://en.wikipedia.org/wiki/United_Nations_Statistics_Division).The complete list of country codes can be found [here](http://www.nationsonline.org/oneworld/country_code_list.htm "One World Nations Online Country List"). 

[(See  Status section)]:#status "Status"
[(See EventData section)]:#eventdata "Event Data"
[(See EntityData section)]:#entitydata "Entity Data"
[(See PublicIdData section)]:#publiciddata "Public Id Data"
[VCardData]:#vcarddata "VCard Data"
[(See IpAddressData section)]:#ipaddressdata "Ip Address Data"
[(See VariantData section)]:#variantdata "Variant Data"
[(See SecureDNSData section)]:#securednsdata "Secure DNS Data"
[(See DSData section)]:#dsdata "DS Data"
[(See KeyData section)]:#keydata "Key Data"
[(See Country code section)]:#country-codes "Country Codes"
[(See Events section)]:#eventaction "Events Catalog"
[(see Entity section)]:#entity "Entity"
[(see Rol section)]:#rol "Roles catalog"


