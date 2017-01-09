---
layout: default
title: Response privacy
---

# Configuring _RedDog_ server response privacy

This document tells you how to set up the RedDog server response privacy.

1.	Find the **WEB-INF/privacy_default** folder in your installation directory. It contains a “_properties_” file for each object used in the server response and has a similar structure. In example, here is the content of the **domain.properties** file: 

	    #For each attribute you can use the values: owner, authenticate, any or none.
	    #The owner value specify that only the owner of the object can see the attribute.
	    #The authenticate value specify that any authenticate user can see the attribute.
	    #The any value specify that the attribute can be seen by authenticated and unauthenticated users.
	    #The none value specify that the attribute cannot be seen by any user
	    handle = any
    	ldhName = any
    	unicodeName = any
    	variants = any
    	nameservers = any
    	secureDNS = any
    	entities = any
    	status = any
    	publicIds = any
    	remarks = any
    	links = any
    	port43 = any
    	events = any
    	network = any
    	
2. Configure the object attributes privacy.
3. Run the server and make a search request to test the configuration.

Other relevant notes:
* **WEB-INF** folder is the default location for the privacy configuration, but you can configure the folder where the server will read the privacy properties:
   1.   In the **WEB-INF/web.xml**, find the following lines:  

```
<!-- <context-param> -->
	<!-- <param-name>privacySettingsUserPath</param-name> -->
	<!-- <param-value></param-value> -->
<!-- </context-param> -->
```

   2.	Uncomment them, and write a valid directory path in the “_param-value_”.
   3.	Save the changes and test the configuration.

* The default value of privacy is commented in each file.  
* Each object has its file for privacy configuration.
* Check [RFC 7483](https://tools.ietf.org/html/rfc7483 "JSON Responses for the Registration Data Access Protocol (RDAP)") for a list of each object and attribute that form a RDAP response.  
 
