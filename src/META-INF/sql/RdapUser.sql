#getMaxSearchResults
SELECT rus.rus_max_search_results FROM  rdap.rdap_user rus WHERE rus.rus_name=?;

#storeToDatabase
INSERT INTO rdap.rdap_user VALUES(null,?,?,?);

#getByName
SELECT * FROM rdap.rdap_user rus WHERE rus.rus_name=?;

#deleteAllRdapUserRoles
DELETE  FROM rdap.rdap_user_role;

#deleteAllRdapUsers
DELETE  FROM rdap.rdap_user;