---
title: User Authentication
breadcrums: ["Documentation", "documentation.html", "Further Custom Configuration", "documentation.html#further-custom-configuration"]
---

# {{ page.title }}

Red Dog can use standard HTTP authentication mechanisms to decide whether information should be served or not.

It should play along with whatever authentication mechanism is set up in the servlet container, though actually uses [Apache Shiro<sup>TM</sup>](https://shiro.apache.org/) to simplify the authentication process.

Using [Apache Shiro<sup>TM</sup>](https://shiro.apache.org/) the server can define which HTTP object can be served to which users, according to the implementer needs. Beside this protection level, Red Dog also supports the use of constraints per object field or attribute.

More information can be seen in the following links:
* [Using Apache Shiro<sup>TM</sup>](using-apache-shiro.html)
* [Configuring Response Privacy](response-privacy.html)

