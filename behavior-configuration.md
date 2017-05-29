---
title: Server Behavior Configuration
---

# Configuring Red Dog server behavior

## Index

1. [Introduction](#introduction)
2. [Keys](#keys)
	1. [`zones`](#zones)
	2. [`minimum_search_pattern_length`](#minimum_search_pattern_length)
	3. [`max_number_result_unauthenticated_user`](#max_number_result_unauthenticated_user)
	4. [`max_number_result_authenticated_user`](#max_number_result_authenticated_user)
	5. [`owner_roles`](#owner_roles)
	6. [`operational_profile`](#operational_profile)
	7. [`anonymous_username`](#anonymous_username)
	8. [`allow_search_wildcards_anywhere`](#allow_search_wildcards_anywhere)

## Introduction

`WEB-INF/configuration.properties` is Red Dog's global configuration file. Here is an example of it:

        # Required. Zones managed (separated by commas). example: mx, lat, com
        zones=
        # Optional.Minimum length of the search pattern. Default 5
        minimum_search_pattern_length=
        # Optional.Max number of results for the authenticated user. Default 20
        max.number.result.authenticated.user=
        # Optional.Max number of results for the unauthenticated user. Default 10
        max_number_result_unauthenticated_user=
        # Optional. Indicates the roles that are the owners of the rdap objects. example: registrar, administrative, registrant. Default: empty
        owner_roles =
        # Allowed Values: registry, registrar, None. Default: none
        operational_profile=
        # Optional. anonymous, username. Default 'anonymous'.
        anonymous_username = 

A description of every configurable key follows.

## Keys

### `zones`

- Type: String (labels separated by commas).
- Default: None
- Example: mx, com.mx, edu.mx

Zones managed by the server. The server will refuse to serve domains queries that do not match these zones.

### `minimum_search_pattern_length`

- Type: Integer
- Default: 5

Minimum allowable length for search patterns. Searches whose request strings have a smaller length than this will be rejected.

For example:

	https://example.com/rdap/entities?fn=Foo*

Will be rejected by the default `minimum_search_pattern_length` because the search pattern ("Foo*") has less than five characters.

### `max_number_result_unauthenticated_user`

- Type: Integer
- Default: 10

Maximum number of results that will be listed within responses to search requests lacking an Authorization header.

For example, if `max_number_result_unauthenticated_user` is 5 and the database contains 10 records that match an authenticated user's search pattern, the server will truncate the response to only 5 records.

### `max_number_result_authenticated_user`

- Type: Integer
- Default: 20

Default maximum number of results that will be listed within responses to search requests queried by authenticated users. The data access implementation can override this by providing [user-specific](https://github.com/NICMx/rdap-data-access-api/blob/v1.1.0/src/main/java/mx/nic/rdap/db/spi/RdapUserDAO.java#L14) [values](https://github.com/NICMx/rdap-data-access-api/blob/v1.1.0/src/main/java/mx/nic/rdap/db/RdapUser.java#L13).

### `owner_roles`

- Type: Strings (separated by commas)
- Default: &lt;Empty&gt;

List of user roles that should be able to view more RDAP Object information than others.

A user (as confirmed by a successful [Authorization header](https://en.wikipedia.org/wiki/List_of_HTTP_header_fields#Request_fields) validation) will be considered the owner of an RDAP Object (Autnum, Domain, Entity, IP Network or Nameserver) if one of the following conditions is met:

- (If the RDAP Object is an Entity) The user's username matches the object's [handle](https://github.com/NICMx/rdap-core/blob/v1.1.0/src/main/java/mx/nic/rdap/core/db/RdapObject.java#L17).
- (If the RDAP Object is not an Entity) the user's username matches the handle of at least one of the [object's entities](https://github.com/NICMx/rdap-core/blob/v1.1.0/src/main/java/mx/nic/rdap/core/db/RdapObject.java#L47) that has at least [one role](https://github.com/NICMx/rdap-core/blob/v1.1.0/src/main/java/mx/nic/rdap/core/db/Entity.java#L33) listed in `owner_roles`.

So, for example, if your `owner_roles` value is "administrative", and you have the following RDAP object:

	Domain (LDH name: "example.com")
		-> Entity (handle: "Alice", roles: "administrative")
		-> Entity (handle: "Bob",   roles: "registrant")
		-> Entity (handle: "Eve",   roles: "technical")

Then only Alice will be considered the owner of example.com.

Owners can be allowed to view object information that is hidden to other users. See [response privacy](response-privacy.html) for more information.

### `operational_profile`

- Type: Enum (`registry`, `registrar` or `none`)
- Default: `none`

Enables or disables a number of additional [validations and requirements](https://www.icann.org/resources/pages/rdap-operational-profile-2016-07-26-en) that should be fulfilled by registries or registrars. 

See [issue #30](https://github.com/NICMx/rdap-server/issues/30).

### `anonymous_username`

- Type: String
- Default: anonymous

The username your reverse proxy is [appending to requests lacking Authorization headers](optional-authentication.html), for the sake of bypassing the servlet container's mandatory HTTP authentication when what you actually want is to provide partial information to unauthenticated users.

As far as Red Dog is concerned, requests containing this username will be treated as anonymous and will therefore be served the minimum authorized information available, according to the [response privacy](response-privacy.html) policies.

### `allow_search_wildcards_anywhere`

- Type: Boolean
- Default: true

The basic specification requires RDAP servers to support, at a minimum, searches where a wildcard can be present at the end of each label. [Here](https://tools.ietf.org/html/rfc7482#section-4.1) is the relevant paragraph:

	Partial string searching uses the asterisk ('*', US-ASCII value
	0x002A) character to match zero or more trailing characters.  A
	character string representing multiple domain name labels MAY be
	concatenated to the end of the search pattern to limit the scope of
	the search.  For example, the search pattern "exam*" will match
	"example.com" and "example.net".  The search pattern "exam*.com" will
	match "example.com".  If an asterisk appears in a search string, any
	label that contains the non-asterisk characters in sequence plus zero
	or more characters in sequence in place of the asterisk would match.
	Additional pattern matching processing is beyond the scope of this
	specification.

So servers are expected to support queries like the following:

	exam*.com

But whether they support things like this is left at the implementor's discretion:

	ex*ple.com

When `allow_search_wildcards_anywhere` is `false`, the server will validate only one wildcard is present per label, and only at the end of it. This is simply intended to prevent the data access implementation from having to validate this.

## Where to go next

[Back to the optional configuration index](documentation.html#further-configuration-optional).
