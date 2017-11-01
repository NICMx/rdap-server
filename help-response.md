---
title: Help Response
breadcrums: ["Documentation", "documentation.html", "Further Custom Configuration", "documentation.html#further-custom-configuration"]
---

# Configuring Red Dog Help Response

RDAP servers are expected to provide metadata (such as terms of service, supported authentication methods and technical support contact) when [prompted for help](http://tools.ietf.org/html/rfc7482#section-3.1.6 "Help Path Segment Specification"). These responses are expected to look [like this](https://tools.ietf.org/html/rfc7483#section-7).

The content of the Help Response can be configured in the **WEB-INF/notices/** directory by creating an XML file named `help.xml`.

The `help.xml` file must have the following format:

- A root element **help** and child elements called **notice**.
- A **notice** element can have four attributes, they must be ordered as listed below.
	- An optional **title** that represents the title of the notice.
	- An optional **type** string denoting a registered type of remark or notice [see Section 10.2.1](https://tools.ietf.org/html/rfc7483#section-10.2.1).
	- A required **description** element that contains at least one **line** child element, for the purposes of conveying any descriptive text.
		- Each **line** element in the **description** element contains a single complete division of human-readable text indicating to clients where do the semantic breaks exist.
	- An optional **links** element that contains at least one **link** child element.
		- A **link** element string has a required attribute named **href**, also contains another optional attributes **rel**, **hreflang**, **title**, **media** and **type**.
	
The formal definition of the notice can be found [here](https://tools.ietf.org/html/rfc7483#section-4.3 "Notices").
	
The formal definition of the link array can be found [here](https://tools.ietf.org/html/rfc7483#section-4.2 "Links").

Here is an example of the `help.xml` file with all the elements that can contain the file.

```xml
<help>
   <notice>
      <title>Example notice</title>
      <type>A registered IANA type</type>
      <description>
         <line>a line of the description of the help notice</line>
         <line>another line</line>
         <line>another line</line>
      </description>
      <links>
         <link rel="" href="http://example.com.mx" hreflang="" title="" media="" type="">http://example.com.mx</link>
         <link rel="" href="http://example2.com.mx" hreflang="" title="" media="" type="">http://example2.com.mx</link>
      </links>
   </notice>
</help>
```

More notices can be added by placing new **notice** elements to the XML file, e.g.

```xml
<help>
   <notice>
      <description>
         <line>A description line of the first help notice.</line>
      </description>
   </notice>
   <notice>
      <title>Second example notice</title>
      <description>
         <line>A line from a different notice.</line>
      </description>
   </notice>
   <notice>
      <title>Third example notice</title>
      <description>
         <line>Another line from a third notice.</line>
         <line>Another line from the same third notice.</line>
      </description>
   </notice>
</help>
```

## Where to go next

The minimum RDAP server is now running. You might want to go to the main documentation index's [optional documentation](documentation.html#further-configuration-optional) to tweak it further.
