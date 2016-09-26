#storeToDatabase
INSERT INTO rdap.remark VALUES (null,?,?,?);

#getByNameserverId
SELECT rem.* FROM rdap.remark rem JOIN rdap.nameserver_remarks nse ON nse.rem_id=rem.rem_id WHERE nse.nse_id=?;

#getByDomainId
SELECT rem.* FROM rdap.remark rem JOIN rdap.domain_remarks dom ON dom.rem_id=rem.rem_id WHERE dom.dom_id=?;

#getByRegistrarId
SELECT rem.* FROM rdap.remark rem JOIN rdap.registrar_remarks rar ON rar.rem_id=rem.rem_id WHERE rar.rar_id=?;

#getByEntityId
SELECT rem.* FROM rdap.remark rem JOIN rdap.entity_remarks ent ON ent.rem_id=rem.rem_id WHERE ent.ent_id=?;

#storeNameserverRemarksToDatabase
INSERT INTO rdap.nameserver_remarks VALUES(?,?);

#storeDomainRemarksToDatabase
INSERT INTO rdap.domain_remarks VALUES (?,?);

#storeEntityRemarksToDatabase
INSERT INTO rdap.entity_remarks VALUES (?, ?);

#storeRegistrarRemarksToDatabase
INSERT INTO rdap.registrar_remarks VALUES (?, ?);

#getAll
SELECT * FROM rdap.remark ORDER BY 1 ASC;

