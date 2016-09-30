#storeToDatabase
INSERT INTO rdap.variant VALUES (null,?,?);

#getByDomainId
SELECT * FROM rdap.variant v WHERE v.dom_id=?;

#getById
SELECT * FROM rdap.variant v WHERE v.var_id = ?;

#getAll
SELECT * FROM rdap.variant;

#storeVariantRelation
INSERT INTO rdap.variant_relation VALUES (?,?);

#getVariantRelationsByVariantId
SELECT rel_id FROM rdap.variant_relation vr WHERE vr.var_id=?;

#storeVariantNames
INSERT INTO rdap.variant_name VALUES (?,?);

#getVariantNamesByVariantId
SELECT vna_ldh_name FROM rdap.variant_name vn WHERE vn.var_id=?;