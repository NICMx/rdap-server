#storeToDatabase
INSERT INTO rdap.nameserver VALUES(null,?,?,?);

#storeDomainNameserversToDatabase
INSERT INTO rdap.domain_nameservers VALUES(?,?);

#findByName
SELECT * FROM rdap.nameserver nse WHERE nse.nse_ldh_name=?;

#getByDomainId
SELECT nse.* FROM rdap.nameserver nse JOIN rdap.domain_nameservers dom ON dom.nse_id=nse.nse_id WHERE dom.dom_id=?;

#existByName
SELECT 1 FROM rdap.nameserver nse WHERE nse.nse_ldh_name=?;


#searchByPartialName
SELECT DISTINCT(nse.nse_id), nse.nse_handle,nse.nse_ldh_name, nse.nse_port43 FROM rdap.nameserver nse WHERE nse.nse_ldh_name like ? ORDER BY 1 LIMIT ?;

#searchByName
SELECT DISTINCT(nse.nse_id), nse.nse_handle,nse.nse_ldh_name, nse.nse_port43 FROM rdap.nameserver nse WHERE nse.nse_ldh_name=? ORDER BY 1 LIMIT ?;

#searchByIp4
SELECT DISTINCT(nse.nse_id), nse.nse_handle,nse.nse_ldh_name, nse.nse_port43 FROM rdap.nameserver nse join rdap.ip_address ipa on ipa.nse_id=nse.nse_id WHERE ipa.iad_value=INET_ATON(?) ORDER BY 1 LIMIT ?;

#searchByIp6
SELECT DISTINCT(nse.nse_id), nse.nse_handle,nse.nse_ldh_name, nse.nse_port43 FROM rdap.nameserver nse join rdap.ip_address ipa on ipa.nse_id=nse.nse_id WHERE ipa.iad_value=INET6_ATON(?) ORDER BY 1 LIMIT ?;

#getAll
SELECT * FROM rdap.nameserver nse;