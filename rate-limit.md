---
title: Built-in Rate Limit Filter
---

# Built-in Rate Limit Filter

Some clients can monopolize too many RDAP server resources by making too many requests at once.

Normally, and assuming this is considered misbehavior, this problem would be handled by means of firewall rules, load balancing and/or rate-limits within reverse proxies ([mod-qos](http://mod-qos.sourceforge.net/) and [limitipconn2](http://dominia.org/djao/limitipconn2.html), for example). But if those solutions are out of scope or too fancy to apply, there's another alternative: Red Dog's minimalist built-in rate limit filter.

Red Dog's rate limiter is simply a [servlet filter](http://www.oracle.com/technetwork/java/filters-137243.html) that prevents to receive too many requests at once from the same IP address. If a client exceeds a predefined limit, the filter will return an [HTTP 429 status code](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#429).

Notice that what this is doing is to reduce request floods from well-behaved clients. The client is still free to continue sending simultaneous requests, so this approach is by no means a DOS attack prevention system. It also prevents the server from wasting too many resources handling the whole request, but notice that servlet filters act fairly late during a packet processing pipeline.

If the filter fulfills the implementer needs, the `web.xml` file must include at its `<web-app>` tag something like this:

```xml
<filter>
	<filter-name>RateLimitFilter</filter-name>
	<filter-class>mx.nic.rdap.server.filter.RateLimitFilter</filter-class>
	<init-param>
		<param-name>limit</param-name>
		<param-value>3</param-value>
	</init-param>
</filter>
<filter-mapping>
	<filter-name>RateLimitFilter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>
```

The [`mx.nic.rdap.server.filter.RateLimitFilter`](https://github.com/NICMx/rdap-server/blob/master/src/main/java/mx/nic/rdap/server/filter/RateLimitFilter.java) is already included within Red Dog's WAR. The `limit` init-param is the number of simultaneous requests allowed per IP address. This is all standard [filter](http://docs.oracle.com/cd/E13222_01/wls/docs81/webapp/web_xml.html#1015950) and [filter-mapping](http://docs.oracle.com/cd/E13222_01/wls/docs81/webapp/web_xml.html#1039330) syntax.

[Back to the optional configuration index](documentation.html#further-configuration-optional).

