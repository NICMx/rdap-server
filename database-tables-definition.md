---
layout: default
title: Database Tables Definition
---

# RDAP Database Tables Definition

This document describes database table's definition, we hope this can hel you to understand our application design and maybe, will help you to migrate your old WHOIS database information. 

RedDog's database contains 62 tables:

1. [asn_entity_roles](#asn_entity_roles)
2. [asn_events](#asn_events)
3. [asn_links](#asn_links)
4. [asn_remarks](#asn_remarks)
5. [asn_status](#asn_status)
6. [autonomous_system_number](#autonomous_system_number)
7. [country_code](#country_code)
8. [domain](#domain)
9. [domain_entity_roles](#domain_entity_roles)
10. [domain_events](#domain_events)
11. [domain_links](#domain_links)
12. [domain_nameservers](#domain_nameservers)
13. [domain_networks](#domain_networks)
14. [domain_public_ids](#domain_public_ids)
15. [domain_remarks](#domain_remarks)
16. [domain_status](#domain_status)
17. [ds_data](#ds_data)
18. [ds_events](#ds_events)
19. [ds_links](#ds_links)
20. [entity](#entity)
21. [entity_contact](#entity_contact)
22. [entity_entity_roles](#entity_entity_roles)
23. [entity_events](#entity_events)
24. [entity_links](#entity_links)
25. [entity_public_ids](#entity_public_ids)
26. [entity_remarks](#entity_remarks)
27. [entity_status](#entity_status)
28. [event](#event)
29. [event_action](#event_action)
30. [event_links](#event_links)
31. [ip_address](#ip_address)
32. [ip_network](#ip_network)
33. [ip_network_entity_roles](#ip_network_entity_roles)
34. [ip_network_events](#ip_network_events)
35. [ip_network_links](#ip_network_links)
36. [ip_network_parent_relation](#ip_network_parent_relation)
37. [ip_network_remarks](#ip_network_remarks)
38. [ip_network_status](#ip_network_status)
39. [ip_version](#ip_version)
40. [link](#link)
41. [nameserver](#nameserver)
42. [nameserver_entity_roles](#nameserver_entity_roles)
43. [nameserver_events](#nameserver_events)
44. [nameserver_links](#nameserver_links)
45. [nameserver_remarks](#nameserver_remarks)
46. [nameserver_status](#nameserver_status)
47. [public_id](#public_id)
48. [rdap_user](#rdap_user)
49. [rdap_user_role](#rdap_user_role)
50. [relation](#relation)
51. [remark](#remark)
52. [remark_description](#remark_description)
53. [remark_links](#remark_links)
54. [roles](#roles)
55. [secure_dns](#secure_dns)
56. [status](#status)
57. [variant](#variant)
58. [variant_name](#variant_name)
59. [variant_relation](#variant_relation)
60. [vcard](#vcard)
61. [vcard_postal_info](#vcard_postal_info)
62. [zone](#zone)

Each table fields and purposes are described in the following sections:

## asn_entity_roles

This table contains the role that an Entity has in relation to an ASN. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id| No | autonomous\_system\_number | asn\_id| 
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|rol\_id|tinyint(4)|Role's id| No | roles | rol\_id| 

**Primary key**: asn\_id, ent\_id, rol\_id.

## asn_events

This table contains the relation between an ASN and its events. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id| No | autonomous_system_number | asn\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: asn\_id, eve\_id.

## asn_links

This table contains the relation between an ASN and its links. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id| No | autonomous_system_number | asn\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: asn\_id, lin\_id.

## asn_remarks

This table contains the relation between an ASN and its events. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id| No | autonomous_system_number | asn\_id| 
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 

**Primary key**: asn\_id, rem\_id.

## asn_status

This table contains the relation between an ASN and its status. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id| No | autonomous_system_number | asn\_id| 
|sta\_id|bigint(20)|Status's id| No | status | sta\_id| 

**Primary key**: asn\_id, lin\_id.

## autonomous_system_number

This table contains the information of the Autonomous system numbers (ASN). Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|asn\_id|bigint(20)|ASN's id. Auto increment. Unique| No |   | | 
|asn\_handle|varchar(100)|An RIR-unique identifier of the autnum registration. Unique| No |  | | 
|asn\_start\_autnum|bigint(20)|A number representing the starting number in the block of Autonomous System numbers| No ||| 
|asn\_end\_autnum|bigint(20)|A number representing the ending number in the block of Autonomous System numbers| No ||| 
|asn\_name|varchar(200)|An identifier assigned to the autnum registration by the the registration holder.| Yes |   | | 
|asn\_type|varchar(200)|A string containing an RIR-specific classification of the autnum| Yes |   | | 
|asn\_port43|varchar(254)|A simple string containing the fully qualified host name or IP addres of the WHOIS server where the ASN instance may be found.| Yes |   | | 
|ccd\_id|smallint(5) unsigned|Country code id| No |country\_code   |cc\_id | 

**Primary key**: asn\_id, ccd\_id.

## country_code
This table contains the catalog of the two-character country code. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ccd\_id|bigint(20)|Country code's id| No |   | | 
|ccd\_code|varchar(2)|The two-character country code| No |  | | 

**Primary key**: ccd\_id

## domain
This table contains the catalog of the two-character country code. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id: Auto increment. Unique|No||| 
|dom\_handle|varchar(255)|An RIR-unique identifier of the domain registration. Unique|No|||
|dom\_ldh\_name|varchar(64)|A string describing a domain name in LDH form as described|Yes|||
|dom\_unicode\_name|varchar(255)|A string containing a domain name with U-labels||||
|dom\_port43|varchar(254)|A simple string containing the fully qualified host name or IP address of the WHOIS server where the domain instance may be found.|Yes|||
|zone\_id|smallint(6)|Zone's id|No|||

**Primary key**: dom\_id, zone\_id

## domain_entity_roles

This table contains the role that an Entity has in relation to a Domain. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|rol\_id|tinyint(4)|Role's id| No | roles | rol\_id| 

**Primary key**: dom\_id, ent\_id, rol\_id.

## domain_events

This table contains the relation between a Domain and its events. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: dom\_id, eve\_id.

## domain_links

This table contains the relation between a Domain and its links. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: dom\_id, lin\_id.

## domain_nameservers

This table contains the relation between a Domain and its nameservers. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 

**Primary key**: dom\_id, nse\_id.

## domain_networks

This table contains the relation between a Domain and its ip networks. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|ine\_id|bigint(20)|Ip Network's id| No | ip_network | ine\_id| 

**Primary key**: dom\_id, ine\_id.

## domain_public_ids

This table contains the relation between a Domain and its public ids. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|pid\_id|bigint(20)|Public id's id. Unique| No | public_id | pid\_id| 

**Primary key**: dom\_id, pid\_id.

## domain_remarks

This table contains the relation between a Domain and its remarks. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 

**Primary key**: dom\_id, rem\_id.

## domain_status

This table contains the relation between a Domain and its status. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dom\_id|bigint(20)|Domain's id| No | domain | dom\_id| 
|sta\_id|bigint(20)|Status's id| No | status | sta\_id| 

**Primary key**: dom\_id, sta\_id.

## ds_data

This table contains the information of a secure DNS DS record. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dsd\_id|bigint(20)|Ds data's id. Auto increment| No | | | 
|sdns\_id|bigint(20)|Secure DNS's id| No | secure_dns | sdns\_id| 
|dsd\_keytag|int(11)|An integer as specified by the key tag field of a DNS DS record.| No | | | 
|dsd\_algorithm|int(11)|An integer as specified by the algorithm field of a DNS DS record.| No | | | 
|dsd\_digest|varchar(255)|A string as specified by the digest field of a DNS DS record.| No | | | 
|dsd\_digest_type|int(11)|An integer as specified by the digest type field of a DNS DS record.| No | | | 

**Primary key**: dsd\_id, sdns\_id.

## ds_events

This table contains the relation between a Domain and its events. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dsd\_id|bigint(20)|Ds data's id| No | ds_data | dsd\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: dsd\_id, eve\_id.

## ds_links

This table contains the relation between a Domain and its links. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|dsd\_id|bigint(20)|DS data's id| No | ds_data | dsd\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: dsd\_id, lin\_id.

## entity

This table contains the information of organizations, corportation, governments, non-profits, clubs, individual persons, and informal groups of people. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id. Auto increment. Unique|No||| 
|ent\_handle|varchar(255)|An RIR-unique identifier of the entity registration. Unique|Yes|||
|dom\_port43|varchar(254)|A simple string containing the fully qualified host name or IP address of the WHOIS server where the entity instance may be found.| Yes|||

**Primary key**: ent\_id, zone\_id

## entity_contact

This table contains the relation between an Entity and its vcard. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|vca\_id|bigint(20)|Vcard's id| No | vcard | vca\_id| 

**Primary key**: ent\_id, vca\_id.

## entity_entity_roles

This table contains the role that an Entity has in relation to another Entity. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|main\_ent\_id|bigint(20)|Main Entity's id| No | entity | dom\_id| 
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|rol\_id|tinyint(4)|Role's id| No | roles | rol\_id| 

**Primary key**: main\_ent\_id, ent\_id, rol\_id.

## entity_events

This table contains the relation between an Entity and its events. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: ent\_id, eve\_id.

## entity_links

This table contains the relation between an Entity and its links. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id|

Primary key: ent\_id, lin\_id.

## entity_public_ids

This table contains the relation between an Entity and its public ids. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|pid\_id|bigint(20)|Public id's id. Unique| No | public\_id | pid\_id| 

**Primary key**: ent\_id, pid\_id.

## entity_remarks

This table contains the relation between a Domain and its remarks. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id|
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 

**Primary key**: ent\_id, rem\_id.

## entity_status

This table contains the relation between a Domain and its status. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|sta\_id|bigint(20)|Status's id| No | status | sta\_id| 

**Primary key**: ent\_id, sta\_id.

## event

This table contains the information about events that have occurred on an instance of an object class. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|eve\_id|bigint(20)|Event's id. Auto increment.|No||| 
|eac\_id|smallint(6)|Event's action's id|No|event\_action|eac\_id| 
|eve\_actor|varchar(45)|Event actor|Yes||| 
|eve\_date|varchar(45)|Event date|Yes||| 

**Primary key**: eve\_id.

## event_action

This table contains the catalog of events actions. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|eac\_id|smallint(6)|Event action's id| No | | | 
|eac\_name|varchar(100)|Event action's name| Yes | | | 

**Primary key**: eac\_id.

## event_links

This table contains the relation between an Event and its links. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id|

Primary key: eve\_id, lin\_id.

## ip_address

This table contains the information about the nameserver's ip addresses. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|iad\_id|int(11)|Ip address' id. Auto increment.| No ||| 
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|iad\_type|tinyint(4)|Ip address type (4 or 6)| No ||| 
|iad\_value|varbinary(16)|Ip address value| No ||| 

**Primary key**: iad\_id, nse\_id.

## ip_network
This table contains the information about the network registration and entities related to an IP network. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_id|bigint(20). Auto Increment. Unsigned|Ip network's id|No|||
|ine\_handle|varchar(255)|An RIR-unique identifier of the Ip network registration. Unique|No|||
|ine\_start\_address\_up|bigint(20) unsigned|The up part of the starting IP address of the network.|Yes|||
|ine\_start\_address\_down|bigint(20) unsigned|The down part of the starting IP address of the network.|Yes|||
|ine\_end\_address\_up|bigint(20) unsigned|The up part of the ending IP address of the network.|Yes|||
|ine\_end\_address\_down|bigint(20) unsigned|The down part of the ending IP address of the network.|Yes|||
|ine\_name|varchar(255)|An identifier assigned to the network registration by the registration holder.|Yes|||
|ine\_type|varchar(255)|A string containing a RIR-specific classification of the Network.|Yes|||
|ine\_port43|varchar(254)|A simple string containing the fully qualified host name or IP address of the WHOIS server where the Ip network instance may be found.|Yes|||
|ccd\_id|smallint(5) unsigned|Country code's id.|No|country\_code|ccd\_id|
|ip\_version\_id|tinyint(3) unsigned|Ip version's id.| No|ip\_version|ive\_id|
|ine\_parent\_handle|varchar(255)|A string containing a RIR-unique identifier of the parent network of this network registration.|Yes|||
|ine\_cidr|smallint(6)|Network mask length of the IP address.|Yes|||

**Primary key**: ine\_id.

## ip_network_entity_roles

This table contains the role that an Entity has in relation to an IP Network. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_id|bigint(20)|IP Network's id| No | ip\_network | ine\_id|
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|rol\_id|tinyint(4)|Role's id| No | roles | rol\_id| 

**Primary key**: ine\_id, ent\_id, rol\_id.

## ip_network_events

This table contains the relation between an IP Network and its events. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_id|bigint(20)|IP Network's id| No | ip_network | ine\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: ine\_id, eve\_id.

## ip_network_links

This table contains the relation between an IP Network and its links. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_id|bigint(20)|IP Network's id| No | ip_network | ine\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: ine\_id, lin\_id.

## ip_network_parent_relation

This table contains the relation between an IP Network and its parent network. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_parent\_handle|varchar(255)|Parent Network's handle| No | ip\_network | ine\_id| 
|ine\_son\_handle|varchar(255)|Son Network's handle| No | ip\_network | ine\_id| 

**Primary key**: ine\_parent\_handle, ine\_son\_handle.

## ip_network_remarks

This table contains the relation between an IP Network and its remarks. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ine\_id|bigint(20)|IP Network's id| No | ip\_network | ine\_id| 
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 

**Primary key**: ine\_id, rem\_id.

## ip_network_status

This table contains the relation between an IP Network and its status. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ent\_id|bigint(20)|IP Network's id| No | ip\_network | ine\_id| 
|sta\_id|bigint(20)|Status's id| No | status | sta\_id| 

**Primary key**: ine\_id, sta\_id.

## ip_version

This table contains the catalog of ip version types. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|ive\_id|tinyint(3) unsigned|IP version's id|No||| 
|ive\_name|varchar(2)|Ip version's name ('v4' or 'v6')| No ||| 

**Primary key**: ive\_id.

## link

This table contains the information about links. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|lin\_id|bigint(20) Auto increment.|Link's id|No||| 
|lin\_value|varchar(45)|Link's value|Yes||| 
|lin\_rel|varchar(45)|Link's rel attribute|Yes||| 
|lin\_href|varchar(45)|Link's href attribute|Yes||| 
|lin\_hreflang|varchar(45)|Link's href language|Yes||| 
|lin\_title|varchar(45)|Link's title|Yes||| 
|lin\_media|varchar(45)|Link's media attribute|Yes||| 
|lin\_type|varchar(45)|Link's type|Yes||| 

**Primary_key**: lin_id.

## nameserver

This table contains information regardin DNS nameservers used in both forward and reverse DNS. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20) Auto increment.|Nameserver's id.|No||| 
|nse\_value|varchar(100)|A RIR-unique identifier of the nameserver registration. Unique.|No||| 
|nse\_rel|varchar(254)|A string describing a nameserver name in LDH form as described.|Yes||| 
|nse\_href|varchar(255)|A string containing a nameserver name with U-labels|Yes||| 
|nse\_hreflang|varchar(254)|A simple string containing the fully qualified host name or IP address of the WHOIS server where the nameserver instance may be found.|Yes||| 

**Primary_key**: nse_id.

## nameserver_entity_roles

This table contains the role that an Entity has in relation to a Nameserver. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|ent\_id|bigint(20)|Entity's id| No | entity | ent\_id| 
|rol\_id|tinyint(4)|Role's id| No | roles | rol\_id| 

**Primary key**: nse\_id, ent\_id, rol\_id.

## nameserver_events

This table contains the relation between a Nameserver and its events. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|eve\_id|bigint(20)|Event's id| No | event | eve\_id| 

**Primary key**: nse\_id, eve\_id.

## nameserver_links

This table contains the relation between a Nameserver and its links. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: nse\_id, lin\_id.

## nameserver_remarks

This table contains the relation between a Domain and its remarks. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 

**Primary key**: nse\_id, rem\_id.

## nameserver_status

This table contains the relation between a Nameserver and its status. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|nse\_id|bigint(20)|Nameserver's id| No | nameserver | nse\_id| 
|sta\_id|bigint(20)|Status's id| No | status | sta\_id| 

**Primary key**: nse\_id, sta\_id.

## public_id

This table contains the information about links. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|pid\_id|bigint(20) Auto increment.|Public id's id|No||| 
|pid\_type|varchar(255)|Public id's type|Yes||| 
|pid\_identifier|varchar(255)|Public id's identifier|Yes||| 

**Primary_key**: pid_id.

## rdap_user

This table contains the information about the user's roles. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rus\_id|bigint(200) Auto increment.|User's id|No||| 
|rus\_name|varchar(16)|User's name. Unique|No||| 
|rus\_pass|varchar(200)|User's password.|No||| 
|rus\_max\_search\_results|int(11)|Max number of results for the user|Yes||| 

**Primary_key**: rus\_id.

## rdap_user_role

This table contains the information about the user's roles. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rus\_id|varchar(16)|User's name|No|rdap\_user|rus\_name| 
|rur\_value|varchar(45)|Role's name|No||| 

**Primary_key**: rus\_name, rur\_name.

## relation

This table contains the catalog of Variant relations. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rel\_id|tinyint(4)|Relation's id|No||| 
|rel\_type|varchar(255)|Relation's type|Yes||| 

**Primary_key**: rel\_id.

## remark

This table contains the information about the remarks, which are structure that denote information about the object class that contains them. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rem\_id|bigint(20) Auto increment.|Remark's id|No||| 
|rem\_title|varchar(255)|Remark's title.|Yes||| 
|rem\_type|varchar(255)|Remark's type.|Yes||| 
|rem\_lang|varchar(255)|Remark's language|Yes||| 

**Primary_key**: rem\_id.

## remark_description

This table contains the remark's descriptions. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rem\_id|bigint(20) Auto increment.|Remark's id|No|remark|rem_id| 
|rde\_order|mediumint(9)|Description's order.|No||| 
|rde\_description|varchar(255)|Description.|No||| 

**This table does not have a primary key.**

## remark_links

This table contains the relation between a Remark and its links. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rem\_id|bigint(20)|Remark's id| No | remark | rem\_id| 
|lin\_id|bigint(20)|Link's id| No | link | lin\_id| 

**Primary key**: rem\_id, lin\_id.

## roles

This table contains the catalog of entity's roles. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rol\_id|tinyint(4)|Role's id|No||| 
|role\_name|varchar(100)|Role's name.|Yes||| 

**Primary_key**: rol\_id.

## secure_dns

This table contains the information about the domains secure dns. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|sdns\_id|bigint(20) Auto increment.|Secure dns' id|No||| 
|sdns\_zone\_signed|tinyint(1)|1 if the zone has been signed, 0 otherwise.|No||| 
|sdns\_delegation\_signed|tinyint(1)|1 if there are DS records in the parent, 0 otherwise.|No||| 
|sdns\_max\_sig\_life|int(11)|An integer representing the signature lifetime in seconds to be used when creating the RRSIG DS record in the parent zone.|Yes||| 
|dom\_id|bigint(20)|Domain's id.|No|domain|dom\_id| 

**Primary_key**: sdns\_id.

## status

This table contains the catalog of status. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rol\_id|tinyint(4)|Role's id|No||| 
|role\_name|varchar(100)|Status's name.|Yes||| 

**Primary_key**: sta\_id.

## variant

This table contains information about the domain's variants. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|var\_id|bigint(20)|Variant's id|No||| 
|var\_idn\_table|varchar(100)|Variant's IDN table.|Yes||| 
|dom\_id|bigint(20)|Domain's id|No|domain|dom\_id| 

**Primary_key**: var\_id, dom\_id.

## variant_name

This table contains the catalog of status. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|var\_ldh\_name|varchar(63)|Variant's ldh name.|Yes||| 
|var\_id|bigint(20)|Variant's id|No|variant|var\_id| 

**This table does not have a primary key.**

## variant_relation

This table contains the relation between a Variant and its relation. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|rel\_id|tinyint(4)|Relation's id.|No|relation|rel\_id| 
|var\_id|bigint(20)|Variant's id.|No|variant|var\_id| 

**Primary_key**: rel\_id, var\_id.

## vcard

This table contains the catalog of status. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|vca\_id|bigint(20) auto increment.|Vcard's id.|No||| 
|vca\_name|varchar(100)|Contact's name.|Yes||| 
|vca\_company\_name|varchar(255)|Contact's company name.|Yes||| 
|vca\_company\_url|varchar(255)|Contact's url.|Yes||| 
|vca\_email|varchar(200)|Contact's email.|Yes||| 
|vca\_voice|varchar(50)|Contact's telephone.|Yes||| 
|vca\_cellphone|varchar(50)|Contact's cellphone.|Yes||| 
|vca\_fax|varchar(50)|Contact's fax.|Yes||| 
|vca\_job\_title|varchar(200)|Contact's job title.|Yes||| 

**Primary_key**: vca\_id.

## vcard_postal_info

This table contains the catalog of status. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|vpi\_id|bigint(20) auto increment.|Postal info's id.|No||| 
|vca\_id|bigint(20)|Vcard's id.|No|vcard|vca\_id| 
|vpi\_type|varchar(45)|Postal info's type.|Yes||| 
|vpi\_country|bigint(100)|Country|Yes||| 
|vpi\_city|bigint(100)|City|Yes||| 
|vpi\_street1|bigint(100)|Street|Yes||| 
|vpi\_street2|bigint(100)|Street|Yes||| 
|vpi\_street3|bigint(100)|Street|Yes||| 
|vpi\_state|bigint(100)|State|Yes||| 
|vpi\_postal\_code|bigint(100)|Postal code|Yes||| 

**Primary_key**: vpi\_id, vca\_id.

## zone

This table contains the zones managed by the RDAP server. Its fields are the following:

|Column name|Column type|Column description|Nullable|Referenced table|Referenced column|
|:----------|:----------|:-----------------|:-------|:---------------|:---------------:|
|zone\_id|smallint(6) Auto increment|Zone's id.|No||| 
|zone\_name|varchar(254)|Zone's name.|No||| 

**Primary_key**: zone\_id.





