#storeToDatabase
INSERT INTO rdap.vcard VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?);

#storeRegistrarContact
INSERT INTO rdap.registrar_contact VALUES (?, ?);

#getById
SELECT * FROM rdap.vcard v WHERE v.vca_id = ?;

#getAll
SELECT * FROM rdap.vcard ORDER BY 1 ASC;

#getByRegistrarId
SELECT vca.* FROM rdap.vcard vca JOIN rdap.registrar_contact rco ON rco.vca_id = vca.vca_id WHERE rco.rar_id = ?;

