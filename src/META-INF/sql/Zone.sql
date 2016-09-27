#storeToDatabase
INSERT INTO rdap.zone VALUES(null,?);

#getByZoneId
SELECT * FROM rdap.zone WHERE zone_id=?;

#getAll
SELECT * FROM rdap.zone;
