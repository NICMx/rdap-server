#storeToDatabase
INSERT INTO rdap.entity VALUES (null, ?, ?);

#getById
SELECT * FROM rdap.entity e WHERE e.ent_id = ?;

#getByHandle
SELECT * FROM rdap.entity e WHERE e.ent_handle = ?;

#getByDomain
SELECT ent.*, dom.rol_id FROM rdap.entity ent JOIN rdap.domain_entity_roles dom ON dom.ent_id=ent.ent_id WHERE dom.dom_id=?;

#getAll
SELECT * FROM rdap.entity ORDER BY 1 ASC;

#getEntitysEntitiesQuery
SELECT DISTINCT (ent.ent_id),  ent.ent_handle, ent.ent_port43 FROM rdap.entity ent JOIN rdap.entity_entity_roles rol ON rol.ent_id = ent.ent_id WHERE rol.main_ent_id = ?;

#getDomainsEntitiesQuery
SELECT DISTINCT (ent.ent_id),  ent.ent_handle, ent.ent_port43 FROM rdap.entity ent JOIN rdap.domain_entity_roles rol ON rol.ent_id = ent.ent_id WHERE rol.dom_id = ?;

#getNameserversEntitiesQuery
SELECT DISTINCT (ent.ent_id),  ent.ent_handle, ent.ent_port43 FROM rdap.entity ent JOIN rdap.nameserver_entity_roles rol ON rol.ent_id = ent.ent_id WHERE rol.nse_id = ?;

#getIdByHandle
SELECT ent_id FROM rdap.entity ent WHERE ent.ent_handle = ?;

#searchByPartialHandle
SELECT * FROM rdap.entity e WHERE e.ent_handle LIKE ?;

#searchByPartialName
SELECT DISTINCT (ent.ent_id),  ent.ent_handle, ent.ent_port43 FROM rdap.entity ent JOIN rdap.entity_contact eco ON eco.ent_id=ent.ent_id JOIN rdap.vcard vca ON vca.vca_id=eco.vca_id WHERE vca.vca_name LIKE ?;

#getByName
SELECT * FROM rdap.entity ent JOIN rdap.entity_contact eco ON eco.ent_id=ent.ent_id JOIN rdap.vcard vca ON vca.vca_id=eco.vca_id WHERE vca.vca_name = ?;