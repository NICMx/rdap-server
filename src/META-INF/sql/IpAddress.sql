#getByNameserverId
SELECT iad.iad_id,iad.nse_id,iad.iad_type,inet_ntoa(iad_value) as iad_value FROM rdap.ip_address iad WHERE iad.nse_id=?;

#storeToDatabase
INSERT INTO rdap.ip_address VALUES (null,?,?,inet_aton(?));