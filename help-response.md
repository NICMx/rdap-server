---
title: Help Response
---

# Configuring Red Dog Help Response

RDAP servers are expected to provide metadata (such as terms of service, supported authentication methods and technical support contact) when [prompted for help](http://tools.ietf.org/html/rfc7482#section-3.1.6 "Help Path Segment Specification"). These responses are expected to look [like this](https://tools.ietf.org/html/rfc7483#section-7).

You can configure the content of the Help Response in the **WEB-INF/help** directory:

1.	Find the **WEB-INF/help** folder in Red Dog's installation directory. Every file here will become a notice in the compiled response. By default, there is a sample file called **1Example**, replicated here:

		title = <title>
	
		description = 
		<paragraph>
	
		<paragraph>
	
		link = <link data>
		link = <link data>
		link = <link data>
	
2.	**Rename and add more notices.** You can create more notices and either tweak or delete the sample one. Once compiled, these notices will appear in ascending order (based on their file names), so you might want to number them as well.

3.	**Fill in each notice.** Each notice can have the following information:
  
  * **Title**: Replace `<title>` with the description’s header.
  
  * **Description**: This description is made out of one or more `<paragraph>`s, separated by one or more white lines. It is also recommended to separate links from the description using a white line.
  
  * **Link**: These are zero or more entries that point to relevant information. The syntax of `<link_data>` is
 
		value|rel|href|hreflang|title|media|type

**Example:**

	http://example.com/context_uri | alter/self | http://example.com/target_uri | [ en, ch ] | title | screen | text/html

The formal definition of the link array can be found [here](https://tools.ietf.org/html/rfc7483#section-4.2 "Links").

