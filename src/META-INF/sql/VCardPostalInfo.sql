#storeToDatabase
INSERT INTO rdap.vcard_postal_info VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?);

#getById
SELECT * FROM rdap.vcard_postal_info vpi WHERE vpi.vpi_id = ?;

#getAll
SELECT * FROM rdap.vcard_postal_info ORDER BY 1 ASC;

#getByVCardId
SELECT * FROM rdap.vcard_postal_info vpi WHERE vpi.vca_id = ?;