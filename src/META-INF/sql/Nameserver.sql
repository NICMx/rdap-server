#storeToDatabase
INSERT INTO rdap.nameserver VALUES(null,?,?,?,?);

#findByName
SELECT * FROM rdap.nameserver nse WHERE nse.nse_ldh_name=?;

#existByName
SELECT 1 FROM rdap.nameserver nse WHERE nse.nse_ldh_name=?;