#storeToDatabase
INSERT INTO rdap.vcard VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?);

#storeEntityContact
INSERT INTO rdap.entity_contact VALUES (?, ?);

#getById
SELECT * FROM rdap.vcard v WHERE v.vca_id = ?;

#getAll
SELECT * FROM rdap.vcard ORDER BY 1 ASC;

#getByEntityId
SELECT vca.* FROM rdap.vcard vca JOIN rdap.entity_contact eco ON eco.vca_id = vca.vca_id WHERE eco.ent_id = ?;

