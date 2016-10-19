#storeToDatabase
INSERT INTO rdap.domain VALUES (null,?,?,?,?);

#storeDomainEntityRoles 
INSERT INTO rdap.domain_entity_roles VALUES (?,?,?);

#getByLdhName
SELECT * FROM rdap.domain WHERE dom_ldh_name=?;

#getDomainById
SELECT * FROM rdap.domain WHERE dom_id=?;

#searchByNameWZone
SELECT domain.* FROM domain dom WHERE domain.dom_ldh_name LIKE ? AND dom.zone_id = ?; 

#searchByNameWOutZone
SELECT domain.* FROM domain WHERE domain.dom_ldh_name LIKE ?;

#searchByNsLdhName
SELECT DISTINCT domain.* FROM   domain dom JOIN domain_nameservers dom_ns ON dom_ns.dom_id = dom.dom_id JOIN nameserver ns ON ns.nse_id = dom_ns.nse_id WHERE  ns.nse_ldh_name LIKE ?;

#searchByNsIp
SELECT domain.* FROM domain dom JOIN domain_nameservers dom_ns ON dom_ns.dom_id = dom.dom_id JOIN nameserver ns ON ns.nse_id = dom_ns.nse_id JOIN ip_address ip	ON ip.nse_id = ns.nse_id WHERE IF(?=4, INET_ATON(?),INET6_ATON(?)) = ip.iad_value; 