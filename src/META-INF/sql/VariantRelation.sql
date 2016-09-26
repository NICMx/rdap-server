#getByVariantId
SELECT rel_id FROM variant_relation WHERE var_id=?;

#storeVariantRelation
INSERT INTO variant_rellation VALUES (?,?);