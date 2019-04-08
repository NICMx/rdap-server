---
title: Exporting a database to Red Dog's built-in schema
breadcrums: ["Documentation", "documentation.html", "Installation/Configuration", "documentation.html#installationconfiguration", "Option 3 - Using Red Dog’s builtin schema", "documentation.html#option-3---using-red-dogs-builtin-schema"]
wheretogo: ["Configuring Red Dog's reference implementation", "data-access-configuration.html"]
scriptLink: https://github.com/NICMx/rdap-sql-provider/blob/master/src/test/resources/META-INF/sql/Database.sql
rawScriptLink: https://raw.githubusercontent.com/NICMx/rdap-sql-provider/master/src/test/resources/META-INF/sql/Database.sql
---

# {{ page.title }}

## Introduction

Red Dog's builtin schema is an ordinary relational database conceived in MySQL. Users that aim to implement [Option 3: Using Red Dog’s builtin schema](intro.html#option-3-using-red-dogs-builtin-schema), need to build a mechanism to populate this database and keep it updated.

The means through which the data is exported will depend on how the data is stored in the origin database, this documentation cannot fall into details on how to do it. Instead, this will serve as reference material for Red Dog's schema.

Red Dog's database contains 68 tables. Though the task of populating them might seem daunting, it is important to note that's likely to need only a fraction of them. The main ones are [autonomous_system_number]({{ page.scriptLink }}#L365), [domain]({{ page.scriptLink }}#L161), [entity]({{ page.scriptLink }}#L25), [ip_network]({{ page.scriptLink }}#L626) and [nameserver]({{ page.scriptLink }}#L423). Pick only the ones needed and branch from there.

## Full Schema definition

The script to generate the database is located [here]({{ page.rawScriptLink }} "SQL file with tables") and the ER diagram [here](img/diagram/db-er.png).

The following table shows the database tables, to see more detail of each table there's a link to its creation script:

| Table | Description | Table details |
|------------------|------------------------------------------------------------------------|--------------------------|
| asn_entity_roles | This table contains the role that an Entity has in relation to an ASN. | [View more]({{ page.scriptLink }}#L413) |
| asn_events | This table contains the relation between an ASN and its events. | [View more]({{ page.scriptLink }}#L932) |
| asn_links | This table contains the relation between an ASN and its links. | [View more]({{ page.scriptLink }}#L907) |
| asn_remarks | This table contains the relation between an ASN and its remarks. | [View more]({{ page.scriptLink }}#L882) |
| asn_status | This table contains the relation between an ASN and its status. | [View more]({{ page.scriptLink }}#L858) |
| autonomous_system_number | This table contains the information of the Autonomous system numbers (ASN). | [View more]({{ page.scriptLink }}#L384) |
| country_code | This table contains the catalog of the two-character country code. | [View more]({{ page.scriptLink }}#L372) |
| domain | This table contains the information about the domain registration. | [View more]({{ page.scriptLink }}#L182) |
| domain_entity_roles | This table contains the role that an Entity has in relation to a Domain. | [View more]({{ page.scriptLink }}#L207) |
| domain_events | This table contains the relation between a Domain and its events. | [View more]({{ page.scriptLink }}#L607) |
| domain_links | This table contains the relation between a Domain and its links. | [View more]({{ page.scriptLink }}#L582) |
| domain_nameservers | This table contains the relation between a Domain and its nameservers. | [View more]({{ page.scriptLink }}#L462) |
| domain_networks | This table contains the relation between a Domain and its ip networks. | [View more]({{ page.scriptLink }}#L689) |
| domain_public_ids | This table contains the relation between a Domain and its public ids. | [View more]({{ page.scriptLink }}#L1177) |
| domain_remarks | This table contains the relation between a Domain and its remarks. | [View more]({{ page.scriptLink }}#L557) |
| domain_status | This table contains the relation between a Domain and its status. | [View more]({{ page.scriptLink }}#L533) |
| ds_data | This table contains the information of a secure DNS DS record. | [View more]({{ page.scriptLink }}#L509) |
| ds_events | This table contains the relation between a DS Data and its events. | [View more]({{ page.scriptLink }}#L713) |
| ds_links | This table contains the relation between a DS Data and its links. | [View more]({{ page.scriptLink }}#L738) |
| entity | This table contains the information of organizations, corporations, governments, non-profits, clubs, individual persons, and informal groups of people. | [View more]({{ page.scriptLink }}#L25) |
| entity_contact | This table contains the relation between an Entity and its contact VCard. | [View more]({{ page.scriptLink }}#L1304) |
| entity_entity_roles | This table contains the role that an Entity has in relation to another Entity. | [View more]({{ page.scriptLink }}#L1360) |
| entity_events | This table contains the relation between an Entity and its events. | [View more]({{ page.scriptLink }}#L322) |
| entity_links | This table contains the relation between an Entity and its links. | [View more]({{ page.scriptLink }}#L347) |
| entity_public_ids | This table contains the relation between an Entity and its public ids. | [View more]({{ page.scriptLink }}#L1203) |
| entity_remarks | This table contains the relation between an Entity and its remarks. | [View more]({{ page.scriptLink }}#L258) |
| entity_role | This table contains the relation between an Entity and its role. | [View more]({{ page.scriptLink }}#L1589) |
| entity_status | This table contains the relation between an Entity and its status. | [View more]({{ page.scriptLink }}#L297) |
| event | This table contains the information about events that have occurred on an object instance. | [View more]({{ page.scriptLink }}#L132) |
| event_action | This table contains the catalog of Events Actions. | [View more]({{ page.scriptLink }}#L118) |
| event_links | This table contains the relation between an Event and its links. | [View more]({{ page.scriptLink }}#L1229) |
| ip_address | This table contains the information of a nameserver's ip addresses. | [View more]({{ page.scriptLink }}#L787) |
| ip_network | This table contains the information about the network registration and entities related to an IP network. | [View more]({{ page.scriptLink }}#L647) |
| ip_network_entity_roles | This table contains the role that an Entity has in relation to an IP Network. | [View more]({{ page.scriptLink }}#L1443) |
| ip_network_events | This table contains the relation between an IP Network and its events. | [View more]({{ page.scriptLink }}#L1031) |
| ip_network_links | This table contains the relation between an IP Network and its links. | [View more]({{ page.scriptLink }}#L1006) |
| ip_network_parent_relation | This table contains the relation between an IP Network and its parent network. | [View more]({{ page.scriptLink }}#L1475) |
| ip_network_remarks | This table contains the relation between an IP Network and its remarks. | [View more]({{ page.scriptLink }}#L981) |
| ip_network_status | This table contains the relation between an IP Network and its status. | [View more]({{ page.scriptLink }}#L957) |
| ip_version | This table contains the catalog of ip version types. | [View more]({{ page.scriptLink }}#L632) |
| key_data | This table contains the information of the Key Data related to the Secure DNS information of a domain. | [View more]({{ page.scriptLink }}#L1500) |
| key_events | This table contains the events related to a Key Data. | [View more]({{ page.scriptLink }}#L1547) |
| key_links | This table contains the links related to a Key Data. | [View more]({{ page.scriptLink }}#L1523) |
| link | This table contains the information about links. | [View more]({{ page.scriptLink }}#L78) |
| link_lang | This table contains the languages related to a link. | [View more]({{ page.scriptLink }}#L1571) |
| nameserver | This table contains information regarding DNS nameservers used in both forward and reverse DNS. | [View more]({{ page.scriptLink }}#L444) |
| nameserver_entity_roles | This table contains the role that an Entity has in relation to a Nameserver. | [View more]({{ page.scriptLink }}#L1328) |
| nameserver_events | This table contains the relation between a Nameserver and its events. | [View more]({{ page.scriptLink }}#L1139) |
| nameserver_links | This table contains the relation between a Nameserver and its links. | [View more]({{ page.scriptLink }}#L833) |
| nameserver_remarks | This table contains the relation between a Nameserver and its remarks. | [View more]({{ page.scriptLink }}#L808) |
| nameserver_status | This table contains the relation between a Nameserver and its status. | [View more]({{ page.scriptLink }}#L763) |
| public_id | This table contains the information about Public IDs. | [View more]({{ page.scriptLink }}#L1164) |
| rdap_access_role | This table contains a catalog of the access roles that a user could have. | [View more]({{ page.scriptLink }}#L1407) |
| rdap_user | This table contains the information about the users. | [View more]({{ page.scriptLink }}#L1392) |
| rdap_user_role | This table contains the Access Roles that a User has. | [View more]({{ page.scriptLink }}#L1420) |
| relation | This table contains the catalog of Variant relations. | [View more]({{ page.scriptLink }}#L1077) |
| remark | This table contains the information about the Remarks that denote information about an object. | [View more]({{ page.scriptLink }}#L62) |
| remark_description | This table contains the Remark's descriptions. | [View more]({{ page.scriptLink }}#L240) |
| remark_links | This table contains the relation between a Remark and its links. | [View more]({{ page.scriptLink }}#L1254) |
| roles | This table contains the catalog of Roles that an entity could have. | [View more]({{ page.scriptLink }}#L154) |
| secure_dns | This table contains the information about a domain Secure DNS. | [View more]({{ page.scriptLink }}#L486) |
| status | This table contains the Status catalog. | [View more]({{ page.scriptLink }}#L283) |
| variant | This table contains information about the domain's variants. | [View more]({{ page.scriptLink }}#L1056) |
| variant_name | This table contains the variants names. | [View more]({{ page.scriptLink }}#L1091) |
| variant_relation | This table contains the type of relations of a Variant. | [View more]({{ page.scriptLink }}#L1279) |
| vcard | This table contains the entities VCards. | [View more]({{ page.scriptLink }}#L41) |
| vcard_postal_info | This table contains the VCards postal information. | [View more]({{ page.scriptLink }}#L1110) |
| zone | This table contains the zones managed by the RDAP server owner. | [View more]({{ page.scriptLink }}#L167) |
