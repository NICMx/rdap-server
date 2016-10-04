#storeToDatabase
INSERT INTO rdap.link VALUES(null,?,?,?,?,?,?,?);

#getByNameServerId
SELECT lin.* FROM rdap.link lin JOIN rdap.nameserver_links nse ON nse.lin_id=lin.lin_id WHERE nse.nse_id=?;

#getByEventId
SELECT lin.* FROM rdap.link lin JOIN rdap.event_links eve ON eve.lin_id=lin.lin_id WHERE eve.eve_id=?;

#getByDsDataId
SELECT lin.* FROM rdap.link lin JOIN rdap.ds_links dsd ON dsd.lin_id=lin.lin_id WHERE dsd.dsd_id=?;

#getByDomainId
SELECT lin.* FROM rdap.link lin JOIN rdap.domain_links dom ON dom.lin_id=lin.lin_id WHERE dom.dom_id=?;

#getByEntityId
SELECT lin.* FROM rdap.link lin JOIN rdap.entity_links ent ON ent.lin_id=lin.lin_id WHERE ent.ent_id=?;

#storeNameserverLinksToDatabase
INSERT INTO rdap.nameserver_links VALUES(?,?);

#storeEventLinksToDatabase
INSERT INTO rdap.event_links VALUES(?,?);

#storeRemarkLinksToDatabase
INSERT INTO rdap.remark_links VALUES(?,?);

#getByRemarkId
SELECT lin.* FROM rdap.link lin JOIN rdap.remark_links rem ON rem.lin_id=lin.lin_id WHERE rem.rem_id=?;

#storeDsDataLinksToDatabase
INSERT INTO rdap.ds_links VALUES(?,?);

#storeDomainLinksToDatabase
INSERT INTO rdap.domain_links VALUES (?,?);

#storeEntityLinksToDatabase
INSERT INTO rdap.entity_links VALUES(?,?);