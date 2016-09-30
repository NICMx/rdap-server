#storeToDatabase
INSERT INTO rdap.ds_data VALUES (null, ?, ?, ?, ?, ?);

#getBySecureDns
SELECT * FROM rdap.ds_data ds WHERE ds.sdns_id = ?;