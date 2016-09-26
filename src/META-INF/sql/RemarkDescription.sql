#storeToDatabase
INSERT INTO rdap.remark_description VALUES (?,?,?);

#getByRemarkId
SELECT * FROM rdap.remark_description rem_desc WHERE rem_desc.rem_id=? ORDER BY rem_desc.rde_order ASC;