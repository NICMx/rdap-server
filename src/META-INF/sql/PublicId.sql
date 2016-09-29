#storeToDatabase
INSERT INTO rdap.public_id VALUES (null,?,?);

#storeDomainPublicIdsToDatabase
INSERT INTO  rdap.domain_public_ids VALUES (?,?);

#storeEntityPublicIdsToDatabase
INSERT INTO  rdap.entity_public_ids VALUES (?,?);

#storeRegistrarPublicIdsToDatabase
INSERT INTO  rdap.registrar_public_ids VALUES (?,?);

#getAll
SELECT * FROM rdap.public_id

#getByDomain
SELECT pid.* FROM rdap.public_id pid INNER JOIN rdap.domain_public_ids dom ON pid.pid_id = dom.pid_id WHERE dom.dom_id=?;

#getByEntity
SELECT pid.* FROM rdap.public_id pid INNER JOIN rdap.entity_public_ids ent ON pid.pid_id = ent.pid_id WHERE ent.ent_id=?;

#getByRegistrar
SELECT pid.* FROM rdap.public_id pid INNER JOIN rdap.registrar_public_ids rar ON pid.pid_id = rar.pid_id WHERE rar.rar_id=?;