#entity
SELECT "XXXX" AS handle ,"whois.example.net" AS port43 ,"active,validated" AS rdap_status ,"registration| 2011-12-31T23:59:59Z| XXXX, reregistration| 2012-12-01T23:59:59Z | XXXX" AS events ,"XXXX |registrar" AS entities ,"1|IANA Registrar ID, 2|NIC ID " AS public_ids ,"Joe Jobs |Orange |www.orange.mx |jj@orange.mx |81 8818181 |81 8181818181 |248.697.0908 |Engineer |local |Mexico |Monterrey |Nuevo Leon |Altavista |100 |Av. Luis Elizondo |64000" AS vcard FROM dual;


#nameserver
SELECT   'XXXX5' AS handle,  'ns1.xn--fo-5ja.example' AS ldh_name,     'whois.example.net' AS port43,     'active,validated' AS rdap_status,     'registration| 2011-12-31T23:59:59Z| XXXX, reregistration| 2012-12-01T23:59:59Z| XXXX' AS events,     '4| 192.0.2.1, 6| 2001:db8::2:1' AS ip_addresses FROM DUAL UNION ALL SELECT     'XXX6' AS handle,     'ns2.xn--fo-5ja.example' AS ldh_name,     'whois.example.net' AS port43,     'active,validated' AS rdap_status,     'registration| 2011-12-31T23:59:59Z|' AS events,     '4| 192.0.2.1, 6| 2001:db8::2:1' AS ip_addresses FROM DUAL;

#domain
SELECT "XXXX" AS handle,"miespacio.mx" AS ldh_name, "whois.example.net" AS port43,"active,validated" AS rdap_status,  "registration| 2011-12-31T23:59:59Z | XXXX, reregistration| 2012-12-01T23:59:59Z| XXXX" AS events, "XXXX|registrar" AS entities, ".EXAMPLE Spanish |{xn--fo-cka.example, xn--fo-fka.example} |{unregistered, registration restricted}" AS variants,  "XXXX" AS namerservers, "true|true|12345" AS secureDNS, "12345|3|49FD46E6C4B45C55D4AC|1" AS dsData, "1|IANA Registrar ID, 2|NIC ID " AS publicIds FROM dual;