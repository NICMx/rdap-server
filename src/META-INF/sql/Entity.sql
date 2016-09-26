#storeToDatabase
INSERT INTO rdap.entity VALUES (null, ?, ?, ?, ?);

#getById
SELECT * FROM rdap.entity e WHERE e.ent_id = ?;

#getByHandle
SELECT * FROM rdap.entity e WHERE e.ent_handle = ?;

#getAll
SELECT * FROM rdap.entity ORDER BY 1 ASC;