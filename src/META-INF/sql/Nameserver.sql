#storeToDatabase
INSERT INTO rdap.nameserver VALUES(null,?,?,?,?);

#findByName
SELECT * FROM rdap.nameserver nse WHERE nse.nse_ldh_name=?;