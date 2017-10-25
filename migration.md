---
title: Database Tables Definition
---

# Exporting a database to Red Dog's built-in schema

## Introduction

Red Dog's builtin schema is an ordinary relational database conceived in MySQL. Users that aim to implement [Option 3](intro.html#option-3-using-red-dogs-builtin-schema) need to build a mechanism to populate this database and keep it updated.

The means through which the data is exported will depend on how the data is stored in the origin database, this documentation cannot fall into details on how to do it. Instead, this will serve as reference material for Red Dog's schema.

Red Dog's database contains 68 tables. Though the task of populating them might seem daunting, it is important to note that's likely to need only a fraction of them. The main ones are [autonomous_system_number](#autonomous_system_number), [domain](#domain), [entity](#entity), [ip_network](#ip_network) and [nameserver](#nameserver). Pick only the ones needed and branch from there.

## Full Schema definition

The script to generate the database is located [here](https://raw.githubusercontent.com/NICMx/rdap-sql-provider/master/src/main/resources/META-INF/sql/Database.sql "SQL file with tables") and the ER diagram [here](img/diagram/db-er.png).

These are the tables:

<ol class="three-columns">
	<li><a href="#asn_entity_roles">asn_entity_roles</a></li>
	<li><a href="#asn_events">asn_events</a></li>
	<li><a href="#asn_links">asn_links</a></li>
	<li><a href="#asn_remarks">asn_remarks</a></li>
	<li><a href="#asn_status">asn_status</a></li>
	<li><a href="#autonomous_system_number">autonomous_system_number</a></li>
	<li><a href="#country_code">country_code</a></li>
	<li><a href="#domain">domain</a></li>
	<li><a href="#domain_entity_roles">domain_entity_roles</a></li>
	<li><a href="#domain_events">domain_events</a></li>
	<li><a href="#domain_links">domain_links</a></li>
	<li><a href="#domain_nameservers">domain_nameservers</a></li>
	<li><a href="#domain_networks">domain_networks</a></li>
	<li><a href="#domain_public_ids">domain_public_ids</a></li>
	<li><a href="#domain_remarks">domain_remarks</a></li>
	<li><a href="#domain_status">domain_status</a></li>
	<li><a href="#ds_data">ds_data</a></li>
	<li><a href="#ds_events">ds_events</a></li>
	<li><a href="#ds_links">ds_links</a></li>
	<li><a href="#entity">entity</a></li>
	<li><a href="#entity_contact">entity_contact</a></li>
	<li><a href="#entity_entity_roles">entity_entity_roles</a></li>
	<li><a href="#entity_events">entity_events</a></li>
	<li><a href="#entity_links">entity_links</a></li>
	<li><a href="#entity_public_ids">entity_public_ids</a></li>
	<li><a href="#entity_remarks">entity_remarks</a></li>
	<li><a href="#entity_role">entity_role</a></li>
	<li><a href="#entity_status">entity_status</a></li>
	<li><a href="#event">event</a></li>
	<li><a href="#event_action">event_action</a></li>
	<li><a href="#event_links">event_links</a></li>
	<li><a href="#ip_address">ip_address</a></li>
	<li><a href="#ip_network">ip_network</a></li>
	<li><a href="#ip_network_entity_roles">ip_network_entity_roles</a></li>
	<li><a href="#ip_network_events">ip_network_events</a></li>
	<li><a href="#ip_network_links">ip_network_links</a></li>
	<li><a href="#ip_network_parent_relation">ip_network_parent_relation</a></li>
	<li><a href="#ip_network_remarks">ip_network_remarks</a></li>
	<li><a href="#ip_network_status">ip_network_status</a></li>
	<li><a href="#ip_version">ip_version</a></li>
	<li><a href="#key_data">key_data</a></li>
	<li><a href="#key_events">key_events</a></li>
	<li><a href="#key_links">key_links</a></li>
	<li><a href="#link">link</a></li>
	<li><a href="#link_lang">link_lang</a></li>
	<li><a href="#nameserver">nameserver</a></li>
	<li><a href="#nameserver_entity_roles">nameserver_entity_roles</a></li>
	<li><a href="#nameserver_events">nameserver_events</a></li>
	<li><a href="#nameserver_links">nameserver_links</a></li>
	<li><a href="#nameserver_remarks">nameserver_remarks</a></li>
	<li><a href="#nameserver_status">nameserver_status</a></li>
	<li><a href="#public_id">public_id</a></li>
	<li><a href="#rdap_access_role">rdap_access_role</a></li>
	<li><a href="#rdap_user">rdap_user</a></li>
	<li><a href="#rdap_user_role">rdap_user_role</a></li>
	<li><a href="#relation">relation</a></li>
	<li><a href="#remark">remark</a></li>
	<li><a href="#remark_description">remark_description</a></li>
	<li><a href="#remark_links">remark_links</a></li>
	<li><a href="#roles">roles</a></li>
	<li><a href="#secure_dns">secure_dns</a></li>
	<li><a href="#status">status</a></li>
	<li><a href="#variant">variant</a></li>
	<li><a href="#variant_name">variant_name</a></li>
	<li><a href="#variant_relation">variant_relation</a></li>
	<li><a href="#vcard">vcard</a></li>
	<li><a href="#vcard_postal_info">vcard_postal_info</a></li>
	<li><a href="#zone">zone</a></li>
</ol>


Each table is described in the following sections:

### asn_entity_roles

This table contains the role that an Entity has in relation to an ASN.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id| No | autonomous\_system\_number | asn\_id| 
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|rol\_id|tinyint(4)|Role's id| No | roles | rol\_id| 

**Primary key**: asn\_id, ent\_id, rol\_id.

### asn_events

This table contains the relation between an ASN and its events.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id| No | autonomous_system_number | asn\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: asn\_id, eve\_id.

### asn_links

This table contains the relation between an ASN and its links.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id| No | autonomous_system_number | asn\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: asn\_id, lin\_id.

### asn_remarks

This table contains the relation between an ASN and its remarks.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id| No | autonomous_system_number | asn\_id| 
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 

**Primary key**: asn\_id, rem\_id.

### asn_status

This table contains the relation between an ASN and its status.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id| No | autonomous_system_number | asn\_id| 
|sta\_id|bigint(20)|Status's id| No | status | sta\_id| 

**Primary key**: asn\_id, lin\_id.

### autonomous_system_number

This table contains the information of the Autonomous system numbers (ASN).

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's unique id| No |   | | 
|asn\_handle|varchar(100)|A RIR unique identifier of the autnum registration| No |  | | 
|asn\_start\_autnum|bigint(20)|A number representing the starting number in the block of Autonomous System numbers| No ||| 
|asn\_end\_autnum|bigint(20)|A number representing the ending number in the block of Autonomous System numbers| No ||| 
|asn\_name|varchar(200)|An identifier assigned to the autnum registration by the registration holder| Yes |   | | 
|asn\_type|varchar(200)|A string containing a RIR specific classification of the autnum| Yes |   | | 
|asn\_port43|varchar(254)|A string containing the fully qualified host name or IP addres of the WHOIS server where the ASN instance may be found| Yes |   | | 
|ccd\_id|smallint(5) unsigned|Country code id| No |country\_code   |cc\_id | 

**Primary key**: asn\_id, ccd\_id.

### country_code
This table contains the catalog of the two-character country code.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ccd\_id|bigint(20)|Country code's id| No |   | | 
|ccd\_code|varchar(2)|The two-character country code| No |  | | 

**Primary key**: ccd\_id

### domain
This table contains the information about the domain registration.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's unique id|No||| 
|dom\_handle|varchar(255)|A RIR/DNR unique identifier of the domain registration|No|||
|dom\_ldh\_name|varchar(64)|A string containing a domain name in LDH form|Yes|||
|dom\_unicode\_name|varchar(255)|A string containing a domain name in U-label||||
|dom\_port43|varchar(254)|A string containing the fully qualified host name or IP address of the WHOIS server where the domain instance may be found|Yes|||
|zone\_id|smallint(6)|Zone's id|No|||

**Primary key**: dom\_id, zone\_id

### domain_entity_roles

This table contains the role that an Entity has in relation to a Domain.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|rol\_id|tinyint(4)|Role's id| No | roles | rol\_id| 

**Primary key**: dom\_id, ent\_id, rol\_id.

### domain_events

This table contains the relation between a Domain and its events.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: dom\_id, eve\_id.

### domain_links

This table contains the relation between a Domain and its links.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: dom\_id, lin\_id.

### domain_nameservers

This table contains the relation between a Domain and its nameservers.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 

**Primary key**: dom\_id, nse\_id.

### domain_networks

This table contains the relation between a Domain and its ip networks.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|ine\_id|bigint(20)|IP Network's id| No | ip_network | ine\_id| 

**Primary key**: dom\_id, ine\_id.

### domain_public_ids

This table contains the relation between a Domain and its public ids.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|pid\_id|bigint(20)|Public id's id| No | public_id | pid\_id| 

**Primary key**: dom\_id, pid\_id.

### domain_remarks

This table contains the relation between a Domain and its remarks.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 

**Primary key**: dom\_id, rem\_id.

### domain_status

This table contains the relation between a Domain and its status.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|sta\_id|bigint(20)|Status's id| No | status | sta\_id| 

**Primary key**: dom\_id, sta\_id.

### ds_data

This table contains the information of a secure DNS DS record.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dsd\_id|bigint(20)|Ds data's id| No | | | 
|sdns\_id|bigint(20)|Secure DNS's id| No | secure_dns | sdns\_id| 
|dsd\_keytag|int(11)|An integer as specified by the key tag field of a DNS DS record| No | | | 
|dsd\_algorithm|int(11)|An integer as specified by the algorithm field of a DNS DS record| No | | | 
|dsd\_digest|varchar(255)|A string as specified by the digest field of a DNS DS record| No | | | 
|dsd\_digest_type|int(11)|An integer as specified by the digest type field of a DNS DS record| No | | | 

**Primary key**: dsd\_id, sdns\_id.

### ds_events

This table contains the relation between a DS Data and its events.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dsd\_id|bigint(20)|Ds data's id| No | ds_data | dsd\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: dsd\_id, eve\_id.

### ds_links

This table contains the relation between a DS Data and its links.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dsd\_id|bigint(20)|DS data's id| No | ds_data | dsd\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: dsd\_id, lin\_id.

### entity

This table contains the information of organizations, corporations, governments, non-profits, clubs, individual persons, and informal groups of people.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's unique id|No||| 
|ent\_handle|varchar(255)|A RIR/DNR unique identifier of the entity registration|Yes|||
|dom\_port43|varchar(254)|A string containing the fully qualified host name or IP address of the WHOIS server where the entity instance may be found| Yes|||

**Primary key**: ent\_id, zone\_id

### entity_contact

This table contains the relation between an Entity and its contact VCard.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|vca\_id|bigint(20)|Vcard's id| No | vcard | vca\_id| 

**Primary key**: ent\_id, vca\_id.

### entity_entity_roles

This table contains the role that an Entity has in relation to another Entity.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|main\_ent\_id|bigint(20)|Main Entity's id| No | entity | dom\_id| 
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|rol\_id|tinyint(4)|Role's id| No | roles | rol\_id| 

**Primary key**: main\_ent\_id, ent\_id, rol\_id.

### entity_events

This table contains the relation between an Entity and its events.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: ent\_id, eve\_id.

### entity_links

This table contains the relation between an Entity and its links.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id|

Primary key: ent\_id, lin\_id.

### entity_public_ids

This table contains the relation between an Entity and its public ids.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|pid\_id|bigint(20)|Public id's id| No | public\_id | pid\_id| 

**Primary key**: ent\_id, pid\_id.

### entity_remarks

This table contains the relation between an Entity and its remarks.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id|
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 

**Primary key**: ent\_id, rem\_id.

### entity_role

This table contains the relation between an Entity and its role.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint|Entity's id| No | entity | ent\_id|
|rol\_id|tinyint|Role's id| No | roles | rol\_id| 

**Primary key**: ent\_id, rol\_id.

### entity_status

This table contains the relation between an Entity and its status.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|sta\_id|bigint(20)|Status's id| No | status | sta\_id| 

**Primary key**: ent\_id, sta\_id.

### event

This table contains the information about events that have occurred on an object instance.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|eve\_id|bigint(20)|Event's id|No||| 
|eac\_id|smallint(6)|Event action's id|No|event\_action|eac\_id| 
|eve\_actor|varchar(45)|Event actor|Yes||| 
|eve\_date|datetime|Event date|Yes||| 

**Primary key**: eve\_id.

### event_action

This table contains the catalog of Events Actions.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|eac\_id|smallint(6)|Event action's id| No | | | 
|eac\_name|varchar(100)|Event action's name| Yes | | | 

**Primary key**: eac\_id.

### event_links

This table contains the relation between an Event and its links.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id|

Primary key: eve\_id, lin\_id.

### ip_address

This table contains the information of a nameserver's ip addresses.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|iad\_id|int(11)|IP address's id| No ||| 
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|iad\_type|tinyint(4)|IP address type (4 or 6)| No ||| 
|iad\_value|varbinary(16)|IP address value| No ||| 

**Primary key**: iad\_id, nse\_id.

### ip_network
This table contains the information about the network registration and entities related to an IP network.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_id|bigint(20). Auto Increment. Unsigned|IP network's id|No|||
|ine\_handle|varchar(255)|A RIR/DNR unique identifier of the IP network registration|No|||
|ine\_start\_address\_up|bigint(20) unsigned|The up part of the starting IP address of the network|Yes|||
|ine\_start\_address\_down|bigint(20) unsigned|The down part of the starting IP address of the network|Yes|||
|ine\_end\_address\_up|bigint(20) unsigned|The up part of the ending IP address of the network|Yes|||
|ine\_end\_address\_down|bigint(20) unsigned|The down part of the ending IP address of the network|Yes|||
|ine\_name|varchar(255)|An identifier assigned to the network registration by the registration holder|Yes|||
|ine\_type|varchar(255)|A string containing a RIR/DNR specific classification of the Network|Yes|||
|ine\_port43|varchar(254)|A string containing the fully qualified host name or IP address of the WHOIS server where the IP network instance may be found|Yes|||
|ccd\_id|smallint(5) unsigned|Country code's id|No|country\_code|ccd\_id|
|ip\_version\_id|tinyint(3) unsigned|IP version's id| No|ip\_version|ive\_id|
|ine\_parent\_handle|varchar(255)|A string containing a RIR/DNR unique identifier of the parent network of this network registration|Yes|||
|ine\_cidr|smallint(6)|Network mask length of the IP address|Yes|||

**Primary key**: ine\_id.

### ip_network_entity_roles

This table contains the role that an Entity has in relation to an IP Network.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_id|bigint(20)|IP Network's id| No | ip\_network | ine\_id|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|rol\_id|tinyint(4)|Role's id| No | roles | rol\_id| 

**Primary key**: ine\_id, ent\_id, rol\_id.

### ip_network_events

This table contains the relation between an IP Network and its events.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_id|bigint(20)|IP Network's id| No | ip_network | ine\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: ine\_id, eve\_id.

### ip_network_links

This table contains the relation between an IP Network and its links.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_id|bigint(20)|IP Network's id| No | ip_network | ine\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: ine\_id, lin\_id.

### ip_network_parent_relation

This table contains the relation between an IP Network and its parent network.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_parent\_handle|varchar(255)|Parent Network's handle| No | ip\_network | ine\_id| 
|ine\_son\_handle|varchar(255)|Son Network's handle| No | ip\_network | ine\_id| 

**Primary key**: ine\_parent\_handle, ine\_son\_handle.

### ip_network_remarks

This table contains the relation between an IP Network and its remarks.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_id|bigint(20)|IP Network's id| No | ip\_network | ine\_id| 
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 

**Primary key**: ine\_id, rem\_id.

### ip_network_status

This table contains the relation between an IP Network and its status.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|IP Network's id| No | ip\_network | ine\_id| 
|sta\_id|bigint(20)|Status's id| No | status | sta\_id| 

**Primary key**: ine\_id, sta\_id.

### ip_version

This table contains the catalog of ip version types.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ive\_id|tinyint(3) unsigned|IP version's id|No||| 
|ive\_name|varchar(2)|IP version's name ('v4' or 'v6')| No ||| 

**Primary key**: ive\_id.

### key_data

This table contains the information of the Key Data related to the Secure DNS information of a domain.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|kd\_id|bigint Auto increment.|Key data's id|No||| 
|sdns\_id|bigint|Secure DNS's id|No|secure\_dns|sdns\_id| 
|kd\_flags|int unsigned|Key data's flags|Yes||| 
|kd\_protocol|int unsigned|Key data's protocol|Yes||| 
|kd\_public\_key|varchar(255)|Key data's public key|Yes||| 
|kd\_algorithm|int unsigned|Key data's algorithm|Yes||| 

**Primary_key**: kd\_id, sdns\_id.

### key_events

This table contains the events related to a Key Data.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|kd\_id|bigint|Key data's id|No|key\_data|kd\_id| 
|eve\_id|bigint|Event's id|No|event|eve\_id| 

**Primary_key**: kd\_id, eve\_id.

### key_links

This table contains the links related to a Key Data.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|kd\_id|bigint|Key data's id|No|key\_data|kd\_id| 
|lin\_id|bigint|Link's id|No|link|lin\_id| 

**Primary_key**: kd\_id, lin\_id.

### link

This table contains the information about links.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|lin\_id|bigint(20) Auto increment.|Link's id|No||| 
|lin\_value|varchar(45)|Link's value|Yes||| 
|lin\_rel|varchar(45)|Link's rel attribute|Yes||| 
|lin\_href|varchar(45)|Link's href attribute|No||| 
|lin\_title|varchar(45)|Link's title|Yes||| 
|lin\_media|varchar(45)|Link's media attribute|Yes||| 
|lin\_type|varchar(45)|Link's type|Yes||| 

**Primary_key**: lin_id.

### link_lang

This table contains the languages related to a link.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|lin\_id|bigint|Link's id|No|link|lin\_id| 
|lan\_hreflang|varchar(45)|Language|No||| 

**Primary_key**: lin\_id, lan\_hreflang.

### nameserver

This table contains information regarding DNS nameservers used in both forward and reverse DNS.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20) Auto increment.|Nameserver's id|No||| 
|nse\_value|varchar(100)|A RIR/DNR unique identifier of the nameserver registration|No||| 
|nse\_rel|varchar(254)|A string containing a nameserver name in LDH form|Yes||| 
|nse\_href|varchar(255)|A string containing a nameserver name in U-label|Yes||| 
|nse\_hreflang|varchar(254)|A string containing the fully qualified host name or IP address of the WHOIS server where the nameserver instance may be found|Yes||| 

**Primary_key**: nse_id.

### nameserver_entity_roles

This table contains the role that an Entity has in relation to a Nameserver.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|rol\_id|tinyint(4)|Role's id| No | roles | rol\_id| 

**Primary key**: nse\_id, ent\_id, rol\_id.

### nameserver_events

This table contains the relation between a Nameserver and its events.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: nse\_id, eve\_id.

### nameserver_links

This table contains the relation between a Nameserver and its links.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: nse\_id, lin\_id.

### nameserver_remarks

This table contains the relation between a Nameserver and its remarks.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 

**Primary key**: nse\_id, rem\_id.

### nameserver_status

This table contains the relation between a Nameserver and its status.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|sta\_id|bigint(20)|Status's id| No | status | sta\_id| 

**Primary key**: nse\_id, sta\_id.

### public_id

This table contains the information about Public IDs.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|pid\_id|bigint(20) Auto increment.|Public id's id|No||| 
|pid\_type|varchar(255)|Public id's type|Yes||| 
|pid\_identifier|varchar(255)|Public id's identifier|Yes||| 

**Primary_key**: pid_id.

### rdap_access_role

This table contains a catalog of the access roles that a user could have.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rar\_name|varchar(45)|Access role's name|No||| 
|rar\_description|varchar(250)|Access role's description|No||| 

**Primary_key**: rar\_name.

### rdap_user

This table contains the information about the users.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rus\_name|varchar(100)|User's name|No||| 
|rus\_pass|varchar(200)|User's password|No||| 
|rus\_max\_search\_results|int(11)|Max number of results that will be returned for the user|Yes||| 

**Primary_key**: rus\_name.

### rdap_user_role

This table contains the Access Roles that a User has.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rus\_name|varchar(100)|User's name|No|rdap\_user|rus\_name| 
|rar\_name|varchar(45)|Access role's name|No|rdap\_access\_role|rar\_name| 

**Primary_key**: rus\_name, rar\_name.

### relation

This table contains the catalog of Variant relations.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rel\_id|tinyint(4)|Relation's id|No||| 
|rel\_type|varchar(255)|Relation's type|Yes||| 

**Primary_key**: rel\_id.

### remark

This table contains the information about the Remarks that denote information about an object.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rem\_id|bigint(20) Auto increment.|Remark's id|No||| 
|rem\_title|varchar(255)|Remark's title|Yes||| 
|rem\_type|varchar(255)|Remark's type|Yes||| 
|rem\_lang|varchar(255)|Remark's language|Yes||| 

**Primary_key**: rem\_id.

### remark_description

This table contains a Remark's descriptions.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rem\_id|bigint(20) Auto increment.|Remark's id|No|remark|rem_id| 
|rde\_order|mediumint(9)|Placement of the description at the Remark|No||| 
|rde\_description|varchar(255)|Remark's description|No||| 

**This table does not have a primary key.**

### remark_links

This table contains the relation between a Remark and its links.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: rem\_id, lin\_id.

### roles

This table contains the catalog of Roles that an entity could have.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rol\_id|tinyint(4)|Role's id|No||| 
|role\_name|varchar(100)|Role's name|Yes||| 

**Primary_key**: rol\_id.

### secure_dns

This table contains the information about a domain Secure DNS.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|sdns\_id|bigint(20) Auto increment.|Secure dns' id|No||| 
|sdns\_zone\_signed|tinyint(1)|Flag to show if the zone has been signed (1=true, 0=false)|No||| 
|sdns\_delegation\_signed|tinyint(1)|Flag to show if there are DS records in the parent (1=true, 0=false)|No||| 
|sdns\_max\_sig\_life|int(11)|An integer representing the signature lifetime in seconds to be used when creating the RRSIG DS record in the parent zone|Yes||| 
|dom\_id|bigint(20)|Domain's id|No|domain|dom\_id| 

**Primary_key**: sdns\_id.

### status

This table contains the Status catalog.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|sta\_id|smallint(4)|Status's id|No||| 
|sta\_name|varchar(100)|Status's name.|Yes||| 

**Primary_key**: sta\_id.

### variant

This table contains information about the domain's variants.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|var\_id|bigint(20)|Variant's id|No||| 
|var\_idn\_table|varchar(100)|Variant's IDN table|Yes||| 
|dom\_id|bigint(20)|Domain's id|No|domain|dom\_id| 

**Primary_key**: var\_id, dom\_id.

### variant_name

This table contains the variants names.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|var\_ldh\_name|varchar(63)|Variant's ldh name|Yes||| 
|var\_id|bigint(20)|Variant's id|No|variant|var\_id| 
|var\_unicode\_name|varchar(255)|Variant's unicode name|Yes||| 

**This table does not have a primary key.**

### variant_relation

This table contains the type of relations of a Variant.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rel\_id|tinyint(4)|Relation's id|No|relation|rel\_id| 
|var\_id|bigint(20)|Variant's id|No|variant|var\_id| 

**Primary_key**: rel\_id, var\_id.

### vcard

This table contains the entities VCards.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|vca\_id|bigint(20) auto increment.|Vcard's id|No||| 
|vca\_name|varchar(100)|Contact's name|Yes||| 
|vca\_company\_name|varchar(255)|Contact's company name|Yes||| 
|vca\_company\_url|varchar(255)|Contact's url|Yes||| 
|vca\_email|varchar(200)|Contact's email|Yes||| 
|vca\_voice|varchar(50)|Contact's telephone|Yes||| 
|vca\_cellphone|varchar(50)|Contact's cellphone|Yes||| 
|vca\_fax|varchar(50)|Contact's fax|Yes||| 
|vca\_job\_title|varchar(200)|Contact's job title|Yes||| 

**Primary_key**: vca\_id.

### vcard_postal_info

This table contains the VCards postal information.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|vpi\_id|bigint(20) auto increment.|Postal info's id|No||| 
|vca\_id|bigint(20)|Vcard's id|No|vcard|vca\_id| 
|vpi\_type|varchar(45)|Postal info's type.|Yes||| 
|vpi\_country|bigint(100)|Country|Yes||| 
|vpi\_city|bigint(100)|City|Yes||| 
|vpi\_street1|bigint(100)|Street (first part)|Yes||| 
|vpi\_street2|bigint(100)|Street (second part)|Yes||| 
|vpi\_street3|bigint(100)|Street (third part)|Yes||| 
|vpi\_state|bigint(100)|State|Yes||| 
|vpi\_postal\_code|bigint(100)|Postal code|Yes||| 

**Primary_key**: vpi\_id, vca\_id.

### zone

This table contains the zones managed by the RDAP server owner.

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|zone\_id|smallint(6) Auto increment|Zone's id|No||| 
|zone\_name|varchar(254)|Zone's name|No||| 

**Primary_key**: zone\_id.

## Where to go next

[Deploying the RDAP Server with the SQL Provider](server-install-option-3.html).
