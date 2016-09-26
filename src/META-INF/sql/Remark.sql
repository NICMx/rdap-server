#storeToDatabase
INSERT INTO rdap.remark VALUES (null,?,?,?);

#getByNameserverId
SELECT rem.* FROM rdap.remark rem JOIN rdap.nameserver_remarks nse ON nse.rem_id=rem.rem_id WHERE nse.nse_id=?;

#getByDomainId
SELECT rem.* FROM rdap.remark rem JOIN rdap.domain_remarks dom ON dom.rem_id=rem.rem_id WHERE dom.dom_id=?;

#storeNameserverRemarksToDatabase
INSERT INTO rdap.nameserver_remarks VALUES(?,?);

#storeDomainRemarksToDatabase
INSER INTO rdap.domain_remarks VALUES (?,?);

#getAll
SELECT * FROM rdap.remark ORDER BY 1 ASC;

