---
title: RDAP Demo Server
---

# RDAP Demo Server

## Index

1. [Introduction](#introduction)
2. [Download](#download)
3. [Running the demo](#running-the-demo)
4. [Available features](#available-features)
5. [Available configuration](#available-configuration)
	1. [`zones`](#zones)
	2. [`minimum_search_pattern_length`](#minimum_search_pattern_length)
	3. [`max_number_result_unauthenticated_user`](#max_number_result_unauthenticated_user)
	4. [`is_reverse_ipv4_enabled`](#is_reverse_ipv4_enabled)
	5. [`is_reverse_ipv6_enabled`](#is_reverse_ipv6_enabled)
6. [Dummy data](#dummy-data)
	1. [Domain data](#domain-data)
	2. [Entity data](#entity-data)
	3. [Nameserver data](#nameserver-data)

## Introduction

Before you begin a full installation of Red Dog, you can try its features out using our demo server. It is a standalone Tomcat binary packaged with Red Dog and an embedded (H2-based) database populated with dummy data.

The only requirement is Java 8 or superior.

## Download

You can download the demo server contained in a zip file from this [link](https://github.com/NICMx/releases/raw/master/RedDog/rdap-server-demo-1.0.zip).

## Running the demo

Expand the compressed zip and run the jar normally:

	unzip rdap-server-demo-1.0.zip
	cd rdap-server-demo
	java -jar demo.jar

The server will run at [http://localhost:8080/rdap-server/](http://localhost:8080/rdap-server/) by default. You can change the binding IP and port, using the `java -jar demo.jar [your-IP-address [your-port]]` syntax. As an example:

	java -jar demo.jar 127.0.0.5 9090

When the server starts, the console will log messages as shown below. As long as you don't get SEVEREs (as opposed to INFOs), the server is underway.

![SERVER CONSOLE](img/demo-console.jpg)
 
The demo server is now ready to answer requests. In your browser, query the address that you set up and you should see a welcome page:
 
![WELCOME PAGE](img/demo-index.jpg)
 
## Available features

As a demo version, this server has limited features compared to the ones defined in RFC 7482 (excluding ASN and IPs queries), as well as the ones from the full server. This is what you can query:

+ Domain query: [http://localhost:8080/rdap-server/domain
+ Entity query: http://localhost:8080/rdap-server/entity
+ Nameserver query: http://localhost:8080/rdap-server/nameserver
+ Domains search: http://localhost:8080/rdap-server/domains?...
+ Entities search: http://localhost:8080/rdap-server/entities?...
+ Nameservers search: http://localhost:8080/rdap-server/nameservers?...

## Available configuration

In the configuration file (`WEB-INF/configuration.properties`) you can change the demo server's behavior by modifying the following properties:

### `zones`

- Type: String (labels separated by commas).
- Default: com, com.example (Embedded explicitly in the demo's configuration; Red Dog does not normally ship with a default in this field.)

Zones managed by the server. The server will only serve domains that match this zone.

For example, if you manage the "com" zone, and a user requests domain "test.example", then the server will respond 404, since it doesn't manage the "example" zone. (Even if the requested domain record exists in the database.)

### `minimum_search_pattern_length`

- Type: Integer
- Default: 1 (Embedded explicitly in the demo's configuration; Red Dog's actual default is 5.)

Minimum length of the search pattern. Searches whose request strings have a lower length than this will be rejected.

For example, if `minimum_search_pattern_length` is 5 and a user attempts to search for "test", the server will respond an error message.

### `max_number_result_unauthenticated_user`

- Type: Integer
- Default: 10

Maximum number of results for unauthenticated users.

For example, if `max_number_result_unauthenticated_user` is 5 and the database contains 10 records that match an authenticated user's search pattern, the server will truncate the response to only 5 records.

### `is_reverse_ipv4_enabled`

- Type: Boolean
- Default: false

Indicates whether this RDAP server should respond to reverse IPv4s domain searches.

If true, the server will search in its database domains that are stored in a reverse IPv4 form when a user send a request of a domain using reverse IPv4 lookup.

### `is_reverse_ipv6_enabled`

- Type: Boolean
- Default: false

Indicates whether this RDAP server should respond to reverse IPv6s domain searches.

If true, the server will search in its database domains that are stored in a reverse IPv6 form when a user send a request of a domain using reverse IPv6 lookup.

## Dummy data 

The demo's database ships with the following test data:

### Domain data

| Handle   | ldh (letter, digit, hyphen) name | Unicode name     | Zone         |
|:---------|:-------------------------------- |:-----------------|:-------------|
| DOM1     | whiterabbit                      |                  | com          |
| DOMCOM   | goldfish                         |                  | com          |
| XXX2     | reddog                           |                  | com          |
| 1234     | blackcat                         |                  | com          |
| ylb      | yellowbird                       |                  | com          |
| DOM2     | conejo_blanco                    |                  | com.example  |
| DOMCOMMX | pez_dorado                       |                  | com.example  |
| XXX3     | perro_rojo                       |                  | com.example  |
| 1235     | gato_negro                       |                  | com.example  |
| pjra     | pajaro_amarillo                  |                  | com.example  |
| DOM3     | conejo_blanco                    |                  | example      |
| DOMMX    | pez_dorado                       |                  | example      |
| XXX4     | perro_rojo                       |                  | example      |
| 1236     | gato_negro                       |                  | example      |
|          | pajaro_amarillo                  |                  | example      |
| DOM4     | choco                            |                  | test         |
| DOMLAT   | moka                             |                  | test         |
| XXX6     | 1.0.168.192                      |                  | in-addr.arpa |
| 1238     | xn--mxico-bsa                    | méxico           | test         |
| xnxn     | xn--elpjaroamarillo-pjb          | elpájaroamarillo | test         |

### Entity data

| Handle     | Full Name |
|:-----------|:----------|
| mr_rabbit  | Bill      |
| mr_fish    | Billy     |
| mr_dog     | Bob       |
| mr_cat     | Barry     |
| mr_bird    | Wonka     |
| don_conejo | Tristan   |
| don_pez    | Shane     |
| don_perro  | Layne     |
| don_gato   | Brittney  |
| don_pajaro | Blair     |
| sr_conejo  | Gary      |
| sr_pez     | Gepetto   |
| sr_perro   | Cindy     |
| sr_gato    | Roy       |
| sr_pajaro  |           |
| cone       |           |
| pez        |           |
| perr       |           |
| gat        |           |
| paj        |           |

### Nameserver data

| Handle | ldh(letter, digit, hyphen) name | Unicode name           | IP Address      |
|:-------|:--------------------------------|:-----------------------|:----------------|
| NSE1   | ns1.chopsuey.net                |                        | 192.168.1.1     |
| NSE2   | ns2.chopsuey.net                |                        | 192.168.1.2     |
| NSE3   | ns3.chopsuey.net                |                        | 192.168.1.3     |
| NSE4   | ns4.chopsuey.net                |                        | 1:0:0:0:0:0:0:1 |
| NSE5   | ns5.chopsuey.net                |                        | 2:0:0:0:0:0:0:2 |
| NSE6   | ns1.white.example               |                        | 192.168.1.4     |
| NSE7   | ns2.white.example               |                        | 192.168.1.5     |
| NSE8   | ns3.white.example               |                        | 192.168.1.6     |
| NSE9   | ns4.white.example               |                        | 192.168.1.7     |
| NSE10  | ns5.white.example               |                        | 192.168.1.8     |
| NSE11  | ns1.bright.info                 |                        |                 |
| NSE12  | ns2.bright.info                 |                        |                 |
| NSE13  | ns3.bright.info                 |                        |                 |
| NSE14  | ns4.bright.info                 |                        |                 |
| NSE15  | ns5.bright.info                 |                        |                 |
| NSE16  | ns1.camión.test                 | ns1.xn--camin-3ta.test |                 | 
| NSE17  | ns2.camión.test                 | ns2.xn--camin-3ta.test |                 |
| NSE18  | ns3.camión.test                 | ns3.xn--camin-3ta.test |                 |
| NSE19  | ns4.camión.test                 | ns4.xn--camin-3ta.test |                 |
| NSE20  | ns5.camión.test                 | ns5.xn--camin-3ta.test |                 |

