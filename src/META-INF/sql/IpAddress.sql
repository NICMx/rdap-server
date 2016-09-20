#getByNameserverId
SELECT iad.iad_id,iad.nse_id,iad.iad_type, IF(iad.iad_type=4,INET_NTOA(iad.iad_value),INET6_NTOA(iad.iad_value)) as iad_value from rdap.ip_address iad WHERE iad.nse_id=?;

#storeToDatabase
INSERT INTO rdap.ip_address  VALUES (null,?,?,IF(?=4,INET_ATON(?),INET6_ATON(?)));
