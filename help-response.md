---
title: Help Response
---

# Configuring RedDog Help Response

RedDog RDAP server gives you the option to implement your own answer to a user help query, as defined in [RDAP RFC 7482 section 3.1.6](http://tools.ietf.org/html/rfc7482#section-3.1.6 "Help Path Segment Specification"). We defined a specific file structure; based in a main folder called help, containing multiple files or "notices",  each one containing information that will be shown to the user when help is requested. This document tells you how to set up that help response:

1.	Find the **WEB-INF/help** folder in your installation directory. It contains a text file called **1Example**, it contains an example of message showed when querying for help. Here is the content of this file:

		title = <title>
	
		description = 
		<paragraph>
	
		<paragraph>
	
		link = <link data>
		link = <link data>
		link = <link data>
	
2.	**Rename and add more notices.** You can either rename or delete the example notice and create more notices. These notices will appear in ascending order so it is recommended to name them with numbers starting from 1.

3.	**Fill each notice.** Each notice can have the following information:
  
  * **Title**: For this just replace `<title>` with the description’s header.
  
  * **Description**: This description is formed of one or more `<paragraph>`, each paragraph is separated by one or more white lines and also is recommended to separate links from the description with a white line.
  
  * **Link**: A description can add one or more links with relevant information, for this you can add one or more lines staring with the word link, an “=” symbol and the `<link_data>`. The link data must have the following form:

		value|rel|href|hreflang|title|media|type

**Example:**

    http://example.com/context_uri | alter/self | http://example.com/target_uri | [ en, ch ] | title | screen | text/html

Other Relevant notes:
* Check [RFC7483 section 7](https://tools.ietf.org/html/rfc7483#section-7 "Responding to Help Queries") for more information about help query and [RFC7483 section 4.2](https://tools.ietf.org/html/rfc7483#section-4.2 "Links") for information about the link data.
* Even though in the example there are various paragraphs and links you can have only one or none.

