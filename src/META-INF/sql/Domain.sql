#storeToDatabase
INSERT INTO rdap.domain VALUES (null,?,?,?,?);

#storeDomainEntityRoles 
INSERT INTO rdap.domain_entity_roles VALUES (?,?,?);

#getByLdhName
SELECT * FROM rdap.domain WHERE dom_ldh_name=?;

#getDomainById
SELECT * FROM rdap.domain WHERE dom_id=?;

#searchByNameWZone
SELECT domain.* FROM rdap.domain WHERE domain.dom_ldh_name LIKE ? AND domain.zone_id = ? ORDER BY 1 LIMIT ?;

#searchByNameWOutZone
SELECT domain.* FROM rdap.domain WHERE domain.dom_ldh_name LIKE ? ORDER BY 1 LIMIT ?;

#searchByNsLdhName
SELECT DISTINCT (dom.dom_id), dom.dom_ldh_name, dom.dom_handle, dom.dom_port43 FROM rdap.domain dom JOIN rdap.domain_nameservers dom_ns ON dom_ns.dom_id = dom.dom_id JOIN rdap.nameserver ns ON ns.nse_id = dom_ns.nse_id WHERE  ns.nse_ldh_name LIKE ? ORDER BY 1 LIMIT ?;

#searchByNsIp
SELECT dom.* FROM rdap.domain dom JOIN rdap.domain_nameservers dom_ns ON dom_ns.dom_id = dom.dom_id JOIN rdap.nameserver ns ON ns.nse_id = dom_ns.nse_id JOIN rdap.ip_address ip	ON ip.nse_id = ns.nse_id WHERE IF(?=4, INET_ATON(?),INET6_ATON(?)) = ip.iad_value ORDER BY 1 LIMIT ?; 