#storeToDatabase
INSERT INTO rdap.remark VALUES (?,?,?,?);

#getByNameserverId
SELECT rem.* FROM rdap.remark rem JOIN rdap.nameserver_remarks nse ON nse.rem_id=rem.rem_id WHERE nse.nse_id=?;

#getAll
SELECT * FROM rdap.remark ORDER BY 1 ASC;