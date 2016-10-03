#storeToDatabase
INSERT INTO rdap.domain VALUES (null,?,?,?,?);

#storeDomainEntityRoles 
INSERT INTO rdap.domain_entity_roles VALUES (?,?,?);

#getByLdhName
SELECT * FROM rdap.domain WHERE dom_ldh_name=?;

#getDomainById
SELECT * FROM rdap.domain WHERE dom_id=?;