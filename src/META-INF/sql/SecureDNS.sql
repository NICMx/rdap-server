#storeToDatabase
INSERT INTO rdap.secure_dns VALUES (null, ? ,?, ?, ?);

#getByDomain
SELECT sdns_id, sdns_zone_signed, sdns_delegation_signed,sdns_max_sig_life FROM rdap.secure_dns WHERE dom_id=?;