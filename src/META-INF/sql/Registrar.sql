#storeToDatabase
INSERT INTO rdap.registrar(rar_id, rar_handle, rar_port43) VALUES (null, ?, ?);

#getById
SELECT * FROM rdap.registrar r WHERE r.rar_id = ?;

#getByHandle
SELECT * FROM rdap.registrar r WHERE r.rar_handle = ?;

#getSimpleRegistrarById
SELECT r.rar_id, r.rar_handle, r.rar_port43 FROM rdap.registrar r WHERE r.rar_id = ?;

#getSimpleRegistrarByHandle
SELECT r.rar_id, r.rar_handle, r.rar_port43 FROM rdap.registrar r WHERE r.rar_handle = ?;

#getRegistarIdByHandle
SELECT r.rar_id FROM rdap.registrar r WHERE r.rar_handle = ?;

#getAll
SELECT * FROM rdap.registrar ORDER BY 1 ASC;