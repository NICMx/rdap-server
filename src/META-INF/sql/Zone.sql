#storeToDatabase
INSERT INTO rdap.zone VALUES(null,?);

#getByZoneId
SELECT * FROM rdap.zone WHERE zone_id=?;

#getByZoneName
SELECT * FROM  rdap.zone WHERE zone_name=?;
#getAll
SELECT * FROM rdap.zone;
