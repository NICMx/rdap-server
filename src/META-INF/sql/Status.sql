#getByNameServerId
SELECT sta_id FROM rdap.nameserver_status WHERE nse_id=?;

#storeNameserverStatusToDatabase
INSERT INTO rdap.nameserver_status VALUES (?,?);