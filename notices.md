---
title: Configuring Red Dog's response Notices
breadcrums: ["Documentation", "documentation.html", "Further Custom Configuration", "documentation.html#further-custom-configuration"]
wheretogo: ["Configuring Red Dog's response Events", "events.html"]
---

# {{ page.title }}

## Index

* [Introduction](#introduction)
* [Notices per request](#notices-per-request)

* [Updater thread](#updater-thread)

## Introduction

Additional to the [TOS configuration](terms-of-service.html), if the implementer wants to add custom notices, it can be added by creating a `notices.xml` file.

* The Notices are added as a **notice** in every server response, including error responses and help response.
* The Notices can have unlimited notices (TOS file can have only one notice).
* The Notices are **optional**.

The content of the notices can be configured in the **WEB-INF/notices/** directory by creating an XML file named `notices.xml`. This file is **optional**, so by default there isn't any file with this name.

The `notices.xml` file must have the following format:

- A root element **notices** and many child element called **notice**.
- A **notice** element can have four attributes, they must be ordered as listed below.
	- An optional **title** that represents the title of the notice.
	- An optional **type** string denoting a registered type of remark or notice [see Section 10.2.1](https://tools.ietf.org/html/rfc7483#section-10.2.1).
	- A required **description** element that contains at least one **line** child element, for the purposes of conveying any descriptive text.
		- Each **line** element in the **description** element contains a single complete division of human-readable text indicating to clients where do the semantic breaks exist.
	- An optional **links** element that contains at least one **link** child element.
		- A **link** element string has a required attribute named **href**, also contains another optional attributes **rel**, **hreflang**, **title**, **media** and **type**.
	
The formal definition of the notice can be found [here](https://tools.ietf.org/html/rfc7483#section-4.3 "Notices").
	
The formal definition of the link array can be found [here](https://tools.ietf.org/html/rfc7483#section-4.2 "Links").

Here is an example of a `notices.xml` file with all the elements that can contain the file.

```xml
<notices>
   <notice>
      <title>Example 1</title>
      <type>A registered IANA type</type>
      <description>
         <line>A line of the terms of service</line>
         <line>another line</line>
         <line>another line</line>
      </description>
      <links>
         <link rel="" href="http://example.com" hreflang="" title="" media="" type="">http://example.com</link>
         <link rel="" href="http://example.com/file" hreflang="" title="" media="" type="">http://example.com/file</link>
      </links>
   </notice>
   <notice>
      <title>Example 2</title>
      <type>A registered IANA type</type>
      <description>
         <line>A line of the terms of service</line>
         <line>another line</line>
         <line>another line</line>
      </description>
      <links>
         <link rel="" href="http://example.com" hreflang="" title="" media="" type="">http://example.com</link>
         <link rel="" href="http://example.com/file" hreflang="" title="" media="" type="">http://example.com/file</link>
      </links>
   </notice>
</notices>
```

## Notices per request
The previous configuration was to add notices to all kind of user requests, but if for some reasons,
it is necessary to add notices to a certain type of request, it can be done by creating files for the 
specific requests, this files are **optional** and the structure of each individual file, is like a `notices.xml` file.

| File name    | Request Type |
|--------------|--------------|
| entity.xml   | /entity/*    |
| domain.xml   | /domain/*    |
| ns.xml       | /nameserver/* |
| autnum.xml   | /autnum/*    |
| ip.xml       | /ip/*        | 


## Updater thread
The RDAP server contains a thread that verifies from time to time, if there are changes in the “notices per request” files.

The files to update the old “notices per request” files should add the extension `.updated`

This thread is activated when the value `notices_timer_update` in` configuration.properties` is different from 0.

The thread will be activated every `notices_timer_update` seconds, The thread checks if there are files to be updated, if it detects that the files exist, read and validate the new file and if valid, it will update the notices in the requests and also will replace the previous file. Otherwise, if no new notice file exists, the thread will fall asleep and wake up after the configured seconds.

