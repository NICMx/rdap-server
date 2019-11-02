---
title: Example - Creating a domain into RedDog's built-in schema
breadcrums: ["Documentation", "documentation.html", "Installation/Configuration", "documentation.html#installationconfiguration", "Option 3 - Using RedDog’s builtin schema", "documentation.html#option-3---using-red-dogs-builtin-schema"]
wheretogo: ["Configuring RedDog's reference implementation", "data-access-configuration.html"]
scriptLink: https://github.com/NICMx/rdap-sql-provider/blob/master/src/test/resources/META-INF/sql/Database.sql
rawScriptLink: https://raw.githubusercontent.com/NICMx/rdap-sql-provider/master/src/test/resources/META-INF/sql/Database.sql
---

# {{ page.title }}

## Introduction

This is an step guide to create a simple domain for RedDog

Create the zone or TLD, in this case our TLD is "example"

```
INSERT INTO `rdap`.`zone` (`zone_id`, `zone_name`) VALUES ('1', 'example');
```

Create a domain, this domain is called "myfirstdomain"

```
INSERT INTO `rdap`.`domain` (`dom_id`, `dom_handle`, `dom_unicode_name`, `dom_port43`, `zone_id`) VALUES ('1', 'dom_handle_1', 'myfirstdomain', '', '1');
```

Create the event "registration" for the new domain, eac_id is a [catalog]({{ page.scriptLink }}#L1653)

```
INSERT INTO `rdap`.`event` (`eve_id`, `eac_id`, `eve_date`) VALUES ('1', '1', '20191031');
INSERT INTO `rdap`.`domain_events` (`dom_id`, `eve_id`) VALUES ('1', '1');
```

Create new status for the domain, also a [catalog]({{ page.scriptLink }}#L1618)

```
INSERT INTO `rdap`.`domain_status` (`dom_id`, `sta_id`) VALUES ('1', '3');
INSERT INTO `rdap`.`domain_status` (`dom_id`, `sta_id`) VALUES ('1', '4');
```

Create two nameservers or hosts for our new domain.

```
INSERT INTO `rdap`.`nameserver` (`nse_id`, `nse_handle`, `nse_unicode_name`) VALUES ('1', 'ns_handle_1', 'ns1.myfirstdomain.example');
INSERT INTO `rdap`.`nameserver_status` (`nse_id`, `sta_id`) VALUES ('1', '11');
INSERT INTO `rdap`.`ip_address` (`iad_id`, `nse_id`, `iad_type`, `iad_value`) VALUES ('1', '1', '4', INET_ATON('192.0.2.1'));
INSERT INTO `rdap`.`ip_address` (`iad_id`, `nse_id`, `iad_type`, `iad_value`) VALUES ('2', '1', '4', INET_ATON('192.0.2.2'));
INSERT INTO `rdap`.`ip_address` (`iad_id`, `nse_id`, `iad_type`, `iad_value`) VALUES ('3', '1', '6', INET6_ATON('2001:db8::123'));
INSERT INTO `rdap`.`ip_address` (`iad_id`, `nse_id`, `iad_type`, `iad_value`) VALUES ('4', '1', '6', INET6_ATON('2001:db8::124'));
INSERT INTO `rdap`.`event` (`eve_id`, `eac_id`, `eve_date`) VALUES ('2', '1', '20191031');
INSERT INTO `rdap`.`nameserver_events` (`nse_id`, `eve_id`) VALUES ('1', '2');

INSERT INTO `rdap`.`nameserver` (`nse_id`, `nse_handle`, `nse_unicode_name`) VALUES ('2', 'ns_handle_2', 'ns2.myfirstdomain.example');
INSERT INTO `rdap`.`nameserver_status` (`nse_id`, `sta_id`) VALUES ('2', '11');
INSERT INTO `rdap`.`ip_address` (`iad_id`, `nse_id`, `iad_type`, `iad_value`) VALUES ('5', '2', '4', INET_ATON('192.0.2.3'));
INSERT INTO `rdap`.`ip_address` (`iad_id`, `nse_id`, `iad_type`, `iad_value`) VALUES ('6', '2', '4', INET_ATON('192.0.2.4'));
INSERT INTO `rdap`.`ip_address` (`iad_id`, `nse_id`, `iad_type`, `iad_value`) VALUES ('7', '2', '6', INET6_ATON('2001:db8::125'));
INSERT INTO `rdap`.`ip_address` (`iad_id`, `nse_id`, `iad_type`, `iad_value`) VALUES ('8', '2', '6', INET6_ATON('2001:db8::126'));
INSERT INTO `rdap`.`event` (`eve_id`, `eac_id`, `eve_date`) VALUES ('3', '1', '20191031');
INSERT INTO `rdap`.`nameserver_events` (`nse_id`, `eve_id`) VALUES ('2', '3');
```

Then link the new nameservers to the domain

```
INSERT INTO `rdap`.`domain_nameservers` (`dom_id`, `nse_id`) VALUES ('1', '1');
INSERT INTO `rdap`.`domain_nameservers` (`dom_id`, `nse_id`) VALUES ('1', '2');
```

Create an entity that represent a registrar, this entity could be linked to more domains that are related to this registrar

```
INSERT INTO `rdap`.`entity` (`ent_id`, `ent_handle`) VALUES ('1', 'registrar1');
-- rol_id is a [catalog]({{ page.scriptLink }}#)L1672
INSERT INTO `rdap`.`entity_role` (`ent_id`, `rol_id`) VALUES ('1', '6');
-- Create the contact information
INSERT INTO `rdap`.`vcard` (`vca_id`, `vca_name`, `vca_company_name`, `vca_company_url`, `vca_email`, `vca_voice`, `vca_fax`, `vca_job_title`) VALUES ('1', 'Domains4Everyone', 'Dom4E', 'Dom4e.com', 'contact@dom4e.com', '4511231234', '', '');
INSERT INTO `rdap`.`vcard_postal_info` (`vpi_id`, `vca_id`, `vpi_type`, `vpi_country`, `vpi_country_code`, `vpi_city`, `vpi_street1`, `vpi_state`, `vpi_postal_code`) VALUES ('1', '1', '', 'US', 'US', 'TX', 'street1', 'TX', '78520');
INSERT INTO `rdap`.`entity_contact` (`ent_id`, `vca_id`) VALUES ('1', '1');
-- Because this entity is a registrar, It could have a public ID
INSERT INTO `rdap`.`public_id` (`pid_id`, `pid_type`, `pid_identifier`) VALUES ('1', 'IANA PublicID', '123456789');
INSERT INTO `rdap`.`entity_public_ids` (`ent_id`, `pid_id`) VALUES ('1', '1');
```

Now we create an abuse contact for the registrar 

```
INSERT INTO `rdap`.`entity` (`ent_id`, `ent_handle`) VALUES ('2', 'registrar1_abuse');
INSERT INTO `rdap`.`vcard` (`vca_id`, `vca_name`, `vca_company_name`, `vca_email`, `vca_voice`, `vca_job_title`) VALUES ('2', 'John Doe', 'Dom4e', 'abuse@dom4e.com', '4511231234', 'abuse contact');
INSERT INTO `rdap`.`vcard_postal_info` (`vpi_id`, `vca_id`, `vpi_country`, `vpi_country_code`, `vpi_city`, `vpi_street1`, `vpi_state`, `vpi_postal_code`) VALUES ('2', '2', 'US', 'US', 'TX', 'street1', 'TX', '78520');
INSERT INTO `rdap`.`entity_contact` (`ent_id`, `vca_id`) VALUES ('2', '2');
```

The abuse entity for the entity registrar are linked in the entity_entity_roles table

```
INSERT INTO `rdap`.`entity_entity_roles` (`main_ent_id`, `ent_id`, `rol_id`) VALUES ('1', '2', '4');
```

Going back to our domain, we create an entity for the registrant

```
INSERT INTO `rdap`.`entity` (`ent_id`, `ent_handle`) VALUES ('3', 'registrant_myfirstdomain');
INSERT INTO `rdap`.`vcard` (`vca_id`, `vca_name`, `vca_company_name`, `vca_email`, `vca_voice`) VALUES ('3', 'Jane Doe', 'myfirstdomain', 'JaneDoe@publicmail.com', '4517894561');
INSERT INTO `rdap`.`vcard_postal_info` (`vpi_id`, `vca_id`, `vpi_country`, `vpi_country_code`, `vpi_city`, `vpi_street1`, `vpi_state`, `vpi_postal_code`) VALUES ('3', '3', 'MX', 'MX', 'CDMX', 'calle1', 'CDMX', '01000');
INSERT INTO `rdap`.`entity_contact` (`ent_id`, `vca_id`) VALUES ('3', '3');
```

We link our entities to the domain as registrar and registrant, remember rol_id is a [catalog]({{ page.scriptLink }}#L1672).

```
INSERT INTO `rdap`.`domain_entity_roles` (`dom_id`, `ent_id`, `rol_id`) VALUES ('1', '1', '6');
INSERT INTO `rdap`.`domain_entity_roles` (`dom_id`, `ent_id`, `rol_id`) VALUES ('1', '3', '1');
```


The result of the inserts should be like the next json

Self links are generated automatically

```
{
  "rdapConformance": [
    "rdap_level_0"
  ],
  "objectClassName": "domain",
  "handle": "dom_handle_1",
  "links": [
    {
      "value": "http://reddog.test.foo:8080/rdap-server/domain/myfirstdomain.example.",
      "rel": "self",
      "href": "http://reddog.test.foo:8080/rdap-server/domain/myfirstdomain.example.",
      "type": "application/rdap+json"
    }
  ],
  "events": [
    {
      "eventAction": "registration",
      "eventDate": "2019-10-31T00:00:00Z"
    }
  ],
  "status": [
    "update prohibited",
    "transfer prohibited"
  ],
  "entities": [
    {
      "objectClassName": "entity",
      "handle": "registrar1",
      "links": [
        {
          "value": "http://reddog.test.foo:8080/rdap-server/entity/registrar1",
          "rel": "self",
          "href": "http://reddog.test.foo:8080/rdap-server/entity/registrar1",
          "type": "application/rdap+json"
        }
      ],
      "entities": [
        {
          "objectClassName": "entity",
          "handle": "registrar1_abuse",
          "roles": [
            "abuse"
          ],
          "vcardArray": [
            "vcard",
            [
              [
                "version",
                {},
                "text",
                "4.0"
              ],
              [
                "fn",
                {},
                "text",
                "John Doe"
              ],
              [
                "org",
                {},
                "text",
                "Dom4e"
              ],
              [
                "email",
                {},
                "text",
                "abuse@dom4e.com"
              ],
              [
                "tel",
                {
                  "type": "voice"
                },
                "text",
                "4511231234"
              ],
              [
                "title",
                {},
                "text",
                "abuse contact"
              ],
              [
                "adr",
                {},
                "text",
                [
                  "",
                  "",
                  "street1",
                  "TX",
                  "TX",
                  "78520",
                  "US"
                ]
              ]
            ]
          ]
        }
      ],
      "roles": [
        "registrar"
      ],
      "publicIds": [
        {
          "type": "IANA PublicID",
          "identifier": "123456789"
        }
      ],
      "vcardArray": [
        "vcard",
        [
          [
            "version",
            {},
            "text",
            "4.0"
          ],
          [
            "fn",
            {},
            "text",
            "Domains4Everyone"
          ],
          [
            "org",
            {},
            "text",
            "Dom4E"
          ],
          [
            "url",
            {},
            "uri",
            "Dom4e.com"
          ],
          [
            "email",
            {},
            "text",
            "contact@dom4e.com"
          ],
          [
            "tel",
            {
              "type": "voice"
            },
            "text",
            "4511231234"
          ],
          [
            "adr",
            {},
            "text",
            [
              "",
              "",
              "street1",
              "TX",
              "TX",
              "78520",
              "US"
            ]
          ]
        ]
      ]
    },
    {
      "objectClassName": "entity",
      "handle": "registrant_myfirstdomain",
      "links": [
        {
          "value": "http://reddog.test.foo:8080/rdap-server/entity/registrant_myfirstdomain",
          "rel": "self",
          "href": "http://reddog.test.foo:8080/rdap-server/entity/registrant_myfirstdomain",
          "type": "application/rdap+json"
        }
      ],
      "roles": [
        "registrant"
      ],
      "vcardArray": [
        "vcard",
        [
          [
            "version",
            {},
            "text",
            "4.0"
          ],
          [
            "fn",
            {},
            "text",
            "Jane Doe"
          ],
          [
            "org",
            {},
            "text",
            "myfirstdomain"
          ],
          [
            "email",
            {},
            "text",
            "JaneDoe@publicmail.com"
          ],
          [
            "tel",
            {
              "type": "voice"
            },
            "text",
            "4517894561"
          ],
          [
            "adr",
            {},
            "text",
            [
              "",
              "",
              "calle1",
              "CDMX",
              "CDMX",
              "01000",
              "MX"
            ]
          ]
        ]
      ]
    }
  ],
  "lang": "en",
  "ldhName": "myfirstdomain.example.",
  "unicodeName": "myfirstdomain.example.",
  "nameservers": [
    {
      "objectClassName": "nameserver",
      "handle": "ns_handle_1",
      "links": [
        {
          "value": "http://reddog.test.foo:8080/rdap-server/nameserver/ns1.myfirstdomain.example.",
          "rel": "self",
          "href": "http://reddog.test.foo:8080/rdap-server/nameserver/ns1.myfirstdomain.example.",
          "type": "application/rdap+json"
        }
      ],
      "events": [
        {
          "eventAction": "registration",
          "eventDate": "2019-10-31T00:00:00Z"
        }
      ],
      "status": [
        "active"
      ],
      "ldhName": "ns1.myfirstdomain.example.",
      "unicodeName": "ns1.myfirstdomain.example.",
      "ipAddresses": {
        "v4": [
          "192.0.2.1",
          "192.0.2.2"
        ],
        "v6": [
          "2001:db8:0:0:0:0:0:123",
          "2001:db8:0:0:0:0:0:124"
        ]
      }
    },
    {
      "objectClassName": "nameserver",
      "handle": "ns_handle_2",
      "links": [
        {
          "value": "http://reddog.test.foo:8080/rdap-server/nameserver/ns2.myfirstdomain.example.",
          "rel": "self",
          "href": "http://reddog.test.foo:8080/rdap-server/nameserver/ns2.myfirstdomain.example.",
          "type": "application/rdap+json"
        }
      ],
      "events": [
        {
          "eventAction": "registration",
          "eventDate": "2019-10-31T00:00:00Z"
        }
      ],
      "status": [
        "active"
      ],
      "ldhName": "ns2.myfirstdomain.example.",
      "unicodeName": "ns2.myfirstdomain.example.",
      "ipAddresses": {
        "v4": [
          "192.0.2.3",
          "192.0.2.4"
        ],
        "v6": [
          "2001:db8:0:0:0:0:0:125",
          "2001:db8:0:0:0:0:0:126"
        ]
      }
    }
  ]
}
```
