---
title: Configuring Red Dog's response Events
breadcrums: ["Documentation", "documentation.html", "Further Custom Configuration", "documentation.html#further-custom-configuration"]
wheretogo: ["Rate Limit Filter", "rate-limit.html"]
---

# {{ page.title }}

## Index

* [Introduction](#introduction)
* [Querying live DB](#querying-live-db)
* [Updater thread](#updater-thread)

## Introduction

If the implementer wants to add events in every request response.

* The Events are added in every server request response.
* The Events are **optional**.

The content of the events can be configured in the **WEB-INF/notices/** directory by creating an XML file named `events.xml`. This file is **optional**, so by default there isn't any file with this name.

The `events.xml` file must have the following format:

- A root element **events** and many child element called **event**.
- A **event** element can have four attributes, they must be ordered as listed below.
	- A required **eventAction** a string denoting the reason for the event. (Only valid eventActions are allowed, lower case, you can see which are the valid ones [here](https://www.iana.org/assignments/rdap-json-values/rdap-json-values.xhtml))
	- An optional **eventActor** an optional identifier denoting the actor responsible for the event
	- A required **eventDate** a string containing the time and date the event occurred.
	- An optional **links** element that contains at least one **link** child element.
		- A **link** element string has a required attribute named **href**, also contains another optional attributes **rel**, **hreflang**, **title**, **media** and **type**.
	
The formal definition of the event can be found [here](https://tools.ietf.org/html/rfc7483#section-4.5 "Events").
	
The formal definition of the link array can be found [here](https://tools.ietf.org/html/rfc7483#section-4.2 "Links").

Here is an example of a `events.xml` file with all the elements that can contain the file.

```xml
<events>
   <event>
      <eventAction>registration</eventAction>
      <eventActor>A registered IANA type</eventActor>
      <eventDate>2018-12-30T23:59:59Z</eventDate>
      <links>
         <link rel="" href="http://example.com" hreflang="" title="" media="" type="">http://example.com</link>
         <link rel="" href="http://example.com/file" hreflang="" title="" media="" type="">http://example.com/file</link>
      </links>
   </event>
   <event>
      <eventAction>last update of rdap database</eventAction>
      <eventActor>A registered IANA type</eventActor>
      <eventDate>2018-12-30T23:59:59Z</eventDate>
      <links>
         <link rel="" href="http://example.com" hreflang="" title="" media="" type="">http://example.com</link>
         <link rel="" href="http://example.com/file" hreflang="" title="" media="" type="">http://example.com/file</link>
      </links>
   </event>
</notices>
```

## Querying live DB
When a RDAP service provider is querying its database directly, and therefore, using real-time data, and also
an eventAction of type `last update of RDAP database` exists in the events file. The implementator may require that the timestamp in the event
is the time when the client perform the request, in this case, the value can be override it by enabling the property [is_db_data_live](behavior-configuration.html#is_db_data_live) in the `configuration.properties` file. 


## Updater thread
The RDAP server contains a thread that verifies from time to time, if there are changes in the “events” files.

The files to update the old “notices per request” files should add the extension `.updated`

This thread is activated when the value `events_timer_update_time` in` configuration.properties` is greater than or equal to 10.

The thread will be activated every `events_timer_update_time` seconds, The thread checks if there are files to be updated, if it detects that the files exist, read and validate the new file and if valid, it will update the notices in the requests and also will replace the previous file. Otherwise, if no new notice file exists, the thread will fall asleep and wake up after the configured seconds.

