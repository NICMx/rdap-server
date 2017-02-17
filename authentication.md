---
title: User Authentication
---

# User Authentication

Red Dog can use standard HTTP authentication mechanisms to decide whether information should be served or not.

It should play along with whatever authentication mechanism you set up in the servlet container, though we have a [tutorial](basic-authentication-tomcat.html) for the Tomcat/Basic combination in particular.

Once that's setup, you can define which HTTP object should be served to which users through standard [security constraints](https://docs.oracle.com/cd/E13222_01/wls/docs81/webapp/web_xml.html#1017885) ([example](http://docs.oracle.com/javaee/5/tutorial/doc/bncbx.html#bnccm)), though in Red Dog's case you can also define constraints [per object field](response-privacy.html).

You might need [this](optional-authentication.html) if anonymous users are relevant to you, as well.


