#storeToDatabase
INSERT * INTO rdap.zone VALUES(?,?);

#getByDomainId
SELECT zone_name FROM rdap.zone WHERE zone.zone_id=?;