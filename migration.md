---
title: Exporting a database to Red Dog's built-in schema
breadcrums: ["Documentation", "documentation.html", "Installation/Configuration", "documentation.html#installationconfiguration", "Option 3 - Using Red Dog’s builtin schema", "documentation.html#option-3---using-red-dogs-builtin-schema"]
---

# Exporting a database to Red Dog's built-in schema

## Introduction

Red Dog's builtin schema is an ordinary relational database conceived in MySQL. Users that aim to implement [Option 3](intro.html#option-3-using-red-dogs-builtin-schema) need to build a mechanism to populate this database and keep it updated.

The means through which the data is exported will depend on how the data is stored in the origin database, this documentation cannot fall into details on how to do it. Instead, this will serve as reference material for Red Dog's schema.

Red Dog's database contains 68 tables. Though the task of populating them might seem daunting, it is important to note that's likely to need only a fraction of them. The main ones are [autonomous_system_number](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L365), [domain](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L161), [entity](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L25), [ip_network](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L626) and [nameserver](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L423). Pick only the ones needed and branch from there.

## Full Schema definition

The script to generate the database is located [here](https://raw.githubusercontent.com/NICMx/rdap-sql-provider/master/src/main/resources/META-INF/sql/Database.sql "SQL file with tables") and the ER diagram [here](img/diagram/db-er.png).

The following table shows the database tables, to see more detail of each table there's a link to its creation script:

| Table | Description | Table details |
|------------------|------------------------------------------------------------------------|--------------------------|
| asn_entity_roles | This table contains the role that an Entity has in relation to an ASN. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L392) |
| asn_events | This table contains the relation between an ASN and its events. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L911) |
| asn_links | This table contains the relation between an ASN and its links. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L886) |
| asn_remarks | This table contains the relation between an ASN and its remarks. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L861) |
| asn_status | This table contains the relation between an ASN and its status. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L837) |
| autonomous_system_number | This table contains the information of the Autonomous system numbers (ASN). | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L365) |
| country_code | This table contains the catalog of the two-character country code. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L351) |
| domain | This table contains the information about the domain registration. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L161) |
| domain_entity_roles | This table contains the role that an Entity has in relation to a Domain. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L186) |
| domain_events | This table contains the relation between a Domain and its events. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L586) |
| domain_links | This table contains the relation between a Domain and its links. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L561) |
| domain_nameservers | This table contains the relation between a Domain and its nameservers. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L441) |
| domain_networks | This table contains the relation between a Domain and its ip networks. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L668) |
| domain_public_ids | This table contains the relation between a Domain and its public ids. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1156) |
| domain_remarks | This table contains the relation between a Domain and its remarks. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L536) |
| domain_status | This table contains the relation between a Domain and its status. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L512) |
| ds_data | This table contains the information of a secure DNS DS record. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L488) |
| ds_events | This table contains the relation between a DS Data and its events. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L692) |
| ds_links | This table contains the relation between a DS Data and its links. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L717) |
| entity | This table contains the information of organizations, corporations, governments, non-profits, clubs, individual persons, and informal groups of people. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L25) |
| entity_contact | This table contains the relation between an Entity and its contact VCard. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1282) |
| entity_entity_roles | This table contains the role that an Entity has in relation to another Entity. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1338) |
| entity_events | This table contains the relation between an Entity and its events. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L301) |
| entity_links | This table contains the relation between an Entity and its links. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L326) |
| entity_public_ids | This table contains the relation between an Entity and its public ids. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1181) |
| entity_remarks | This table contains the relation between an Entity and its remarks. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L237) |
| entity_role | This table contains the relation between an Entity and its role. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1567) |
| entity_status | This table contains the relation between an Entity and its status. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L276) |
| event | This table contains the information about events that have occurred on an object instance. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L111) |
| event_action | This table contains the catalog of Events Actions. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L97) |
| event_links | This table contains the relation between an Event and its links. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1207) |
| ip_address | This table contains the information of a nameserver's ip addresses. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L766) |
| ip_network | This table contains the information about the network registration and entities related to an IP network. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L626) |
| ip_network_entity_roles | This table contains the role that an Entity has in relation to an IP Network. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1422) |
| ip_network_events | This table contains the relation between an IP Network and its events. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1010) |
| ip_network_links | This table contains the relation between an IP Network and its links. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L985) |
| ip_network_parent_relation | This table contains the relation between an IP Network and its parent network. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1453) |
| ip_network_remarks | This table contains the relation between an IP Network and its remarks. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L960) |
| ip_network_status | This table contains the relation between an IP Network and its status. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L936) |
| ip_version | This table contains the catalog of ip version types. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L611) |
| key_data | This table contains the information of the Key Data related to the Secure DNS information of a domain. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1478) |
| key_events | This table contains the events related to a Key Data. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1525) |
| key_links | This table contains the links related to a Key Data. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1501) |
| link | This table contains the information about links. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L78) |
| link_lang | This table contains the languages related to a link. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1549) |
| nameserver | This table contains information regarding DNS nameservers used in both forward and reverse DNS. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L423) |
| nameserver_entity_roles | This table contains the role that an Entity has in relation to a Nameserver. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1306) |
| nameserver_events | This table contains the relation between a Nameserver and its events. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1117) |
| nameserver_links | This table contains the relation between a Nameserver and its links. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L812) |
| nameserver_remarks | This table contains the relation between a Nameserver and its remarks. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L787) |
| nameserver_status | This table contains the relation between a Nameserver and its status. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L742) |
| public_id | This table contains the information about Public IDs. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1142) |
| rdap_access_role | This table contains a catalog of the access roles that a user could have. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1385) |
| rdap_user | This table contains the information about the users. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1370) |
| rdap_user_role | This table contains the Access Roles that a User has. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1398) |
| relation | This table contains the catalog of Variant relations. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1056) |
| remark | This table contains the information about the Remarks that denote information about an object. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L62) |
| remark_description | This table contains the Remark's descriptions. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L219) |
| remark_links | This table contains the relation between a Remark and its links. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1232) |
| roles | This table contains the catalog of Roles that an entity could have. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L133) |
| secure_dns | This table contains the information about a domain Secure DNS. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L465) |
| status | This table contains the Status catalog. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L262) |
| variant | This table contains information about the domain's variants. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1035) |
| variant_name | This table contains the variants names. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1070) |
| variant_relation | This table contains the type of relations of a Variant. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1257) |
| vcard | This table contains the entities VCards. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L41) |
| vcard_postal_info | This table contains the VCards postal information. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L1089) |
| zone | This table contains the zones managed by the RDAP server owner. | [View more](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Database.sql#L146) |

## Where to go next

[Deploying the RDAP Server with the SQL Provider](server-install-option-3.html).
