#storeDomainsEntityRol
INSERT INTO rdap.domain_entity_roles VALUES (?, ?, ?);

#storeEntitiesEntityRol
INSERT INTO rdap.entity_entity_roles VALUES (?, ?, ?);

#storeNSEntityRol
INSERT INTO rdap.nameserver_entity_roles VALUES (?, ?, ?);

#getDomainRol
SELECT rol.rol_id FROM rdap.domain_entity_rol rol WHERE rol.dom_id = ? AND rol.ent_id = ?;

#getEntityRol
SELECT rol.rol_id FROM rdap.entity_entity_rol rol WHERE rol.dom_id = ? AND rol.ent_id = ?;

#getNSRol
SELECT rol.rol_id FROM rdap.nameserver_entity_rol rol WHERE rol.dom_id = ? AND rol.ent_id = ?;