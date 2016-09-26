#storeToDatabase
INSERT INTO rdap.event VALUES(null,?,?,?);

#getByNameServerId
SELECT eve.eve_id,eve.eac_id,eve.eve_actor,eve.eve_date FROM rdap.event eve JOIN rdap.nameserver_events nse ON nse.eve_id=eve.eve_id WHERE nse.nse_id=?;

#getByDsDataId
SELECT eve.eve_id,eve.eac_id,eve.eve_actor,FROM_UNIXTIME(eve.eve_date,'%m/%d/%Y %H:%i:%s') as eve_date FROM rdap.event eve JOIN rdap.ds_events dse ON dse.eve_id=eve.eve_id WHERE dse.dsd_id=?;

#getByDomainId
SELECT eve.eve_id,eve.eac_id,eve.eve_actor,FROM_UNIXTIME(eve.eve_date,'%m/%d/%Y %H:%i:%s') as eve_date FROM rdap.event eve JOIN rdap.domain_events dome ON dome.eve_id=eve.eve_id WHERE dome.dom_id=?;

#storeNameserverEventsToDatabase
INSERT INTO rdap.nameserver_events values (?,?);

#storeDomainEventsToDatabase
INSERT INTO rdap.domain_events VALUES (?,?);

#storeDsDataEventsToDatabase
INSERT INTO rdap.ds_events values (?,?);

