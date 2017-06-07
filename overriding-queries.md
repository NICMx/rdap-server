---
title: Overriding Red Dog Queries
---

# Overriding Red Dog Queries

## Introduction

This document is a formal definition of the SQL Provider’s query interface. Users that aim to implement [Option 2](intro.html#option-2-overriding-sql-provider-queries) need to provide queries that fulfill these requirements.

## How to override queries

The queries are expected to be found in a directory called `user_sql_files/`. This directory should be a collection of `.sql` files, mirroring the ones from the [default implementation](https://github.com/NICMx/rdap-sql-provider/tree/master/src/main/resources/META-INF/sql). The [installation document](server-install-option-2.html) states where this directory should be placed to be detected by Red Dog.

Except for queries we refer to as “catalogs”, you are not expected to override all the objects, and within each object you also don’t necessarily need to define every query. You should identify and provide only the ones you need. Red Dog will return [HTTP 501](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#501) when requested information that requires a query you don’t define.

Each file should list the queries needed to interact with the object, and name them by means of lines starting with the ‘#’ character. As an example, here’s how one might implement `Autnum.sql`:

	#getByRange
	SELECT asn.asn_id, asn.asn_handle, asn.asn_start_autnum, asn.asn_end_autnum, asn.asn_name, asn.asn_type, asn.asn_port43, asn.ccd_id 
	FROM {schema}.autonomous_system_number asn 
	WHERE asn.asn_start_autnum <= ? AND asn.asn_end_autnum >= ?;

	#getAutnumByEntity
	SELECT asn.asn_id, asn.asn_handle, asn.asn_start_autnum, asn.asn_end_autnum, asn.asn_name, asn.asn_type, asn.asn_port43, asn.ccd_id 
	FROM {schema}.autonomous_system_number asn 
	JOIN {schema}.asn_entity_roles ent ON ent.asn_id = asn.asn_id 
	WHERE ent.ent_id = ?;

The names of the columns and the number and order of parameters (‘?’ symbols) need to match the requirements listed for each query in the following sections. It should be noted that SQL aliases can be used to rename columns when the query alone would yield some other name.

The Red Dog implemented SQL files are the following:

*	[RdapUser.sql](#rdapusersql)
*	[Domain.sql](#domainsql)
*	[Entity.sql](#entitysql)
*	[Nameserver.sql](#nameserversql)
*	[Autnum.sql](#autnumsql)
*	[IpNetwork.sql](#ipnetworksql)
*	[Event.sql](#eventsql)
*	[IpAddress.sql](#ipaddresssql)
*	[Link.sql](#linksql)
*	[PublicId.sql](#publicidsql)
*	[RdapUserRole.sql](#rdapuserrolesql)
*	[Remark.sql](#remarksql)
*	[RemarkDescription.sql](#remarkdescriptionsql)
*	[SecureDNS.sql](#securednssql)
*	[DsData.sql](#dsdatasql)
*	[KeyData.sql](#keydatasql)
*	[Variant.sql](#variantsql)
*	[VCard.sql](#vcardsql)
*	[VCardPostalInfo.sql](#vcardpostalinfosql)
*	[Zone.sql](#zonesql)

The Red Dog SQL files calling catalogs and other needed catalogs:
(You must make sure you have all these implemented)

*	[Role.sql](#rolesql)
*	[Status.sql](#statussql)
*	[EventAction](#eventaction)
*	[VariantRelation](#variantrelation)
*	[CountryCode.sql](#countrycodesql)

## Objects

### RdapUser.sql

This file calls an Entity that has authority to use the server.

The following table describes each value we expect you to retrieve with your own queries:

|Alias Name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|rus_id|	Long|	No|	User's id|	123|
|rus_name|	String|	No|	User's name. Unique|	user123|
|rus_max_search_results|	Integer|	Yes|	Max number of results for the user|	100|

#### Queries

Your new SQL file must be able to handle the following queries and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByName
	SELECT rus.rus_id, rus.rus_name, rus.rus_pass, rus.rus_max_search_results
	FROM {schema}.rdap_user rus WHERE rus.rus_name=?;

	#getMaxSearchResults
	SELECT rus.rus_max_search_results FROM  rdap.rdap_user rus WHERE rus.rus_name=?;

### Domain.sql

This files calls a domain object, this represents a DNS name and point of delegation. For RIRs, these delegation points are in the reverse DNS tree, whereas for DNRs, these delegation points are in the forward DNS tree.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|dom_id|	Long|	No|	Domain's id.|	123|
|dom_handle|	String|	Yes|	An RIR-unique identifier of the domain registration. Unique|	XXXX|
|dom_ldh_name|	String|	Yes|	A string describing a domain name in LDH form as described|	xn--exampl-gva|
|dom_unicode_name|	String|	Yes|	A string containing a domain name with U-labels|	examplé|
|dom_port43|	String|	Yes|	A simple string containing the fully qualified host name or IP address of the WHOIS server where the domain instance may be found.	|whois.example|
|zone_id|	Integer|	No|	Zone's id(Refer to [Zone.sql](#zonesql) as it is also needed)| 	1|

#### Queries

Your new SQL file must be able to handle the following queries and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByLdhName
	SELECT dom_id, dom_handle, dom_ldh_name, dom_unicode_name, dom_port43, zone_id 
	FROM {schema}.domain 
	WHERE (dom_ldh_name=? OR dom_unicode_name=?) AND zone_id = ?;

	#searchByPartialNameWZone
	SELECT domain.dom_id, domain.dom_handle, domain.dom_ldh_name, domain.dom_unicode_name, domain.dom_port43, domain.zone_id 
	FROM {schema}.domain 
	WHERE (domain.dom_ldh_name LIKE ? OR domain.dom_unicode_name LIKE ? ) AND domain.zone_id = ? ORDER BY 1 LIMIT ?;

	#searchByNameWZone
	SELECT domain.dom_id, domain.dom_handle, domain.dom_ldh_name, domain.dom_unicode_name, domain.dom_port43, domain.zone_id 
	FROM {schema}.domain 
	WHERE (dom_ldh_name=? OR dom_unicode_name=?) AND domain.zone_id = ? ORDER BY 1 LIMIT ?;

	#searchByPartialNameWPartialZone
	SELECT DISTINCT(d.dom_id), d.dom_handle, d.dom_ldh_name, d.dom_port43, d.zone_id, d.dom_unicode_name 
	FROM {schema}.domain d 
	JOIN {schema}.zone z on d.zone_id = z.zone_id AND z.zone_id IN (?) 
	WHERE (d.dom_ldh_name LIKE ? OR d.dom_unicode_name LIKE ?) AND z.zone_name like ? LIMIT ?;

	#searchByNameWPartialZone
	SELECT DISTINCT(d.dom_id), d.dom_handle, d.dom_ldh_name, d.dom_port43, d.zone_id, d.dom_unicode_name 
	FROM {schema}.domain d 
	JOIN {schema}.zone z on d.zone_id = z.zone_id AND z.zone_id IN (?) 
	WHERE (d.dom_ldh_name = ? OR d.dom_unicode_name = ?) AND z.zone_name like ? LIMIT ?;

	#searchByNameWOutZone
	SELECT d.dom_id, d.dom_handle, d.dom_ldh_name, d.dom_unicode_name, d.dom_port43, d.zone_id 
	FROM {schema}.domain d 
	WHERE d.zone_id IN (?)  AND (d.dom_ldh_name = ? OR d.dom_unicode_name = ?) ORDER BY 1 LIMIT ?;

	#searchByPartialNameWOutZone
	SELECT d.dom_id, d.dom_handle, d.dom_ldh_name, d.dom_unicode_name, d.dom_port43, d.zone_id 
	FROM {schema}.domain d 
	WHERE d.zone_id IN (?) AND (d.dom_ldh_name LIKE ? OR d.dom_unicode_name LIKE ?) ORDER BY 1 LIMIT ?;

	#searchByNsLdhName
	SELECT DISTINCT (dom.dom_id), dom.dom_ldh_name, dom.dom_handle, dom.dom_port43, dom.zone_id, dom.dom_unicode_name 
	FROM {schema}.domain dom 
	JOIN {schema}.domain_nameservers dom_ns ON dom_ns.dom_id = dom.dom_id 
	JOIN {schema}.nameserver ns ON ns.nse_id = dom_ns.nse_id 
	WHERE  (ns.nse_ldh_name LIKE ? OR ns.nse_unicode_name LIKE ?) ORDER BY 1 LIMIT ?;

	#searchByNsIp
	SELECT DISTINCT (dom.dom_id), dom.dom_ldh_name, dom.dom_handle, dom.dom_port43, dom.zone_id, dom.dom_unicode_name 
	FROM {schema}.domain dom 
	JOIN {schema}.domain_nameservers dom_ns ON dom_ns.dom_id = dom.dom_id 
	JOIN {schema}.nameserver ns ON ns.nse_id = dom_ns.nse_id JOIN {schema}.ip_address ip ON ip.nse_id = ns.nse_id 
	WHERE IF(?=4, INET_ATON(?),INET6_ATON(?)) = ip.iad_value ORDER BY 1 LIMIT ?;

	#existByNsLdhName
	SELECT EXISTS(
		SELECT 1 
		FROM {schema}.domain dom 
		JOIN {schema}.domain_nameservers dom_ns ON dom_ns.dom_id = dom.dom_id 
		JOIN {schema}.nameserver ns ON ns.nse_id = dom_ns.nse_id 
		WHERE ( ns.nse_ldh_name LIKE ? OR ns.nse_unicode_name LIKE ?));

	#existByNsIp
	SELECT EXISTS(
		SELECT 1 
		FROM {schema}.domain dom 
		JOIN {schema}.domain_nameservers dom_ns ON dom_ns.dom_id = dom.dom_id 
		JOIN {schema}.nameserver ns ON ns.nse_id = dom_ns.nse_id 
		JOIN {schema}.ip_address ip	ON ip.nse_id = ns.nse_id 
		WHERE IF(?=4, INET_ATON(?),INET6_ATON(?)) = ip.iad_value);

	#searchByRegexNameWithZone
	SELECT DISTINCT(d.dom_id), d.dom_handle, d.dom_ldh_name, d.dom_port43, d.zone_id, d.dom_unicode_name 
	FROM {schema}.domain d 
	JOIN {schema}.zone z on d.zone_id = z.zone_id AND z.zone_id IN (?) 
	WHERE (d.dom_ldh_name REGEXP ? OR d.dom_unicode_name REGEXP ?) AND z.zone_name REGEXP ? LIMIT ?;

	#searchByRegexNameWithOutZone
	SELECT DISTINCT(d.dom_id), d.dom_handle, d.dom_ldh_name, d.dom_port43, d.zone_id, d.dom_unicode_name 
	FROM {schema}.domain d 
	WHERE d.zone_id IN (?) AND (d.dom_ldh_name REGEXP ? OR d.dom_unicode_name REGEXP ?) ORDER BY 1 LIMIT ?;

	#searchByRegexNsLdhName
	SELECT DISTINCT (dom.dom_id), dom.dom_ldh_name, dom.dom_handle, dom.dom_port43, dom.zone_id, dom.dom_unicode_name 
	FROM {schema}.domain dom 
	JOIN {schema}.domain_nameservers dom_ns ON dom_ns.dom_id = dom.dom_id 
	JOIN {schema}.nameserver ns ON ns.nse_id = dom_ns.nse_id 
	WHERE  (ns.nse_ldh_name REGEXP ? OR ns.nse_unicode_name REGEXP ?) ORDER BY 1 LIMIT ?;

### Entity.sql

This file calls an Entity which represents the information of organizations, corporations, governments, non-profits, clubs, individual persons, and informal groups of people.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|ent_id|	Long|	No|	The entity's id assigned in your database.|	123|
|ent_handle|	String|	Yes|	The entity's id assigned in your database.|	XXXX|
|ent_port43|	String|	Yes|	The host or ip address of the WHOIS server where the entity instance may be found.|	whois.example.net|

#### Queries

Your new SQL file must be able to handle the following queries and use the same aliases so both Red Dog's implementation and your database columns coincide:

	#getByHandle
	SELECT ent_id, ent_handle, ent_port43
	FROM {schema}.entity e 
	WHERE e.ent_handle = ?;

	#getByHandle
	SELECT ent_id, ent_handle, ent_port43
	FROM {schema}.entity e 
	WHERE e.ent_handle = ?;

	#getByDomain
	SELECT ent.ent_id, ent.ent_handle, ent.ent_port43, dom.rol_id 
	FROM {schema}.entity ent 
	JOIN {schema}.domain_entity_roles dom ON dom.ent_id=ent.ent_id 
	WHERE dom.dom_id=?;

	#getEntitysEntitiesQuery
	SELECT DISTINCT (ent.ent_id), ent.ent_handle, ent.ent_port43 
	FROM {schema}.entity ent 
	JOIN {schema}.entity_entity_roles rol ON rol.ent_id = ent.ent_id 
	WHERE rol.main_ent_id = ?;

	#getDomainsEntitiesQuery
	SELECT DISTINCT (ent.ent_id), ent.ent_handle, ent.ent_port43 
	FROM {schema}.entity ent 
	JOIN {schema}.domain_entity_roles rol ON rol.ent_id = ent.ent_id 
	WHERE rol.dom_id = ?;

	#getNameserversEntitiesQuery
	SELECT DISTINCT (ent.ent_id), ent.ent_handle, ent.ent_port43 
	FROM {schema}.entity ent 
	JOIN {schema}.nameserver_entity_roles rol ON rol.ent_id = ent.ent_id 
	WHERE rol.nse_id = ?;

	#getAutnumEntitiesQuery
	SELECT DISTINCT (ent.ent_id), ent.ent_handle, ent.ent_port43 
	FROM {schema}.entity ent 
	JOIN {schema}.asn_entity_roles rol ON rol.ent_id = ent.ent_id 
	WHERE rol.asn_id = ?;

	#getIpNetworkEntitiesQuery
	SELECT DISTINCT (ent.ent_id), ent.ent_handle, ent.ent_port43 
	FROM {schema}.entity ent 
	JOIN {schema}.ip_network_entity_roles rol ON rol.ent_id = ent.ent_id 
	WHERE rol.ine_id = ?;

	#searchByPartialHandle
	SELECT ent_id, ent_handle, ent_port43
	FROM {schema}.entity e 
	WHERE e.ent_handle LIKE ? ORDER BY 1 LIMIT ?;

	#searchByPartialName
	SELECT DISTINCT (ent.ent_id), ent.ent_handle, ent.ent_port43 
	FROM {schema}.entity ent 
	JOIN {schema}.entity_contact eco ON eco.ent_id=ent.ent_id 
	JOIN {schema}.vcard vca ON vca.vca_id=eco.vca_id 
	WHERE vca.vca_name LIKE ? ORDER BY 1 LIMIT ?;

	#getByName
	SELECT ent_id, ent_handle, ent_port43
	FROM {schema}.entity ent 
	JOIN {schema}.entity_contact eco ON eco.ent_id=ent.ent_id 
	JOIN {schema}.vcard vca ON vca.vca_id=eco.vca_id 
	WHERE vca.vca_name = ?;

	#searchByHandle
	SELECT ent_id, ent_handle, ent_port43
	FROM {schema}.entity e 
	WHERE e.ent_handle = ? ORDER BY 1 LIMIT ?;

	#searchByName
	SELECT ent_id, ent_handle, ent_port43
	FROM {schema}.entity ent 
	JOIN {schema}.entity_contact eco ON eco.ent_id=ent.ent_id 
	JOIN {schema}.vcard vca ON vca.vca_id=eco.vca_id 
	WHERE vca.vca_name = ? ORDER BY 1 LIMIT ?;

	#existByPartialName
	SELECT EXISTS(
		SELECT 1 
		FROM {schema}.entity ent 
		JOIN {schema}.entity_contact eco ON eco.ent_id=ent.ent_id 
		JOIN {schema}.vcard vca ON vca.vca_id=eco.vca_id 
		WHERE vca.vca_name LIKE ?);

	#existByName
	SELECT EXISTS(
		SELECT 1 
		FROM {schema}.entity ent 
		JOIN {schema}.entity_contact eco ON eco.ent_id=ent.ent_id 
		JOIN {schema}.vcard vca ON vca.vca_id=eco.vca_id 
		WHERE vca.vca_name = ?);

	#searchByRegexHandle
	SELECT ent_id, ent_handle, ent_port43
	FROM {schema}.entity e 
	WHERE e.ent_handle REGEXP ? ORDER BY 1 LIMIT ?;

	#searchByRegexName
	SELECT DISTINCT (ent.ent_id), ent.ent_handle, ent.ent_port43 
	FROM {schema}.entity ent 
	JOIN {schema}.entity_contact eco ON eco.ent_id=ent.ent_id 
	JOIN {schema}.vcard vca ON vca.vca_id=eco.vca_id 
	WHERE vca.vca_name REGEXP ? ORDER BY 1 LIMIT ?;

### Nameserver.sql

This file calls a Nameserver which represents information regarding DNS nameservers used in both forward and reverse DNS.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|nse_id|	Long|	No|	Nameserver's id.|	123|
|nse_handle|	String|	Yes|	A RIR-unique identifier of the nameserver registration. Unique.|	XXXXX|
|nse_ldh_name|	String|	Yes|	A string describing a nameserver name in LDH form as described.	ns1.| xn--exampl-gva|
|nse_unicode_name|	String|	Yes|	A string containing a nameserver name with U-labels|	ns1.examplé|
|nse_port43|	String|	Yes|	A simple string containing the fully qualified host name or IP address of the WHOIS server where the nameserver instance may be found.|	whois.example|

#### Queries

Your new SQL file must be able to handle the following query and use aliases so both Red Dog's implementation and your database columns coincide:

	#findByName
	SELECT nse.nse_id, nse.nse_handle, nse.nse_ldh_name, nse.nse_unicode_name, nse.nse_port43 
	FROM {schema}.nameserver nse 
	WHERE nse.nse_ldh_name=? OR nse.nse_unicode_name = ?;

	#getByDomainId
	SELECT nse.nse_id, nse.nse_handle, nse.nse_ldh_name, nse.nse_unicode_name, nse.nse_port43 
	FROM {schema}.nameserver nse 
	JOIN {schema}.domain_nameservers dom ON dom.nse_id=nse.nse_id 
	WHERE dom.dom_id=?;

	#searchByPartialName
	SELECT DISTINCT(nse.nse_id), nse.nse_handle,nse.nse_ldh_name, nse.nse_port43, nse.nse_unicode_name 
	FROM {schema}.nameserver nse 
	WHERE nse.nse_ldh_name LIKE ? OR nse.nse_unicode_name LIKE ? ORDER BY 1 LIMIT ?;

	#searchByName
	SELECT DISTINCT(nse.nse_id), nse.nse_handle,nse.nse_ldh_name, nse.nse_port43, nse.nse_unicode_name 
	FROM {schema}.nameserver nse 
	WHERE nse.nse_ldh_name=? OR nse.nse_unicode_name=? ORDER BY 1 LIMIT ?;

	#searchByIp4
	SELECT DISTINCT(nse.nse_id), nse.nse_handle,nse.nse_ldh_name, nse.nse_port43, nse.nse_unicode_name 
	FROM {schema}.nameserver nse 
	JOIN {schema}.ip_address ipa ON ipa.nse_id=nse.nse_id 
	WHERE ipa.iad_value=INET_ATON(?) ORDER BY 1 LIMIT ?;

	#searchByIp6
	SELECT DISTINCT(nse.nse_id), nse.nse_handle,nse.nse_ldh_name, nse.nse_port43, nse.nse_unicode_name 
	FROM {schema}.nameserver nse 
	JOIN {schema}.ip_address ipa ON ipa.nse_id=nse.nse_id 
	WHERE ipa.iad_value=INET6_ATON(?) ORDER BY 1 LIMIT ?;

	#searchByRegexName
	SELECT DISTINCT(nse.nse_id), nse.nse_handle,nse.nse_ldh_name, nse.nse_port43, nse.nse_unicode_name 
	FROM {schema}.nameserver nse 
	WHERE nse.nse_ldh_name REGEXP ? OR nse.nse_unicode_name REGEXP ? ORDER BY 1 LIMIT ?;


### Autnum.sql

This file calls an Autnum object which models Autonomous System number registrations found in RIRs.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|asn_id|	Long|	No|	ASN's id.|	123|
|asn_handle|	String|	No|	An RIR-unique identifier of the autnum registration. Unique|	XXXXX|
|asn_start_autnum|	Long|	No|	A number representing the starting number in the block of Autonomous System numbers|	20|
|asn_end_autnum|	Long|	No|	A number representing the ending number in the block of Autonomous System numbers|	25|
|asn_name|	String|	Yes|	An identifier assigned to the autnum registration by the registration holder.|	asn1324|
|asn_type|	String|	Yes|	A string containing an RIR-specific classification of the autnum|	public|
|asn_port43|	String|	Yes|	A simple string containing the fully qualified host name or IP address of the WHOIS server where the ASN instance may be found.	|whois.example|
|ccd_id|	Integer|	No|	Country code id. (Refer to [Country Codes catalog]((#countrycodesql)))|	484|


#### Queries

Your new SQL file must be able to handle the following queries and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByRange
	SELECT asn.asn_id, asn.asn_handle, asn.asn_start_autnum, asn.asn_end_autnum, asn.asn_name, asn.asn_type, asn.asn_port43, asn.ccd_id 
	FROM {schema}.autonomous_system_number asn 
	WHERE asn.asn_start_autnum <= ? AND asn.asn_end_autnum >= ?;

	#getAutnumByEntity
	SELECT asn.asn_id, asn.asn_handle, asn.asn_start_autnum, asn.asn_end_autnum, asn.asn_name, asn.asn_type, asn.asn_port43, asn.ccd_id 
	FROM {schema}.autonomous_system_number asn 
	JOIN {schema}.asn_entity_roles ent ON ent.asn_id = asn.asn_id 
	WHERE ent.ent_id = ?;

### IpNetwork.sql

This file calls an IpNetwork object which models IP network registrations found in RIRs and contains information about the network registration and entities related to the IP network.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|ine_id|	Long|	No|	Ip network's id.|	123|
|ine_handle|	String|	No|	An RIR-unique identifier of the Ip network registration. Unique|	XXXXX|
|ine_start_address_up|	String|	Yes|	The up part of the starting IP address of the network.|	2306144275399704592|
|ine_start_address_down|	String|	Yes|	The down part of the starting IP address of the network.|	0|
|ine_end_address_up|	String|	Yes|	The up part of the ending IP address of the network.|	2306144275399704592|
|ine_end_address_down|	String|	Yes|	The down part of the ending IP address of the network.|	18446744073709551615|
|ine_name|	String|	Yes|	An identifier assigned to the network registration by the registration holder.|	some_name|
|ine_type|	String|	Yes|	A string containing a RIR-specific classification of the Network.|	private|
|ine_port43|	String|	Yes|	A simple string containing the fully qualified host name or IP address of the WHOIS server where the Ip network instance may be found.	|whois.example|
|ccd_id|	Integer|	No|	Country code's id. (Refer to [Country Codes catalog]((#countrycodesql)))|	484|
|ip_version_id|	Integer|	No|	Ip version's id.|	6|
|ine_parent_handle|	String|	Yes|	A string containing a RIR-unique identifier of the parent network of this network registration.|	XXXX|
|ine_cidr|	Integer|	Yes|	Network mask length of the IP address.|	64|


#### Queries

Your new SQL file must be able to handle the following queries and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByIPv4
	SELECT ine_id, ine_handle, ine_start_address_up, ine_start_address_down, ine_end_address_up, ine_end_address_down, ine_name, ine_type, ine_port43, ccd_id, ine_version_id, ip_version_id, ine_parent_handle, ine_cidr
	FROM {schema}.ip_network ipn 
	WHERE ip_version_id = 4 AND ine_cidr <= ? AND ine_start_address_down <= ? AND ine_end_address_down >= ? ORDER BY ine_cidr DESC;

	#getByIPv6
	SELECT ine_id, ine_handle, ine_start_address_up, ine_start_address_down, ine_end_address_up, ine_end_address_down, ine_name, ine_type, ine_port43, ccd_id, ine_version_id, ip_version_id, ine_parent_handle, ine_cidr
	FROM {schema}.ip_network 
	WHERE ip_version_id = 6 AND ine_cidr <= ? AND ine_start_address_up <= ? AND ine_start_address_down <= ? AND ine_end_address_up >= ? AND ine_end_address_down >= ? ORDER BY ine_cidr DESC;

	#getByEntityId
	SELECT ipn.ine_id, ipn.ine_handle, ipn.ine_start_address_up, ipn.ine_start_address_down, ipn.ine_end_address_up, ipn.ine_end_address_down, ipn.ine_name, ipn.ine_type, ipn.ine_port43, ipn.ccd_id, ipn.ine_version_id, ipn.ip_version_id, ipn.ine_parent_handle, ipn.ine_cidr 
	FROM {schema}.ip_network ipn 
	JOIN {schema}.ip_network_entity_roles ent ON ent.ine_id = ipn.ine_id 
	WHERE ent.ent_id = ?;

	#getByDomainId
	SELECT ipn.ine_id, ipn.ine_handle, ipn.ine_start_address_up, ipn.ine_start_address_down, ipn.ine_end_address_up, ipn.ine_end_address_down, ipn.ine_name, ipn.ine_type, ipn.ine_port43, ipn.ccd_id, ipn.ine_version_id, ipn.ip_version_id, ipn.ine_parent_handle, ipn.ine_cidr
	FROM {schema}.ip_network ipn 
	JOIN {schema}.domain_networks dom ON dom.ine_id = ipn.ine_id 
	WHERE dom.dom_id = ?;

	#existByIPv4
	SELECT EXISTS(
		SELECT 1 
		FROM {schema}.ip_network 
		WHERE ip_version_id = 4 AND ine_cidr <= ? AND ine_start_address_down <= ? AND ine_end_address_down >= ?);

	#existByIPv6
	SELECT EXISTS(
		SELECT 1  
		FROM {schema}.ip_network 
		WHERE ip_version_id = 6 AND ine_cidr <= ? AND ine_start_address_up <= ? AND ine_start_address_down <= ? AND ine_end_address_up >= ? AND ine_end_address_down >= ? );

### Event.sql

This data structure represents events that have occurred on an instance of an object.  

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|eve_id|	Long|	No|	Event's id.| 	123|
|eac_id|	Integer|	No|	Event's action's id. (Needs to be retrieved from the [EventAction catalog](#eventaction).)| 1|
|eve_actor|	String|	Yes|	Event actor.|	XXXXX|
|eve_date|	TimeStamp|	Yes|	Event date.|	2011-12-31T23:59:59Z|

#### Queries

Your new SQL file must be able to handle the following queries and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByNameServerId
	SELECT eve.eve_id,eve.eac_id,eve.eve_actor,eve.eve_date 
	FROM {schema}.event eve 
	JOIN {schema}.nameserver_events nse ON nse.eve_id=eve.eve_id
	WHERE nse.nse_id=?;

	#getByDsDataId
	SELECT eve.eve_id,eve.eac_id,eve.eve_actor,eve.eve_date 
	FROM {schema}.event eve 
	JOIN {schema}.ds_events dse ON dse.eve_id=eve.eve_id 
	WHERE dse.dsd_id=?;

	#getByDomainId
	SELECT eve.eve_id,eve.eac_id,eve.eve_actor,eve.eve_date 
	FROM {schema}.event eve 
	JOIN {schema}.domain_events dome ON dome.eve_id=eve.eve_id 
	WHERE dome.dom_id=?;

	#getByEntityId
	SELECT eve.eve_id,eve.eac_id,eve.eve_actor,eve.eve_date 
	FROM {schema}.event eve 
	JOIN {schema}.entity_events ent ON ent.eve_id=eve.eve_id 
	WHERE ent.ent_id=?;

	#getByAutnumId
	SELECT eve.eve_id, eve.eac_id, eve.eve_actor, eve.eve_date 
	FROM {schema}.event eve 
	JOIN {schema}.asn_events asn ON asn.eve_id=eve.eve_id 
	WHERE asn.asn_id=?;

	#getByIpNetworkId
	SELECT eve.eve_id, eve.eac_id, eve.eve_actor, eve.eve_date 
	FROM {schema}.event eve 
	JOIN {schema}.ip_network_events ine ON ine.eve_id=eve.eve_id 
	WHERE ine.ine_id=?;

	#getByKeyDataId
	SELECT eve.eve_id,eve.eac_id,eve.eve_actor,eve.eve_date 
	FROM {schema}.event eve 
	JOIN {schema}.key_events kde ON kde.eve_id=eve.eve_id 
	WHERE kde.kd_id=?;

### IpAddress.sql

This data structure represents the ip addresses of a nameserver.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|iad_id|Long|No|Ip address' id.|123|
|nse_id|Long|No|Nameserver's id.|123|
|iad_type|Integer|No|	Ip address type (4 or 6)|4|
|iad_value|	String|	No|	Ip address value.|4204805978|


#### Queries

Your new SQL file must be able to handle the following query and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByNameserverId
	SELECT iad.iad_id,iad.nse_id,iad.iad_type, 
		IF(iad.iad_type=4,INET_NTOA(iad.iad_value),INET6_NTOA(iad.iad_value)) as iad_value 
	FROM rdap.ip_address iad 
	WHERE iad.nse_id=?;

### Link.sql

This data structure represents a link used in your RDAP server.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|lin_id|	Long|	No|	Link id.|	123|
|lin_value|	String|	Yes|	A String containing value field of a link.|	http://example.net/ip/201.0.0.0/|
|lin_rel|	String|	Yes|	A String containing link relation.|	otherself|
|lin_href|	String|	Yes|	A String containing the  redirecting URL.|	http://example.net/ip/201.0.0.0/8|
|lin_title|	String|	Yes|	A String containing this link title.|	title|
|lin_media|	String|	Yes|	A String containing the information style of thye content of this link.|	screen|
|lin_type|	String|	Yes|	A link containing the media type used.|	application/rdap+json|
|lan_hreflang|	String|	No|	A string containing the prefix of the language used in this link.|	en|


#### Queries

Your new SQL file must be able to handle the following queries and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByNameServerId
	SELECT lin.lin_id, lin.lin_value, lin.lin_rel, lin.lin_href, lin.lin_title, lin.lin_media, lin.lin_type 
	FROM {schema}.link lin 
	JOIN {schema}.nameserver_links nse ON nse.lin_id=lin.lin_id 
	WHERE nse.nse_id=?;

	#getByEventId
	SELECT lin.lin_id, lin.lin_value, lin.lin_rel, lin.lin_href, lin.lin_title, lin.lin_media, lin.lin_type
	FROM {schema}.link lin 
	JOIN {schema}.event_links eve ON eve.lin_id=lin.lin_id 
	WHERE eve.eve_id=?;

	#getByDsDataId
	SELECT lin.lin_id, lin.lin_value, lin.lin_rel, lin.lin_href, lin.lin_title, lin.lin_media, lin.lin_type
	FROM {schema}.link lin J
	OIN {schema}.ds_links dsd ON dsd.lin_id=lin.lin_id x
	WHERE dsd.dsd_id=?;

	#getByDomainId
	SELECT lin.lin_id, lin.lin_value, lin.lin_rel, lin.lin_href, lin.lin_title, lin.lin_media, lin.lin_type
	FROM {schema}.link lin 
	JOIN {schema}.domain_links dom ON dom.lin_id=lin.lin_id 
	WHERE dom.dom_id=?;

	#getByEntityId
	SELECT lin.lin_id, lin.lin_value, lin.lin_rel, lin.lin_href, lin.lin_title, lin.lin_media, lin.lin_type
	FROM {schema}.link lin 
	JOIN {schema}.entity_links ent ON ent.lin_id=lin.lin_id 
	WHERE ent.ent_id=?;

	#getByAutnumId
	SELECT lin.lin_id, lin.lin_value, lin.lin_rel, lin.lin_href, lin.lin_title, lin.lin_media, lin.lin_type
	FROM {schema}.link lin 
	JOIN {schema}.asn_links asn ON asn.lin_id=lin.lin_id 
	WHERE asn.asn_id = ?;

	#getByIpNetworkId
	SELECT lin.lin_id, lin.lin_value, lin.lin_rel, lin.lin_href, lin.lin_title, lin.lin_media, lin.lin_type
	FROM {schema}.link lin 
	JOIN {schema}.ip_network_links ine ON ine.lin_id=lin.lin_id 
	WHERE ine.ine_id=?;

	#getByKeyDataId
	SELECT lin.lin_id, lin.lin_value, lin.lin_rel, lin.lin_href, lin.lin_title, lin.lin_media, lin.lin_type
	FROM {schema}.link lin 
	JOIN {schema}.key_links kdl ON kdl.lin_id=lin.lin_id 
	WHERE kdl.kd_id=?;

	#getByRemarkId
	SELECT lin.lin_id, lin.lin_value, lin.lin_rel, lin.lin_href, lin.lin_title, lin.lin_media, lin.lin_type
	FROM {schema}.link lin 
	JOIN {schema}.remark_links rem ON rem.lin_id=lin.lin_id 
	WHERE rem.rem_id=?;

	#getLinkHreflangs
	SELECT lan_hreflang 
	FROM {schema}.link_lang lan 
	WHERE lan.lin_id = ?;

### PublicId.sql

This data structure represents the Key Data of a Secure DNS.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|pid_id|	Long|	No|	Public id's id.|	123|
|pid_type|	String|	Yes|	Public id's type.|	IANA Registrar ID|
|pid_identifier|	String|	Yes|	Public id's identifier.|	256|

#### Queries

Your new SQL file must be able to handle the following queries and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByDomain
	SELECT pid.* 
	FROM {schema}.public_id pid INNER 
	JOIN {schema}.domain_public_ids dom ON pid.pid_id = dom.pid_id 
	WHERE dom.dom_id=?;

	#getByEntity
	SELECT pid.* 
	FROM {schema}.public_id pid INNER 
	JOIN {schema}.entity_public_ids ent ON pid.pid_id = ent.pid_id 
	WHERE ent.ent_id=?;

### RdapUserRole.sql

This data structure represents the Role of an User.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|rus_name|	String|	No|	Public id's id.|	Username|
|rur_name|	String|	No|	Public id's type.|	OWNER|

#### Queries

Your new SQL file must be able to handle the following query and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByUserName
	SELECT rur.rus_name, rur.rur_name 
	FROM {schema}.rdap_user_role rur 
	WHERE rur.rus_name=?;

### Remark.sql

This data structure represents a Remark.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|rem_id|	Long|	No|	Remark's id.|	123|
|rem_title|	String|	Yes|	Remark's title.|	Title|
|rem_type|	String|	Yes|	Remark's type.|	Advice|
|rem_lang|	String|	Yes|	Remark's language.|	en|


#### Queries

Your new SQL file must be able to handle the following queries and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByNameserverId
	SELECT rem.rem_id, rem.rem_title, rem.rem_type, rem.rem_lang 
	FROM {schema}.remark rem 
	JOIN {schema}.nameserver_remarks nse ON nse.rem_id=rem.rem_id 
	WHERE nse.nse_id=?;

	#getByDomainId
	SELECT rem.rem_id, rem.rem_title, rem.rem_type, rem.rem_lang  
	FROM {schema}.remark rem 
	JOIN {schema}.domain_remarks dom ON dom.rem_id=rem.rem_id 
	WHERE dom.dom_id=?;

	#getByEntityId
	SELECT rem.rem_id, rem.rem_title, rem.rem_type, rem.rem_lang 
	FROM {schema}.remark rem 
	JOIN {schema}.entity_remarks ent ON ent.rem_id=rem.rem_id 
	WHERE ent.ent_id=?;

	#getByAutnumId
	SELECT rem.rem_id, rem.rem_title, rem.rem_type, rem.rem_lang 
	FROM {schema}.remark rem 
	JOIN {schema}.asn_remarks asn ON asn.rem_id=rem.rem_id 
	WHERE asn.asn_id=?;

	#getByIpNetworkId
	SELECT rem.rem_id, rem.rem_title, rem.rem_type, rem.rem_lang 
	FROM {schema}.remark rem 
	JOIN {schema}.ip_network_remarks ine ON ine.rem_id=rem.rem_id 
	WHERE ine.ine_id=?;

### RemarkDescription.sql

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|rde_order|	Integer|	No|	Number showing placement of the description.|	3|
|rem_id|	Long|	No|	Remark's unique identifier.|	123|
|rde_description|	String|	No|	Description content.|	Description 3|

#### Queries

Your new SQL file must be able to handle the following query and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByRemarkId
	SELECT rem_desc.rde_order, rem_desc.rem_id, rem_desc.rde_description 
	FROM {schema}.remark_description rem_desc 
	WHERE rem_desc.rem_id=? ORDER BY rem_desc.rde_order ASC;

### SecureDNS.sql

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|sdns_id|	Long|	No|	Secure dns' id.|	123|
|sdns_zone_signed|	Boolean|	No|	1 if the zone has been signed, 0 otherwise.|	1|
|sdns_delegation_signed|	Boolean|	No|	1 if there are DS records in the parent, 0 otherwise.|	1|
|sdns_max_sig_life|	Integer|	Yes|	An integer representing the signature lifetime in seconds to be used when creating the RRSIG DS record in the parent zone.|	63000|
|dom_id|	Long|	No|	Domain's id.|	123|

#### Queries

Your new SQL file must be able to handle the following query and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByDomain
	SELECT s.sdns_id, s.sdns_zone_signed, s.sdns_delegation_signed, s.sdns_max_sig_life, s.dom_id 
	FROM {schema}.secure_dns s 
	WHERE s.dom_id = ?;


### DsData.sql

This file calls a DSData data object which attributes are described in the following table:

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|dsd_id|	Long|	No|	Ds data's id.|	123|
|sdns_id|	Long|	No|	Secure DNS's id.|	123|
|dsd_keytag|	Integer|	No|	An integer as specified by the key tag field of a DNS DS record.|	12345|
|dsd_algorithm|	Integer|	No|	An integer as specified by the algorithm field of a DNS DS record.|	3|
|dsd_digest|	String|	No|	A string as specified by the digest field of a DNS DS record.|	49FD46E6C4B45C55D4AC|
|dsd_digest_type|	Integer|	No|	An integer as specified by the digest type field of a DNS DS record.|	1|

#### Queries

Your new SQL file must be able to handle the following query and use aliases so both Red Dog's implementation and your database columns coincide:

	#getBySecureDns
	SELECT ds.dsd_id, ds.sdns_id, ds.dsd_keytag, ds.dsd_algorithm, ds.dsd_digest, ds.dsd_digest_type 
	FROM {schema}.ds_data ds 
	WHERE ds.sdns_id = ?;

### KeyData

This data structure represents the Key Data of a Secure DNS.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|kd_id|	Long|	No|	Key Data's id.|	123|
|kd_kd_sdns_id|	Long|	No|	Secure DNS id.|	123|
|kd_flags|	Integer|	Yes|	Integer containing the flags.|	256|
|kd_protocol|	Integer|	Yes|	Integer containing the protocol value.|	3|
|kd_public_key|	String|	Yes|	Public Key Material.|	105klfíe05|
|kd_algorithm|	Integer|	Yes|	Public Key cryptographic algorithm.|	5|

#### Queries

Your new SQL file must be able to handle the following query and use aliases so both Red Dog's implementation and your database columns coincide:

	#getBySecureDns
	SELECT kd.kd_id, kd.kd_sdns_id, kd.kd_flags, kd.kd_protocol, kd.kd_public_key, kd.kd_algorithm 
	FROM {schema}.key_data kd 
	WHERE kd.sdns_id = ?;

### Variant.sql

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|var_id|	Long|	No|	Variant's id.|	123|
|var_idn_table|	String|	Yes|	Variant's IDN table.|	idn.table|
|vna_ldh_name|	String|	No|	Variant name in LDH format.|	xn–fo-fka.example|
|rel_id|	Integer|	No|	Variant Relation Id(Needs to be retrieved from [this catalog]((#variantrelation)))|	1|
|dom_id|	Long|	No|	Domain's id.|	123|

#### Queries

Your new SQL file must be able to handle the following queries and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByDomainId
	SELECT v.var_id, v.var_idn_table, v.dom_id 
	FROM {schema}.variant v 
	WHERE v.dom_id=?;

	#getVariantRelationsByVariantId
	SELECT rel_id 
	FROM {schema}.variant_relation vr 
	WHERE vr.var_id=?;

	#getVariantNamesByVariantId
	SELECT vna_ldh_name 
	FROM {schema}.variant_name vn 
	WHERE vn.var_id=?;

### VCard.sql 

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|vca_id|	Long|	No|	Vcard's id.|	123|
|vca_name|	String|	Yes|	Contact's name.|	Joe Jobs|
|vca_company_name|	String|	Yes|	Contact's company name.|	Orange|
|vca_company_url|	String|	Yes|	Contact's url.|	www.orange.mx|
|vca_email|	String|	Yes|	Contact's email.|	jj@orange.mx|
|vca_voice|	String|	Yes|	Contact's telephone.|	81 8818181|
|vca_cellphone|	String|	Yes|	Contact's cellphone.|	81 8181818181|
|vca_fax|	String|	Yes|	Contact's fax.|	248.697.0908|
|vca_job_title|	String|	Yes|	Contact's job title.|	Engineer|

#### Queries

Your new SQL file must be able to handle the following query and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByEntityId
	SELECT vca.vca_id, vca_name, vca_company_name, vca_company_url, vca_email, vca_voice, vca_cellphone, vca_fax, vca_job_title 
	FROM {schema}.vcard vca 
	JOIN {schema}.entity_contact eco ON eco.vca_id = vca.vca_id 
	WHERE eco.ent_id = ?;

### VCardPostalInfo.sql

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|vpi_id|	Long|	No|	Postal info's id.|	123|
|vca_id|	String|	Yes|	Vcard's id.|	Joe Jobs|
|vpi_type|	String|	Yes|	Postal info's type.|	local|
|vpi_country|	String|	Yes|	Country.|	Mexico|
|vpi_city|	String|	Yes|	City.|	Juarez|
|vpi_street1|	String|	Yes|	Street.|	Luis Elizondo|
|vpi_street2|	String|	Yes|	Street.|	Altavista|
|vpi_street3|	String|	Yes|	Street.|	100|
|vpi_state|	String|	Yes|	State.|	Guadalajara|
|vpi_postal_code|	String|	Yes|	Postal code.|	34020|

#### Queries

Your new SQL file must be able to handle the following query and use aliases so both Red Dog's implementation and your database columns coincide:

	#getByVCardId
	SELECT vpi.vpi_id, vpi.vca_id, vpi.vpi_type, vpi.vpi_country, vpi.vpi_city, vpi.vpi_street1, vpi.vpi_street2, vpi.vpi_street3, vpi.vpi_state, vpi.vpi_postal_code 
	FROM {schema}.vcard_postal_info vpi 
	WHERE vpi.vca_id = ?;


### Zone.sql

This file is extremely important for the [Domain object](#domainsql) as all of the queries need this id to work.

The following table describes each value we expect you to retrieve with your own queries:

|Alias name|Value Type|Allows Null|Description|Example|
|:---------|:--------:|:---------:|:----------|:-----:|
|zone_id|	Integer|	No|	Variant's id.|	6|
|zone_name|	String|	No|	Variant's IDN table.|	net|

#### Queries

Your new SQL file must be able to handle the following query and use aliases so both Red Dog's implementation and your database columns coincide:

	#getAll
	SELECT zone_id, zone_name FROM {schema}.zone;


## Needed Catalogs

Your implementation will need to add the following catalogs, as well as some queries for Red Dog to be able to reach those,

### Status

The following values have been registered in the “RDAP JSON Values” registry for the Status of the RDAP objects:

|RDAP Status name|	Status description|
|:--------------:|:-------------------|
|validated|	Signifies that the data of the object instance has been found to be accurate. This type of status is usually found on entity object instances to note the validity of identifying contact information.|
|renew prohibited|	Renewal or reregistration of the object instance is forbidden.|
|update prohibited|	Updates to the object instance are forbidden.|
|transfer prohibited|	Transfers of the registration from one registrar to another are forbidden.|
|delete prohibited|	Deletion of the registration of the object instance is forbidden.|
|proxy|	The registration of the object instance has been performed by a third party.|
|private|	The information of the object instance is not designated for public consumption.|
|removed|	Some of the information of the object instance has not been made available and has been removed.|
|obscured|	Some of the information of the object instance has been altered for the purposes of not readily revealing the actual information of the object instance.|
|associated|	The object instance is associated with other object instances in the registry.|
|active|	The object instance is in use. For domain names, it signifies that the domain name is published in DNS. For network and autnum registrations, it signifies that they are allocated or assigned for use in operational networks.|
|inactive|	The object instance is not in use.|
|locked|	Changes to the object instance cannot be made, including the association of other object instances.|
|pending create|	A request has been received for the creation of the object instance, but this action is not yet complete.|
|pending renew|	A request has been received for the renewal of the object instance, but this action is not yet complete.|
|pending transfer|	A request has been received for the transfer of the object instance, but this action is not yet complete.|
|pending update|	A request has been received for the update or modification of the object instance, but this action is not yet complete.|
|pending delete|	A request has been received for the deletion or removal of the object instance, but this action is not yet complete. For domains, this might mean that the name is no longer published in DNS but has not yet been purged from the registry database.|


#### Status.sql

	#getByNameServerId
	SELECT sta_id 
	FROM {schema}.nameserver_status 
	WHERE nse_id=?;

	#getByDomainId
	SELECT sta_id 
	FROM {schema}.domain_status 
	WHERE dom_id=?;

	#getByEntityId
	SELECT sta_id 
	FROM {schema}.entity_status 
	WHERE ent_id=?;

	#getByRegistrarId
	SELECT sta_id 
	FROM {schema}.registrar_status 
	WHERE rar_id=?;

	#getByAutnumid
	SELECT sta_id 
	FROM {schema}.asn_status 
	WHERE asn_id=?;

	#getByIpNetworkId
	SELECT sta_id 
	FROM {schema}.ip_network_status 
	WHERE ine_id=?;

You need this catalog and queries so main objects can have a status.

### EventAction

The following values have been registered in the “RDAP JSON Values” registry for the EventAction of the RDAP objects:

|EventAction name|	EventAction description|
|:--------------:|:------------------------|
|registration|	The object instance was initially registered.|
|reregistration|	The object instance was registered subsequently to initial registration.|
|last changed|	An action noting when the information in the object instance was last changed.|
|expiration|	The object instance has been removed or will be removed at a predetermined date and time from the registry.|
|deletion|	The object instance was removed from the registry at a point in time that was not predetermined.|
|reinstantiation|	The object instance was reregistered after having been removed from the registry.|
|transfer|	The object instance was transferred from one registrant to another.|
|locked|	The object instance was locked.|
|unlocked|	The object instance was unlocked.|

This catalog does not have a SQL file but is needed to be able to retrieve an [Event](#eventsql) object.

### Role

The following values have been registered in the “RDAP JSON Values” registry for Rol:

|Rol name|	Rol description|
|:------:|:----------------|
|registrant|	The entity object instance is the registrant of the registration. In some registries, this is known as a maintainer.|
|technical|	The entity object instance is a technical contact for the registration.|
|administrative|	The entity object instance is an administrative contact for the registration.|
|abuse|	The entity object instance handles network abuse issues on behalf of the registrant of the registration.|
|billing|	The entity object instance handles payment and billing issues on behalf of the registrant of the registration.|
|registrar|	The entity object instance represents the authority responsible for the registration in the registry.|
|reseller|	The entity object instance represents a third party through which the registration was conducted (i.e., not the registry or registrar).|
|sponsor|	The entity object instance represents a domain policy sponsor, such as an ICANN-approved sponsor.|
|proxy|	The entity object instance represents a proxy for another entity object, such as a registrant.|
|notifications|	An entity object instance designated to receive notifications about association object instances.|
|noc|	The entity object instance handles communications related to a network operations center (NOC).|

#### Role.sql

	#getMainEntityRole
	SELECT rol.rol_id 
	FROM {schema}.entity_role rol 
	WHERE rol.ent_id = ?;

	#getDomainRol
	SELECT rol.rol_id 
	FROM {schema}.domain_entity_roles rol 
	WHERE rol.dom_id = ? AND rol.ent_id = ?;

	#getEntityRol
	SELECT rol.rol_id 
	FROM {schema}.entity_entity_roles rol
	WHERE rol.main_ent_id = ? AND rol.ent_id = ?;

	#getNSRol
	SELECT rol.rol_id 
	FROM {schema}.nameserver_entity_roles rol 
	WHERE rol.nse_id = ? AND rol.ent_id = ?;

	#getAutnumRol
	SELECT rol.rol_id 
	FROM {schema}.asn_entity_roles rol 
	WHERE rol.asn_id = ? AND rol.ent_id = ?;

	#getIpNetworkRol
	SELECT rol.rol_id 
	FROM {schema}.ip_network_entity_roles rol 
	WHERE rol.ine_id = ? AND rol.ent_id = ?;

You need this catalog and queries so an object can have an Entity with an associated role.

### VariantRelation

The following values have been registered in the “RDAP JSON Values” registry for VariantRelations:

|VariantRelation name|	VariantRelation description|
|:------------------:|:----------------------------|
|registered|	The variant names are registered in the registry.|
|unregistered|	The variant names are not found in the registry.|
|registration restricted|	Registration of the variant names is restricted to certain parties or within certain rules.|
|open registration|	Registration of the variant names is available to generally qualified registrants.|
|conjoined|	Registration of the variant names occurs automatically with the registration of the containing domain registration.|

This catalog does not have a SQL file but is needed to be able to retrieve a [Variant](#variantsql) object.

#### Country codes

Red Dog uses the standard for area codes used by the [United Nations Statistics Division](https://en.wikipedia.org/wiki/United_Nations_Statistics_Division "United Nations Statistics Division from Wikipedia").The complete list of country codes can be found [here](http://www.nationsonline.org/oneworld/country_code_list.htm "ISO Alpha-2, Alpha-3, and Numeric Country Codes").

## Where to Go Next

After making your own queries now you will have to install the server, steps on how to do this can be found [here](server-install-option-2.html).

## Additional Notes

*	All necessary values for the catalogs in RDAP can be found [here](https://www.iana.org/assignments/rdap-json-values/rdap-json-values.xhtml#rdap-json-values-1 "RDAP JSON Values").
*	All the provided queries are those implemented by **Red Dog**, yours don´t have to be the same, they only need to return the same type of data.


