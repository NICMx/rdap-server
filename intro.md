---
title: Introduction to RDAP/Red Dog
---

# Introduction to RDAP/Red Dog

## Index

1. [What is RDAP?](#what-is-rdap)
2. [What is Red Dog?](#what-is-red-dog)	

## What is RDAP?

RDAP (_Registration Data Access Protocol_) is a successor of WHOIS--a protocol used for querying information regarding Internet resources (such as domain names, IP addresses and autonomous system numbers). RDAP is based on a typical client-server model. The server is a RESTful service expected to provide HTTP content in accordance with RFCs [7480](https://tools.ietf.org/html/rfc7480), [7481](https://tools.ietf.org/html/rfc7481), [7482](https://tools.ietf.org/html/rfc7482) and [7483](https://tools.ietf.org/html/rfc7483).

Some advantages of RDAP over WHOIS are

- Standardized request and response formats, in contrast to WHOIS' provider-defined arbitrary text.
- Reliance on Representational State Transfer (REST) technologies, a strong base which is widely known.
- Bootstrapping, the automatic determination of where a query should be sent.
- Support for Internationalized Domain Names and support for localized registration data.
- Support for identification, authentication and access control to the service.

## What is Red Dog?

Red Dog is a free and open source Java implementation of an RDAP server carcass. It is a handful of servlets and APIs that can help you serve your registration data in a standard manner.

![Fig. 1 - Overview](img/diagram/intro-overview.svg)

As pictured, deploying Red Dog requires the development of an interface between your database and the servlets. This can be done in three different ways:

### Option 1: Full Data Access Implementation

The [Data Access API](https://github.com/NICMx/rdap-data-access-api) (DAA) mainly consists of [a set of Java interfaces](https://github.com/NICMx/rdap-data-access-api/tree/master/src/main/java/mx/nic/rdap/db/spi). The server queries an implementation of these interfaces to access the data.

![Fig.1 - Full implementation architecture](img/diagram/intro-option-1.svg)

By providing your own implementation of the DAA (a "Data Access Implementation") you can wrap your database to the server in any way you want. This can range from anything from direct queries to `Your Main Database`, or to a mirror of it, to queries to non-relational databases.

[Here](data-access-layer.html) are some directions that might get you started in creating a DAI.

### Option 2: Overriding SQL Provider queries

The [SQL Provider](https://github.com/NICMx/rdap-sql-provider) is a project that implements the DAA as queries to a relational database. Instead of developing Java code, you create queries that return data in a specific format.

![Fig. 2 - SQL Provider](img/diagram/intro-option-2.svg)

## Option 3: Using Red Dog's builtin schema

The SQL Provider ships with default queries, intended to be used along a predefined schema.

![Fig. 3 - SQL Provider default](img/diagram/intro-option-3.svg)

What you need to provide under this architecture is a means to export your data from your core database to Red Dog's schema.

