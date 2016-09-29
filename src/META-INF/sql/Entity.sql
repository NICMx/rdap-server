#storeToDatabase
INSERT INTO rdap.entity VALUES (null, ?, ?, ?, ?);

#getById
SELECT * FROM rdap.entity e WHERE e.ent_id = ?;

#getByHandle
SELECT * FROM rdap.entity e WHERE e.ent_handle = ?;

#getByDomain
SELECT ent.*, dom.rol_id FROM rdap.entity ent JOIN rdap.domain_entity_roles dom ON dom.ent_id=ent.ent_id WHERE dom.dom_id=?;

#getAll
SELECT * FROM rdap.entity ORDER BY 1 ASC;