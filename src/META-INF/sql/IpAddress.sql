#getByNameserverId
SELECT iad.iad_id,iad.iad_type,inet_ntoa(iad_value) as iad_value FROM rdap.ip_address iad WHERE iad.nse_id=?;