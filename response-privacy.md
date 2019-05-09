---
title: Configuring Response Privacy
breadcrums: ["Documentation", "documentation.html", "Further Custom Configuration", "documentation.html#further-custom-configuration", "User Authentication", "authentication.html"]
---

# {{ page.title }}

## Index

* [Introduction](#introduction)
* [Privacy configuration files](#privacy-configuration-files)
* [Configuring attributes access](#configuring-attributes-access)
* [Using custom roles](#using-custom-roles)
* [Role specific privacy](#role-specific-privacy)
* [Replace VCard values](#replace-vcard-values)

## Introduction

As described by [RFC 7481 section 3.3](https://tools.ietf.org/html/rfc7481#section-3.3), the RedDog server allows to protect data using authorization policies.

Data can be served either to everyone, authenticated users, only the object owners, to specific users with custom roles, or to no one. This level of access can be configured per object field or attribute, as well as the owner objects can be defined by the implementer.

The following sections will help to understand how the privacy settings are configured and how they can be personalized.

## Privacy configuration files

These files are placed at the folder `WEB-INF/privacy` in the installation directory (the whole list of files can be seen at github's folder [`WEB-INF/privacy`](https://github.com/NICMx/rdap-server/tree/master/src/main/webapp/WEB-INF/privacy)). They should look like this:

![PRIVACY CONFIGURATION PATH](img/privacy-configuration-path.png)

There is a **“properties”** file for each object returned in the server responses and each file contains the attributes of such object based on [RFC 7483](https://tools.ietf.org/html/rfc7483). For instance, the content of the `WEB-INF/privacy/domain.properties` file is:

```
#For each attribute you can use the values: 'any', 'authenticated', 'none', 'owner' or a custom user role configured in
#property 'user_roles' at 'configuration.properties'.
#The 'any' value specifies that the attribute can be seen by authenticated and unauthenticated users. [DEFAULT]
#The 'authenticated' value specifies that any authenticated user can see the attribute.
#The 'none' value specifies that nobody can see the attribute.
#The 'owner' value specifies that only the owner of the object can see the attribute (can be used with custom user roles).
#A custom user role value specifies that users with such role can see the attribute (can be used with 'owner' value).
#Custom user roles can be represented in a list separated by commas (',') and can be mixed with the value 'owner'.
#Eg. lang = myrole1, owner, myrole2

#handle = any
#ldhName = any
#unicodeName = any
#variants = any
#nameservers = any
#secureDNS = any
#entities = any
#status = any
#publicIds = any
#remarks = any
#links = any
#port43 = any
#events = any
#network = any
#lang = any
```

Just as stated in the first comments of each file, each attribute can have a specific value to indicate its privacy level. The allowed values and its meaning are listed here:
* **any**: everybody (unauthenticated users and authenticated users) can see the attribute. This is the **default** value of every object property.
* **authenticated**: only authenticated users can see the attribute.
* **none**: nobody can see the attribute.
* **owner**: only the owner of the object can see the attribute, this value can be mixed with “_custom roles_”. This value works in conjunction with the configuration property [owner_roles_*](behavior-configuration.html#owner_roles_).
* **List of custom roles**: users with this role(s) can see the attribute, this value(s) can be mixed with “_owner_” value. More information of how this is configured is shown [below](#using-custom-roles).


> ![Warning](img/warning.svg) If the values `authenticated`, `owner`, and/or **List of custom roles** are ment to be used, the server must be properly configured to authenticate users so that the privacy settings behave as expected. Learn more at [User Authentication](authentication.html).


## Configuring attributes access

The default privacy value of every property is “**any**”, to state that all the information is available to all the users. If this behavior isn’t what the implementer desires it can be modified to satisfy its needs.

To adapt the privacy access of each object to the implementer needs, the files listed at `WEB-INF/privacy` can be modified. The next example will help to comprehend this. Taking the same file used as example in the previous section, `WEB-INF/privacy/domain.properties`, the privacy values can be modified like this (header was removed to ease comprehension):

```
handle = any
ldhName = any
unicodeName = any
variants = authenticated
nameservers = authenticated
secureDNS = owner
entities = owner
status = none
publicIds = none
#remarks = any
#links = any
#port43 = any
#events = any
#network = any
#lang = any
```

First of all, the properties must be uncommented so that its configured values can be loaded when the server initializes. What has been done in the example is this:
* `handle`, `ldhName`, `unicodeName` and the rest of the commented properties will be accessible to everybody whenever a domain object is requested.
* `variants` and `nameservers` will be displayed only if the user that made the request is successfully authenticated.
* `secureDNS` and `entities` will be displayed exclusively to the user that is owner of the domain object requested, this means that the user must be successfully authenticated and satisfy other requirements (see [owners configuration](behavior-configuration.html#owner_roles_*)).
* `status` and `publicIds` will never be displayed, not even to authenticated users nor owners.

> ![Warning](img/warning.svg) Whenever a property of the response is hidden due to privacy settings, a remark of type `object truncated due to authorization` is added to the corresponding object. If the object is an **Entity** also a `private` status is added to its status list.

The example didn’t include the use of custom roles to keep things simple, but this configuration is explained in the next section.

## Using custom roles

The declaration and use of custom roles is optional. If the implementer wishes to use custom roles, these must be first defined when [Configuring RedDog's Server Behavior](behavior-configuration.html#user_roles). The roles configured can be used at the privacy settings of each object.

To simplify the explanation of the custom roles use, the same example will be taken (`WEB-INF/privacy/domain.properties`) and used only with custom roles mixtures. The file can be modified as follows, assuming that the roles **president**, **governor**, and **judge** are already configured:

```
handle = president
ldhName = governor
unicodeName = judge
variants = president, governor
nameservers = president, governor, judge
secureDNS = owner, judge
#entities = any
#status = any
#publicIds = any
#remarks = any
#links = any
#port43 = any
#events = any
#network = any
#lang = any
```

What’s going to happen when a request is made to get a domain object is:
* None of the uncommented attributes will be shown if the user isn’t authenticated.
* If the user is authenticated, then it must have any of the roles:
  * **president**: to see `handle`, `variants`, and `nameservers` attributes.
  * **governor**: to see `ldhName`, `variants`, and `nameservers` attributes.
  * **judge**: to see `unicodeName`, `nameservers`, and `secureDNS` attributes.
* If the authenticated user is the owner of the domain object, then `secureDNS` can be seen in the response.

The roles are complementary to each other, so if a subject has several roles then all the information that can be displayed to such roles will be displayed. As an example, if an user has the roles **governor** and **judge** then all the information available for those roles will be displayed in the response.

Just as mentioned previously, the **owner** value can be mixed with custom roles, since the value itself is kind of a role.

> ![Warning](img/warning.svg) The values `any`, `none`, and `authenticated` can’t be mixed with each other neither with custom roles nor `owner` value.

## Role specific privacy
For Entity and Vcard privacy files, could exists specific role privacy settings, to create a specific privacy file for the role type that have an entity or a vcard. It needs Its own custom privacy file.

The file name rules are:

* For entities: `entity_ROLE_[RFC7483#10.2.4 Role in upper case].properties`
* For vcard: `vcard_ROLE_[RFC7483#10.2.4 Role in upper case].properties`

Vcard object doesn’t have a `role` value, so it’s taken from its `entity` container.

It is not necessary to fill all attributes with privacy settings, when the server loads the privacy configurations, it takes first the server privacy files, then the privacy files configured by the implementer (entity.properties), then override it with the specific privacy for the role type.

Example:

```
{
     "objectClassName" : "domain",
     "handle" : "XXXX",
     "ldhName" : "0.2.192.in-addr.arpa",
     "nameservers" :
     [
       {
         "objectClassName" : "nameserver",
         "ldhName" : "ns1.rir.example"
       },
       {
         "objectClassName" : "nameserver",
         "ldhName" : "ns2.rir.example"
       }
     ],
     "events" :
     [
       {
         "eventAction" : "registration",
         "eventDate" : "1990-12-31T23:59:59Z"
       },
       {
         "eventAction" : "last changed",
         "eventDate" : "1991-12-31T23:59:59Z",
         "eventActor" : "joe@example.com"
       }
     ],
     "entities" :
     [
       {
         "objectClassName" : "entity",
         "handle" : "XXXX",
         "vcardArray":[
           "vcard",
           [
             ["version", {}, "text", "4.0"],
             ["fn", {}, "text", "Joe User"],
             ["kind", {}, "text", "individual"],
             ["lang", {
               "pref":"1"
             }, "language-tag", "fr"],
             ["lang", {
               "pref":"2"
             }, "language-tag", "en"],
             ["org", {
               "type":"work"
             }, "text", "Example"],
             ["title", {}, "text", "Research Scientist"],
             ["role", {}, "text", "Project Lead"],
             ["adr",
               { "type":"work" },
               "text",
               [
                 "",
                 "Suite 1234",
                 "4321 Rue Somewhere",
                 "Quebec",
                 "QC",
                 "G1V 2M2",
                 "Canada"
               ]
			   ],
             ["tel",
               { "type":["work", "voice"], "pref":"1" },
               "uri", "tel:+1-555-555-1234;ext=102"
             ],
             ["email",
               { "type":"work" },
               "text", "joe.user@example.com"
             ]
           ]
         ],
         "roles" : [ "registrar" ],
         "events" :
         [
           {
             "eventAction" : "registration",
             "eventDate" : "1990-12-31T23:59:59Z"
           },
           {
             "eventAction" : "last changed",
             "eventDate" : "1991-12-31T23:59:59Z",
             "eventActor" : "joe@example.com"
           }
         ]
       },
	   {
         "objectClassName" : "entity",
         "handle" : "XXXX",
         "vcardArray":[
           "vcard",
           [
             ["version", {}, "text", "4.0"],
             ["fn", {}, "text", "Joe User"],
             ["kind", {}, "text", "individual"],
             ["lang", {
               "pref":"1"
             }, "language-tag", "fr"],
             ["lang", {
               "pref":"2"
             }, "language-tag", "en"],
             ["org", {
               "type":"work"
             }, "text", "Example"],
             ["title", {}, "text", "Research Scientist"],
             ["role", {}, "text", "Project Lead"],
             ["adr",
               { "type":"work" },
               "text",
               [
                 "",
                 "Suite 1234",
                 "4321 Rue Somewhere",
                 "Quebec",
                 "QC",
                 "G1V 2M2",
                 "Canada"
               ]
			   ],
             ["tel",
               { "type":["work", "voice"], "pref":"1" },
               "uri", "tel:+1-555-555-1234;ext=102"
             ],
             ["email",
               { "type":"work" },
               "text", "joe.user@example.com"
             ]
           ]
         ],
         "roles" : [ "registrant" ],
         "events" :
         [
           {
             "eventAction" : "registration",
             "eventDate" : "1990-12-31T23:59:59Z"
           },
           {
             "eventAction" : "last changed",
             "eventDate" : "1991-12-31T23:59:59Z",
             "eventActor" : "joe@example.com"
           }
         ]
       }
     ]
   }
```

The previous response was a domain response, that includes two entities attributes.

It is possible to hide or show some information based on the role type that the entity has.

Using the previous response, it is necessary to hide some attributes for all type of entities, so, in the path `WEB-INF/privacy/entity.properties`, it’s configured the privacy based on the requirements.

Now for some reason, RDAP service provider is forced to show some values to all publics, when the entity has the role type `registrant`.

So in order to fill the requirement, in the path `WEB-INF/privacy/`, create a new privacy file for the entity with role type `registrant`, by adding the words `_ROLE_` + [the role type] + `.properties`, for this example, the new file will be `entity_ROLE_REGISTRANT.properties`.

And now, in this new file, the attributes privacy level can be less private for an entity that has the role type `registrant`.



## Replace VCard values
The attributes from an entity’s vcard object can be replaced by any customized message.

In any attribute, in addition to ‘any’, ‘authenticated’, ‘none’, ‘owner’ or a custom user role configured in, it can be added a new privacy value called obscured. Also, it is needed to insert a pipe &#x7c; next to obscured value, followed by the text that is required to display instead of the original vcard attribute value, as it’s shown on the following example:

`some_valid_attribute = obscured | To request the information please go to www.example.com/request_info.html`

