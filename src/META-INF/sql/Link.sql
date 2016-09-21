#storeToDatabase
INSERT INTO rdap.link VALUES(null,?,?,?,?,?,?,?);

#getByNameServerId
SELECT lin.* FROM rdap.link lin JOIN rdap.nameserver_links nse ON nse.lin_id=lin.lin_id WHERE nse.nse_id=?;

#getByEventId
SELECT lin.* FROM rdap.link lin JOIN rdap.event_links eve ON eve.lin_id=lin.lin_id WHERE eve.eve_id=?;

#storeNameserverLinksToDatabase
INSERT INTO rdap.nameserver_links VALUES(?,?);

#storeEventLinksToDatabase
INSERT INTO rdap.event_links VALUES(?,?);