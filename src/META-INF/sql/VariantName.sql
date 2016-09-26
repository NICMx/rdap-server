#storeToDatabase
INSERT INTO rdap.variant_name VALUES (?,?);

#getByVariantId
SELECT vna_ldh_name FROM rdap.variant_name WHERE variant_name.var_id=?;