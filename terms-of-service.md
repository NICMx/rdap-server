---
title: Configuring Red Dog's Terms of Service
breadcrums: ["Documentation", "documentation.html", "Further Custom Configuration", "documentation.html#further-custom-configuration"]
wheretogo: ["Configuring Red Dog’s response Notices", "notices.html"]
---

# {{ page.title }}

Additional to the [help response configuration](help-response.html), a notice to represent the server's terms of service can be configured. The main differences between the help configuration and the terms of service configuration are:
* The terms of service are added as a **notice** in every server response, including error responses and help response.
* The terms of service can only have **one notice** (help response can have several notices).
* The terms of service are **optional**.

The content of the terms of service can be configured in the **WEB-INF/notices/** directory by creating an XML file named `tos.xml`. This file is **optional**, so by default there isn't any file with this name.

The `tos.xml` file must have the following format:

- A root element **tos** and one child element called **notice**.
- A **notice** element can have four attributes, they must be ordered as listed below.
	- An optional **title** that represents the title of the notice.
	- An optional **type** string denoting a registered type of remark or notice [see Section 10.2.1](https://tools.ietf.org/html/rfc7483#section-10.2.1).
	- A required **description** element that contains at least one **line** child element, for the purposes of conveying any descriptive text.
		- Each **line** element in the **description** element contains a single complete division of human-readable text indicating to clients where do the semantic breaks exist.
	- An optional **links** element that contains at least one **link** child element.
		- A **link** element string has a required attribute named **href**, also contains another optional attributes **rel**, **hreflang**, **title**, **media** and **type**.
	
The formal definition of the notice can be found [here](https://tools.ietf.org/html/rfc7483#section-4.3 "Notices").
	
The formal definition of the link array can be found [here](https://tools.ietf.org/html/rfc7483#section-4.2 "Links").

Here is an example of a `tos.xml` file with all the elements that can contain the file.

```xml
<tos>
   <notice>
      <title>Example terms of service</title>
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
</tos>
```
