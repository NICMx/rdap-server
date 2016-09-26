#getByNameServerId
SELECT sta_id FROM rdap.nameserver_status WHERE nse_id=?;

#getByDomainId
SELECT sta_id FROM rdap.domain_status WHERE dom_id=?;

#getByEntityId
SELECT sta_id FROM rdap.entity_status WHERE ent_id=?;

#getByRegistrarId
SELECT sta_id FROM rdap.registrar_status WHERE rar_id=?;

#storeNameserverStatusToDatabase
INSERT INTO rdap.nameserver_status VALUES (?,?);

#storeDomainStatusToDatabase
INSERT INTO rdap.domain_status VALUES (?,?);

#storeEntityStatusToDatabase
INSERT INTO rdap.entity_status VALUES (?,?);

#storeRegistrarStatusToDatabase
INSERT INTO rdap.registrar_status VALUES (?,?);
