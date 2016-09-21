#storeToDatabase
INSERT INTO rdap.public_id VALUES (null,?,?);

#getByDomain
SELECT public_id.* FROM public_id INNER JOIN domain_public_ids ON public_id.pid_id = domain_public_ids.pid_id WHERE  domain_public_ids.dom_id=?;

#getByEntity
SELECT public_id.* FROM public_id INNER JOIN entity_public_ids ON public_id.pid_id = entity_public_ids.pid_id WHERE  entity_public_ids.ent_id=?;

#getByRegistrar
SELECT public_id.* FROM public_id INNER JOIN registrar_public_ids ON public_id.pid_id = registrar_public_ids.pid_id WHERE  entity_public_ids.ent_id=?;