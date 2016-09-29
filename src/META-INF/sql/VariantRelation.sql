#getByVariantId
SELECT rel_id FROM rdap.variant_relation WHERE var_id=?;

#storeVariantRelation
INSERT INTO rdap.variant_relation VALUES (?,?);