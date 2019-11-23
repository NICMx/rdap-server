---
title: User consent configuration
breadcrums: ["Documentation", "documentation.html", "Further Custom Configuration", "documentation.html#further-custom-configuration"]
wheretogo: ["Replaceable Link Response", "replaceable-link-response.html"]
---

# {{ page.title }}

Starting from RedDog server v1.5.0 and rdap-core 1.3.0, If your systems allow to publish user contact information when they consent it you can publish that information in the RDAP response.

RedDog server offer 3 options:
- The first option `(NONE)` is that you do not offer user consent, ergo you can skip this page and continue reading other configurations.
- The second option `(GLOBAL)` is that you offer to the user a checkbox that they allow to publish his contact information.
- The last option `(ATTRIBUTES)` is similar to the second, but, you have multiple checkboxes, this way, the user only choice specific attributes to be published.

## Global consent
To use global consent, the entity object needs to have an instance of the class `UserConsentGlobal`, also in this instance you need to indicate the consent is granted by the user `setGlobalConsent("true")`

## Consent by attributes
To use consent by attributes, the entity object needs to have an instance of the class `UserConsentByAttribute`.
In the instance of `UserConsentByAttribute` you need to set to true the attributes that will be published.

## RDAP-sql-provider
If you are using our reference sql provider implementation we have provided two tables, each table are related to the two previous modes of user consent.

If you are using Global consent, you have to use the table `user_global_consent`, otherwise use the table `user_consent_by_attributes`.

Also you need to configure the type of consent to be use, more information [here](data-access-configuration.html#user_consent_type)

Our recommendation is to fill the tables only by the entities that gave consent to publish his contact information.

