---
layout: default
title: Introduction to RDAP/Red Dog
---

# Introduction to RDAP/Red Dog

## What is RDAP?

RDAP (_Registration Data Access Protocol_) is a successor of WHOIS--a protocol used for querying information regarding Internet resources (such as domain names, IP addresses and autonomous system numbers).

Some advantages of RDAP over WHOIS are

- Standardized request and response formats, in contrast to WHOIS' provider-defined arbitrary text.
- Reliance on Representational State Transfer (REST) technologies, a strong base which is widely known.
- Bootstrapping, the automatic determination of where a query should be sent.
- Support for Internationalized Domain Names and support for localized registration data.
- Support for identification, authentication and access control to the service.

## What is Red Dog?

Red Dog is a free and open source Java implementation of an RDAP server currently under development.

## Status

There are three development phases planned as of 2016-09-20:

1. Lookup Path Segment, rate-limit, basic authentication, JSON render, help command.
2. Search Path Segment, Digest authentication, rate-limit penalization, Apache Proxy support, access configuration, indexing.
3. Federated Authentication, HTML render, redirection, extensions, internationalization, query cache, client, API, installer.

Phase 1 is currently under development.

