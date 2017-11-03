---
title: Using Apache Shiro
breadcrums: ["Documentation", "documentation.html", "Further Custom Configuration", "documentation.html#further-custom-configuration", "User Authentication", "authentication.html"]
wheretogo: ["Configuring Response Privacy", "response-privacy.html"]
---

# {{ page.title }}

## Index

1. [Why Apache Shiro?](#why-apache-shiro)
1. [Apache Shiro at Red Dog](#apache-shiro-at-red-dog)
   1. [Enabling Apache Shiro](#enabling-apache-shiro)
   1. [Configuring Apache Shiro](#configuring-apache-shiro)
      1. [Users information](#users-information)
      1. [Basic authentication](#basic-authentication)
      1. [Restrict access](#restrict-access)
         1. [Basic authentication with permissive access](#basic-authentication-with-permissive-access)
         1. [Basic authentication with restrictive access](#basic-authentication-with-restrictive-access)
         1. [Basic authentication with permissive and restrictive access](#basic-authentication-with-permissive-and-restrictive-access)
         1. [Disable basic authentication](#disable-basic-authentication)
   1. [Apache Shiro's Subject](#apache-shiros-subject)
1. [More documentation](#more-documentation)


## Why Apache Shiro?

[Apache Shiro<sup>TM</sup>](https://shiro.apache.org/) is an open source security framework provided by the [Apache Software Fundation](https://www.apache.org/)  licensed just as Red Dog's ([Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)). Citing Apache Shiro's own description:

> Apache Shiro™ is a powerful and easy-to-use Java security framework that performs authentication, authorization, cryptography, and session management.

Red Dog uses [Apache Shiro<sup>TM</sup>](https://shiro.apache.org/) in order to provide authentication mechanisms as described in [RFC 7481 section 3.2](https://tools.ietf.org/html/rfc7481#section-3.2), since the framework facilitates these tasks thanks to its flexibility.

## Apache Shiro at Red Dog

This section will explain how Red Dog uses [Apache Shiro<sup>TM</sup>](https://shiro.apache.org/), this configuration can be altered in order to suite whatever needs the implementer has.

> ![Warning](img/warning.svg) Before further reading, it's recommended that the reader visit [Apache Shiro Reference Documentation](https://shiro.apache.org/reference.html).

Currently Red Dog has the following dependencies from Apache Shiro:
* [Core v1.4.0](https://mvnrepository.com/artifact/org.apache.shiro/shiro-core/1.4.0)
* [Shiro-web v1.4.0](https://mvnrepository.com/artifact/org.apache.shiro/shiro-web/1.4.0)

### Enabling Apache Shiro

By default Apache Shiro is enabled and loaded from the server configuration, this is achieved with the following lines at [`WEB-INF/web.xml`](https://github.com/NICMx/rdap-server/blob/master/src/main/webapp/WEB-INF/web.xml):

```xml
<listener>
    <listener-class>org.apache.shiro.web.env.EnvironmentLoaderListener</listener-class>
</listener>

<filter>
    <filter-name>ShiroFilter</filter-name>
    <filter-class>org.apache.shiro.web.servlet.ShiroFilter</filter-class>
</filter>

<filter-mapping>
    <filter-name>ShiroFilter</filter-name>
    <url-pattern>/*</url-pattern>
    <dispatcher>REQUEST</dispatcher>
    <dispatcher>ERROR</dispatcher>
    <dispatcher>FORWARD</dispatcher>
    <dispatcher>INCLUDE</dispatcher>
</filter-mapping>
```

Using this lines, Apache Shiro is expecting to find a configuration file at any of the options:
* `WEB-INF/shiro.ini`
* `shiro.ini` at the root of classpath.

By default, Red Dog has the configuration file at [`WEB-INF/shiro.ini`](https://github.com/NICMx/rdap-server/blob/master/src/main/webapp/WEB-INF/shiro.ini); file that is explained in the following section.

This configuration has been obtained following Apache Shiro's documentation. To learn more about Apache Shiro's Configuration, click [here](https://shiro.apache.org/web.html#configuration).

### Configuring Apache Shiro

Once Apache Shiro has been enabled, the configuration file is needed, just as stated in the previous section. This file is at [`WEB-INF/shiro.ini`](https://github.com/NICMx/rdap-server/blob/master/src/main/webapp/WEB-INF/shiro.ini) and currently has a default configuration, that can be altered to satisfy whatever needs the implementer has.

Just as the configuration set at [`WEB-INF/web.xml`](https://github.com/NICMx/rdap-server/blob/master/src/main/webapp/WEB-INF/web.xml), this configuration is based on Apache Shiro's documentation. To learn more about Apache Shiro's INI Configuration, click [here](https://shiro.apache.org/configuration.html#ini-configuration "INI Configuration") and [here](https://shiro.apache.org/web.html#web-ini-configuration "Web INI Configuration").

Basically Red Dog's [`WEB-INF/shiro.ini`](https://github.com/NICMx/rdap-server/blob/master/src/main/webapp/WEB-INF/shiro.ini) file has the following behavior regarding to the use of Apache Shiro:
* Get the [users information](#users-information) (password and access roles) from the database.
* Use [basic authentication](#basic-authentication), as mandatory and/or optional.
* Optionally, [restrict access](#restrict-access) to specific paths.

Each of these behaviors is explained in the following sections.

> ![Warning](img/warning.svg) WARNING: Red Dog has authentication disabled by default, using the [`anon`](https://shiro.apache.org/static/1.4.0/apidocs/org/apache/shiro/web/filter/authc/AnonymousFilter.html) filter; if authentication is needed, the following steps will help to enable authentication using Apache Shiro.

#### Users information

Users and its corresponding roles are loaded from a database, using a [`BasicDataSource`](https://tomcat.apache.org/tomcat-8.0-doc/api/org/apache/tomcat/dbcp/dbcp2/BasicDataSource.html) object. This object [can be configured](https://commons.apache.org/proper/commons-dbcp/configuration.html) with the necessary properties to reach a database and get the necessary data from there. Here's how that configuration looks like in the [`WEB-INF/shiro.ini`](https://github.com/NICMx/rdap-server/blob/master/src/main/webapp/WEB-INF/shiro.ini) file (inside the `[main]` section):

```
[main]

ds = org.apache.tomcat.dbcp.dbcp2.BasicDataSource 
ds.driverClassName = 
ds.url = 
ds.username = 
ds.password = 
```

After the declaration and configuration, the `BasicDataSource` is set as the `DataSource` of the [`CustomSecurityRealm`](https://github.com/NICMx/rdap-server/blob/master/src/main/java/mx/nic/rdap/server/shiro/CustomSecurityRealm.java) class that extends the Shiro's [`JdbcRealm`](https://shiro.apache.org/static/1.4.0/apidocs/org/apache/shiro/realm/jdbc/JdbcRealm.html) class. Beside this, the queries that are used to authenticate a user and get its roles must also be defined (all this is still inside the `[main]` section):

```
[main]

customRealm = mx.nic.rdap.server.shiro.CustomSecurityRealm
customRealm.dataSource = $ds
customRealm.authenticationQuery = 
customRealm.userRolesQuery = 
```

> ![Warning](img/warning.svg) If the implementer is using the SQL implementation provided by Red Dog, the queries will be something like this:
> ```ini
> customRealm.authenticationQuery = SELECT rus_pass FROM rdap_user WHERE rus_name = ?
> customRealm.userRolesQuery = SELECT rar_name FROM rdap_user_role WHERE rus_name = ?
> ```
> Both queries must receive the user's name to retrieve the corresponding information.

> ![Warning](img/warning.svg) If user roles are going to be used, these must be also configured at property `user_roles` when [Configuring Red Dog's Server Behavior](behavior-configuration.html#user_roles). When validating user roles, the characters case is indifferent (eg. Role 'JUDGE' is the same that 'judge').

The `customRealm` must be used, and so is assigned to the [default `SecurityManager`](http://shiro.apache.org/configuration.html#default-securitymanager):

```ini
[main]

securityManager.realms = $customRealm
```

As the reader may note, the users are loaded from a database and also is expected that its passwords are stored as plain text. This behavior can be modified to suite other needs (probably some needs related to security aspects). If the reader wishes to learn more about this, probably [Apache Shiro Configuration](https://shiro.apache.org/configuration.html#apache-shiro-configuration) will be of help, or also the section [More documentation](#more-documentation) could be visited.

#### Basic authentication

The server supports [Basic Authentication](https://tools.ietf.org/html/rfc2617#section-2) using the [BasicHttpAuthenticationFilter](https://shiro.apache.org/static/1.4.0/apidocs/org/apache/shiro/web/filter/authc/BasicHttpAuthenticationFilter.html) filter provided by Apache Shiro. Currently only 2 attributes of the filter are configured:
* `applicationName`: the application name that will be returned as realm in the HTTP header "WWW-Authenticate" when responding a 401 code (see [RFC 7235 section 4.1](https://tools.ietf.org/html/rfc7235#section-4.1)). Default value: `rdap-server`.
* `enabled`: to enable/disable the filter. Default value: `true`.

The following lines are the ones that set the configuration mentioned in the previous paragraph:

```ini
[main]

authcBasic = org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
authcBasic.applicationName = rdap-server
authcBasic.enabled = false
```

This filter works in conjunction with the [users information](#users-information) configuration, since the information received from the Authorization HTTP Request header will pass through this filter so that it can authenticate the user. In order to do the authentication, the filter will use the realm set at `securityManager.realms`; here's where Apache Shiro simplifies things, since all the authentication process is handled internally.

#### Restrict access

If the authentication filter is going to be used, then it must be related to an URL path or paths. The `[urls]` section helps to define this behavior, the following sections explain what must be done in order to configure the access according to the implementer needs. The **default** behavior is [**Disable basic authentication**](#disable-basic-authentication).

##### Basic authentication with permissive access

The term "permissive access" means that the resources are still public, but whenever the HTTP header "WWW-Authenticate" is received the credentials will be validated in order to authenticate the user. If the authentication fails, then the corresponding **401** HTTP response code will be returned; if the authentication succeeds, then the user will be authenticated and will have access to whatever things the implementer decides.

At Shiro's configuration file, the `authcBasic` filter must be enabled and in the `[urls]` section the corresponding resources must be related to the filter with the `permissive` qualifier (this qualifier is used internally by Apache Shiro to enable the permissive behavior). The configuration at `shiro.ini` must include these lines:

```ini
[main]
authcBasic.enabled = true
[urls]
/** = authcBasic[permissive]
```

The path expression `/**` means that all the resources of the server will pass through the corresponding filter whenever they are requested, more of this can be seen at Apache Shiro's [Web INI configuration](https://shiro.apache.org/web.html#web-ini-configuration).

##### Basic authentication with restrictive access

The term "restrictive access" means that the resources will always require authentication in order to solve client requests. Whenever the client request a protected resource (eg. "/domains") the server will respond with a challenge so that the client can send its credentials (see more about this process at [RFC 7235 section 2](https://tools.ietf.org/html/rfc7235#section-2)). When the credentials are received by the server, then these will be validated to grant or reject access to the resource.

At Shiro's configuration file, the `authcBasic` filter must be enabled and in the `[urls]` section the corresponding resources must be related to the filter. The configuration at `shiro.ini` must include these lines:

```ini
[main]
authcBasic.enabled = true
[urls]
/domains/** = authcBasic
```

The path expression `/domains/**` means that the resources under and including `/domains` will pass through the corresponding filter, more of this can be seen at Apache Shiro's [Web INI configuration](https://shiro.apache.org/web.html#web-ini-configuration).

##### Basic authentication with permissive and restrictive access

Thanks to the use of URL paths expressions (eg. `/**`, `/myresource1/**`) the implementer can use distinct filters at each resource that's required, this allows to Red Dog mix the use of permissive and restrictive access in a simple way.

The configuration will be a combination of [permissive access](#basic-authentication-with-permissive-access) and [restrictive access](#basic-authentication-with-restrictive-access), so the `shiro.ini` file must include these lines:

```ini
[main]
authcBasic.enabled = true
[urls]
/domains/** = authcBasic
/** = authcBasic[permissive]
```

The order of the filters matters (see more at Apache Shiro's documentation of [`[urls]`](https://shiro.apache.org/web.html#urls-)). If the resource `/domains` is requested then the `authcBasic` filter will proceed to use restrictive access, since the URL path expression `/domains/**` is before the `/**` expression.

##### Disable basic authentication

Disabling the authentication is simple, and by **default** is disabled.

One approach to disable the authentication is to remove/comment the use and declaration of the `authcBasic` filter that's used and go up from there removing the unnecessary configurations at `shiro.ini` (eg. the `customRealm` isn't needed so it can be erased, and since it's going to be erased then the line `securityManager.realms = $customRealm` isn't needed as well). Once that the declaration and use of `authcBasic` is removed, instead of using the `authcBasic` filter the `anon` filter can be used; this is the [`AnonymousFilter`](https://shiro.apache.org/static/1.4.0/apidocs/org/apache/shiro/web/filter/authc/AnonymousFilter.html) provided by Apache Shiro (see more information about Apache Shiro's [Default Filters](https://shiro.apache.org/web.html#default-filters)). So the `shiro.ini` file will look like this:

```ini
[main]
# ANY OTHER CUSTOM CONFIG #
[urls]
/** = none
```

Another approach that can be used is to disable the `authcBasic` filter that was defined, the default `[urls]` section doesn't need to be modified if the `authcBasic` was being used. When the filter is disabled, all of the URLs that had a relation with that filter will skip such filter (see more at Apache Shiro's documentation [Enabling and Disabling Filters](http://shiro.apache.org/web.html#enabling-and-disabling-filters)).

The configuration at `shiro.ini` must include these lines:

```ini
[main]
authcBasic.enabled = false
[urls]
/** = authcBasic[permissive]
```

### Apache Shiro's Subject

Red Dog uses the [`Subject`](https://shiro.apache.org/static/1.4.0/apidocs/org/apache/shiro/subject/Subject.html) object provided by Apache Shiro. This object simplifies the validations related to users access level based on authentication and the use of access roles.

The [`Subject`](https://shiro.apache.org/static/1.4.0/apidocs/org/apache/shiro/subject/Subject.html) is always available thanks to [`org.apache.shiro.SecurityUtils.getSubject()`](https://shiro.apache.org/static/1.4.0/apidocs/org/apache/shiro/SecurityUtils.html#getSubject--) function, and it contains relevant information such as the user's name and roles (if it's authenticated).

This object is used by [`UserInfo`](https://github.com/NICMx/rdap-server/blob/master/src/main/java/mx/nic/rdap/server/privacy/UserInfo.java#L10) struct, which is a struct useful for privacy settings (learn more at [Configuring Response Privacy](response-privacy.html)). Beside this, the `Subject` also helps to verify the max number of results than can be shown to a specific user (eg. a request to `/domains` could validate this, just as seen in [`DomainSearchServlet`](https://github.com/NICMx/rdap-server/blob/master/src/main/java/mx/nic/rdap/server/servlet/DomainSearchServlet.java#L85) class).

## More documentation

Apache Shiro can be customized to suite the needs that the implementer has, Red Dog's implementation is just one approach of many configurations that can be done.

The purpose of this page is to understand only what's has been done at Red Dog, further explanations are out of scope. So, here's a small guide if the reader wishes to know more about Apache Shiro:
* [Apache Shiro Main Page](https://shiro.apache.org/)
* [Apache Shiro Community Forums](https://shiro.apache.org/forums.html)
* [Apache Shiro Reference Documentation](https://shiro.apache.org/reference.html)
* [Apache Shiro Documentation](https://shiro.apache.org/documentation.html)
* [Apache Shiro Web Support](https://shiro.apache.org/web.html)