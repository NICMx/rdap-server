#storeToDatabase
INSERT INTO rdap.ds_data VALUES (null, ?, ?, ?, ?, ?);

#getBySecureDns
SELECT dsd_id, dsd_keytag, dsd_algorithm, dsd_digest, dsd_digest_type FROM rdap.ds_data WHERE sdns_id=?;