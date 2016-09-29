#storeToDatabase
INSERT INTO rdap.variant VALUES (null,?,?);

#getByDomainId
SELECT * FROM rdap.variant WHERE variant.dom_id=?;

#getAll
SELECT * FROM rdap.variant;