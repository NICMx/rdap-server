---
title: Search Request
---

# Configuring RedDog server Search’s Request

This document tells you how to set up the RedDog server to respond search’s request.
1.	Find the **WEB-INF/configuration.properties** file in your installation directory. It contains the following lines:

        #Optional.Language of the server. Values en=english, es=español. Default:en
        #language=
        #Required.Zones managed (separated by commas). example: mx, lat, com
        #zones=
        #Optional.Minimum length of the search pattern. Default 5
        #minimum.search.pattern.length=
        #Optional.Max number of results for the authenticated user. Default 20
        #max.number.result.authenticated.user=
        #Optional.Max number of results for the unauthenticated user. Default 10
        #max.number.result.unauthenticated.user=
        #Required. Indicates the roles that are the owners of the rdap objects. example: registrar, administrative, registrant
        #ownerRoles =

 
2.	Configure the **zones** and the **ownerRoles** attributes and, if you want, the other attributes (uncomment them first).

3.	Run the server and make a search request to test the configuration.

Other relevant notes:
* **WEB-INF** folder is the default location for the **configuration.properties** file, but you can configure the folder where the server will read that file:
    1.	In the **WEB-INF/web.xml**, find the following lines:
 
		<!-- <context-param> -->
			<!-- <param-name>
				rdapConfigurationUserPath
			</param-name> -->
			<!-- <param-value>
			</param-value> -->
		<!-- </context-param> -->

    2.	Uncomment them, and write a valid directory path in the “_param-value_”.
    3.	Save the changes and test the configuration.
* You must verify that the value assigned to “_zones_” attribute is an existing zone(s) in the database, when the servers is starting, this matching values will be validated.


