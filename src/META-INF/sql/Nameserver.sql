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
SELECT * FROM rdap.nameserver nse WHERE nse.nse_ldh_name like ?;

#searchByIp4
SELECT nse.* FROM rdap.nameserver nse join rdap.ip_address ipa on ipa.nse_id=nse.nse_id where ipa.iad_value=INET_ATON(?);

#searchByIp6
SELECT nse.* FROM rdap.nameserver nse join rdap.ip_address ipa on ipa.nse_id=nse.nse_id where ipa.iad_value=INET6_ATON(?);
