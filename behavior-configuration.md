---
title: Configuring Red Dog's Server Behavior
breadcrums: ["Documentation", "documentation.html", "Further Custom Configuration", "documentation.html#further-custom-configuration"]
wheretogo: ["Configuring Red Dog's Help Response", "help-response.html"]
---

# {{ page.title }}

## Index

1. [Introduction](#introduction)
1. [Configuring `configuration.properties`](#configuring-configurationproperties)
   1. [`language`](#language)
   1. [`minimum_search_pattern_length`](#minimum_search_pattern_length)
   1. [`max_number_result_authenticated_user`](#max_number_result_authenticated_user)
   1. [`max_number_result_unauthenticated_user`](#max_number_result_unauthenticated_user)
   1. [`owner_roles_*`](#owner_roles_)
   1. [`allow_multiple_search_wildcards`](#allow_multiple_search_wildcards)
   1. [`allow_search_wildcard_anywhere`](#allow_search_wildcard_anywhere)
   1. [`allow_regex_searches`](#allow_regex_searches)
   1. [`user_roles`](#user_roles)
   1. [`notices_timer_update_time`](#notices_timer_update_time)
   1. [`events_timer_update_time`](#events_timer_update_time)
   1. [`is_db_data_live`](#is_db_data_live)


## Introduction

Red Dog's behavior can be configured to satisfy whatever needs the implementer has. To achieve this, there's a global configuration file located at folder `WEB-INF/configuration.properties` in the installation directory. Here's a preview of the file's beginning:

```
#Optional. Language of the server. Values en=english, es=español. Default: en
#language = 

#Optional. Minimum length of the search pattern. Default: 5
#minimum_search_pattern_length = 

#Optional. Max number of results for the authenticated user. Default: 20
#max_number_result_authenticated_user = 

#Optional. Max number of results for the unauthenticated user. Default: 10
#max_number_result_unauthenticated_user =
```

The next section will explain each of the properties that can be configured to customize Red Dog's server behavior.

## Configuring `configuration.properties`

The `configuration.properties` file has several properties, each one with a specific task. In this section those properties and its expected behavior will be explained.

### `language`

Language used at the server responses. This value will be set at the `lang` attribute of a [`RdapObject`](https://github.com/NICMx/rdap-core/blob/master/src/main/java/mx/nic/rdap/core/db/RdapObject.java).

This table shows the specs of the property:

| Required? | Type | Default | Example |
|-----------|------|---------|---------|
| ![No](img/red_x.svg) | String | en | language = es |

### `minimum_search_pattern_length`

Minimum allowed length for search patterns. Any request where the search pattern length is smaller than this property value, will be rejected.

**Examples.** If the value is `5`, this is the expected result for each case:

| Request                                 | Valid?                                                                     | 
|-----------------------------------------|----------------------------------------------------------------------------|
| https://example.com/rdap/domains?name=dumm* | ![Yes](img/green_bkg_check.svg)                                                         |
| https://example.com/rdap/domains?name=dum*  | ![No](img/red_x.svg) search pattern length is 4                                             |
| https://example.com/rdap/domains?name=dum** | ![No](img/red_x.svg) search pattern length is 4 (consecutive wildcards are treated as one)  |

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Integer | 5 | minimum_search_pattern_length = 6 |

### `max_number_result_authenticated_user`

Maximum number of results that will be listed within responses to search requests made by authenticated users. This property indicates the default value for all authenticated users.

If the implementer wishes to customize the max number of results per user (eg. User A can see a max of 50 results, but user B can only see 30 results), the attribute `maxSearchResults` of the object [`RdapUser`](https://github.com/NICMx/rdap-data-access-api/blob/master/src/main/java/mx/nic/rdap/db/RdapUser.java) will be useful to achieve that.

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Integer | 20      | max_number_result_authenticated_user = 30 |

### `max_number_result_unauthenticated_user`

Maximum number of results that will be listed within responses to search requests made by unauthenticated users.

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Integer | 10      | max_number_result_unauthenticated_user = 20 |

### `owner_roles_*`

List of entity roles that should be able to see more information of a specific RDAP Object. Must be existing roles according to the catalog of [RFC 7483 section 10.2.4](https://tools.ietf.org/html/rfc7483#section-10.2.4).

To specify the owner of each RDAP Object, there’s a list of properties. These properties have the same validations but apply to distinct cases, just as their names prove it:
* `owner_roles_ip`
* `owner_roles_autnum`
* `owner_roles_domain`
* `owner_roles_nameserver`

The **Entity** object isn’t considered since it’s a generic object. Still, the next paragraph explains how an **Entity** object is treated.

A user (as confirmed by a successful [Authorization header](https://en.wikipedia.org/wiki/List_of_HTTP_header_fields#Request_fields) validation) will be considered the owner of a RDAP Object (Autnum, Domain, Entity, IP Network or Nameserver) if one of the following conditions is met:
* (If the RDAP Object is an Entity) The user’s username matches the object’s [`handle`](https://github.com/NICMx/rdap-core/blob/master/src/main/java/mx/nic/rdap/core/db/RdapObject.java#L17).
* (If the RDAP Object is not an Entity) The user’s username matches the handle of at least one of the object’s entities that has at least one role listed in `owner_roles_*`.

The following example will help to understand how this works. If the property `owner_roles_domain` value is **registrant**, and the server has this domain object with the corresponding entities related:

```
Domain (LDH name: "example.com")
  -> Entity (handle: "Alice", roles: "administrative")
  -> Entity (handle: "Bob",   roles: "registrant")
  -> Entity (handle: "Eve",   roles: "technical")
```

Then only the user **“Bob”** will be considered the **owner** of the domain **"example.com"**.

These properties work in conjunction with [privacy settings](response-privacy.html).

This table shows the specs of each property:

| Property               | Required?          | Type   | Default        | Example                            |
|------------------------|--------------------|--------|----------------|------------------------------------|
| owner_roles_ip         | ![No](img/red_x.svg) | String (can be a list separated by commas) | administrative | owner_roles_ip = technical         |
| owner_roles_autnum     | ![No](img/red_x.svg) | String (can be a list separated by commas) | administrative | owner_roles_autnum = technical     |
| owner_roles_domain     | ![No](img/red_x.svg) | String (can be a list separated by commas) | registrant     | owner_roles_domain = technical     |
| owner_roles_nameserver | ![No](img/red_x.svg) | String (can be a list separated by commas) | registrar      | owner_roles_nameserver = technical |

### `allow_multiple_search_wildcards`

Boolean flag to indicate if the wildcard ‘\*’ can be used more than 1 time on each label of the search patterns. This property is used to comply with [RFC 7482 section 4.1](https://tools.ietf.org/html/rfc7482#section-4.1) allowing the use of the asterisk ‘\*’ wildcard in searches.

The property is used to relieve the cost of searches that could be expensive to the server. If the property has a value of `true` then an object label can have more than one wildcard when used at partial searches, if `false` then only one wildcard per label will be allowed. The following table will help to comprehend this:

| Property value | Search request                                | Valid?                   |
|----------------|---------------------------------------------  |--------------------------|
| false          | https://example.com/rdap/domains?name=doma\*      | ![Yes](img/green_bkg_check.svg)       |
| false          | https://example.com/rdap/domains?name=dom\*n.co\* | ![Yes](img/green_bkg_check.svg)       |
| false          | https://example.com/rdap/domains?name=d\*ma\*.\*o\* | ![No](img/red_x.svg)       |
| false          | https://example.com/rdap/domains?name=d\*ma\*     | ![No](img/red_x.svg) |
| true           | https://example.com/rdap/domains?name=d\*ma\*     | ![Yes](img/green_bkg_check.svg)       |
| true           | https://example.com/rdap/domains?name=doma\*.co\* | ![Yes](img/green_bkg_check.svg)       |
| true           | https://example.com/rdap/domains?name=d\*ma\*.\*o\* | ![Yes](img/green_bkg_check.svg)       |

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Boolean | false     | allow_multiple_search_wildcards = true |

### `allow_search_wildcard_anywhere`

Boolean flag to indicate if the wildcard ‘\*’ can be used anywhere on each label of a search pattern. If the property has a value of `true` is valid to use the wildcard anywhere on each label of the search pattern, if `false` the wildcard can only be used at the end of each label.

> ![Warning](img/warning.svg) If the property [`allow_multiple_search_wildcards`](#allow_multiple_search_wildcards) is set to `true`, then the value of `allow_search_wildcard_anywhere` is indifferent.

The following table shows some examples on how this flag works:

| Property value | Search request                              | Valid?                   |
|----------------|---------------------------------------------|--------------------------|
| false          | https://example.com/rdap/domains?name=doma\*      | ![Yes](img/green_bkg_check.svg)       |
| false          | https://example.com/rdap/domains?name=dom\*n      | ![No](img/red_x.svg) |
| false          | https://example.com/rdap/domains?name=doma\*.co\* | ![Yes](img/green_bkg_check.svg)       |
| false          | https://example.com/rdap/domains?name=dom\*n.c\*m | ![No](img/red_x.svg)       |
| true           | https://example.com/rdap/domains?name=dom\*n      | ![Yes](img/green_bkg_check.svg)       |
| true           | https://example.com/rdap/domains?name=dom\*n.co\* | ![Yes](img/green_bkg_check.svg)       |

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Boolean | false     | allow_search_wildcard_anywhere = true |

### `allow_regex_searches`

Boolean flag to indicate if the server will support extended searches using REGEX. If the property has a value of `true` the server will support the behavior stated at draft [Registration Data Access Protocol (RDAP) Search Using POSIX Regular Expressions v01](https://tools.ietf.org/html/draft-fregly-regext-rdap-search-regex-01) (see examples at the table below), if `false` then this behavior will be disabled (the parameter `searchtype` will be ignored).

> ![Warning](img/warning.svg) The feature is based on a draft, so it isn't quite updated, use it cautiously. Hopefully this will be updated on a later release.

The following table shows some examples on how this flag works:

| Property value | Search request | Result |
|----------------|----------------|--------|
| false | https://example.com/rdap/domains?name=doma\* | Regular partial search |
| false | https://example.com/rdap/domains?name=doma\*&searchtype=regex | Regular partial search: parameter `searchtype` is ignored |
| false | https://example.com/rdap/domains?name=Xihkb20pLio=&searchtype=regex | Regular partial search: parameter `searchtype` is ignored, base64 encoded regex sent at `name` will not be decoded |
| true | https://example.com/rdap/domains?name=doma\* | Regular partial search |
| true | https://example.com/rdap/domains?name=doma\*&searchtype=regex | Invalid partial search: parameter `searchtype` is used, the parameter `name` MUST be a base64 encoded regex |
| true | https://example.com/rdap/domains?name=Xihkb20pLio=&searchtype=regex | Regex partial search: parameter `searchtype` is used, regex base64 encoded sent at `name` will be decoded and used (the regex sent is `^(dom).*`) |

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Boolean | false     | allow_regex_searches = true |

### `user_roles`

List of strings representing custom user roles that can be used to configure [privacy settings](response-privacy.html). The roles defined here **MUST NOT** have any of these values: `any`, `none`, `owner`, or `authenticated`; since those are reserved words used at privacy settings.

Since each role is expected to be a String, any value (except for the reserved words) that suites the implementer needs can be used, still it's **recommended** to use alphanumeric values. When using roles, its value is used and validated as case insensitive.

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | String (can be a list separated by commas) | null     | user_roles = president, governor, judge |


### `notices_timer_update_time`
Integer number that represents time in seconds. It is used by a `notices update timer` that checks every N seconds, if exists a change in the notices files for each type of requests (i.e. entity, domain, nameserver, autnum, ip)

if the value is less than 10 (0 - default value), the timer for the notices will never start nor check for updates. 
To update the notices, you will need to restart the application.
This functionality behave this way because not every implementer needs to update notices too often.
 

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Integer | 0 | notices_timer_update = 3600 |

### `events_timer_update_time`
Integer number that represents time in seconds. It is used by an `event update timer` that checks every N seconds, if exists a change in the event file.

if the value is less than 10 (0 - default value), the timer for the events will never start nor check for updates. 
To update the events, you will need to restart the application.
This functionality behave this way  because not every implementer needs to update events too often.
 

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Integer | 0 | events_timer_update = 3600 |

### `is_db_data_live`
Boolean value to override the timestamp in the eventAction type `last update of RDAP database`.

It is used when the RDAP service provider is querying its database directly, and therefore, using real-time data, so for every request, the timestamp value will be overridden to the server timestamp when the request was made.

In case that the event `last update of RDAP database` event does not exist, this value will be ignored.

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Boolean | false     | is_db_data_live = false |

### `add_custom_conformance`
Rdap Conformances (separated by commas) to be displayed in the result response.

It is used when the RDAP service wants to add custom conformance to the response, e.g. If your RDAP service is compliant with ICANN's RDAP profile, you can add the conformance that tells that you are complying with the profile.

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Strings sepparated by commas | empty | add_custom_conformance = lunar_nic_0 |


### `add_email_remark`
Boolean value.

Indicates if Email remark (from section 2.7.5.3 in ICANN's RDAP response profile) needs to be added to the domain response.

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Boolean | false | add_email_remark = true |


### `is_country_code_released`
Boolean value

Indicates If the ISO-3166-1-alpha-2 parameter has been published in the vCard Properties
and handle according to the ICANN's RDAP response profile section 1.4

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Boolean | false | is_country_code_released = true |
