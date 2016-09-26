#storeToDatabase
INSERT INTO rdap.variant VALUES (null,?,?);

#getByDomainId
SELECT var_id, var_idn_table FROM rdap.variant WHERE variant.dom_id=?;

#getAll
SELECT * FROM rdap.variant;