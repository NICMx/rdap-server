---
title: Changes in database and Provider Implementation
breadcrums: ["Documentation", "documentation.html"]
---

# {{ page.title }}

## Index

1. [Introduction](#introduction)
1. [Database schema changelog](#database-schema-changelog)
1. [Data-access-api changelog](#data-access-api-changelog)
1. [Database schema migration queries](#database-schema-migration-queries)


## Introduction

As of today June 28th, 2019, the last version of the database is 1.4.1 and the data-access-api version is 1.2.0, the following tables shows the version relation that support each component.

| rdap-sql-provider | rdap-data-access-api |
| ---:| ---------------------:|
|1.0.0 | 1.0.0|
|1.1.0 | 1.1.0|
|1.2.0 | 1.1.1|
|1.3.0 | 1.2.0|
|1.4.0 | 1.2.0|
|1.4.1 | 1.2.0|

<br>


|rdap-server | rdap-data-access-api|
|---:|---:|
|1.1.0 | 1.1.0|
|1.1.1 | 1.1.0|
|1.1.2 | 1.1.0|
|1.2.0 | 1.1.1|
|1.2.1 | 1.1.1|
|1.2.2 | 1.1.1|
|1.3.0 | 1.2.0|
|1.4.0 | 1.2.0|
|1.4.1 | 1.2.0|


## Database schema changelog

### v1.1.0 to v1.2.0

 - rdap.configuration table is deleted
 - rdap_user and rdap_user_role tables are modified
 - variant_name table is altered to add a unicode column
 - rdap_access_role, entity_role and link_lang tables are added

### v1.2.0 to v1.3.0

- New catalogs in status table are added
- domain and nameserver tables are altered to change the length of ldh_name column.

### v1.3.0 to v1.4.0

- vcard_contact_uri table is added
- domain and nameserver tables are altered to remove ldh_name column
- domain and nameserver tables are altered to change the collate of unicode_name column to 'utf8_bin'
- vcard_postal_info table is altered to add column vpi_country_code after vpi_country

### v1.4.0 to v1.4.1

- variant_name table is altered to remove ldh_name column. Also, unicode_name column collate change to 'utf8_bin'
- more default values are added to country_code table.

## Data-access-api changelog

### 1.0.0 to 1.1.0

- A great refactor in the API to obtain the DAOs

### 1.1.0 to 1.1.1


- Several BugFixes
- User objects are added to support user access

### 1.1.1 to 1.2.0

- Nameserver DAO is modified to implement [rdap-nameserver-sharing-name draft](https://tools.ietf.org/html/draft-lozano-rdap-nameservers-sharing-name-03)

## Database schema migration queries

Here we show some migration queries to ease your migration. Before you start to apply the alter queries, please read the next consideration:

- In domain, nameserver and variant_name tables, if your unicode_name column is not populated, you need to populate it first.
- If all your domains and nameservers are not using IDN, just copy the ldh_name column to unicode_name column, or better, just rename it.
- If you have some IDNs domain and nameserver names in your database, populate the unicode_name column using u-label format.
- Rdap-sql-provicer v1.4.1 only use the unicode_name column, the ldh_name column is not used anymore.
 
If you meet the previous consideration, then just pick the migration queries fall-through from your current version of database schema.


V1.1.0

```
ALTER TABLE `rdap`.`rdap_user_role` 
DROP FOREIGN KEY `rdap_user_ir_fk`;

ALTER TABLE `rdap`.`rdap_user` 
DROP COLUMN `rus_id`,
CHANGE COLUMN `rus_name` `rus_name` VARCHAR(100) NOT NULL ,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`rus_name`);

ALTER TABLE `rdap`.`rdap_user_role` 
CHANGE COLUMN `rus_name` `rus_name` VARCHAR(100) NOT NULL ;

ALTER TABLE `rdap`.`rdap_user_role` 
ADD CONSTRAINT `rdap_user_ir_fk`
  FOREIGN KEY (`rus_name`)
  REFERENCES `rdap`.`rdap_user` (`rus_name`);

-- -----------------------------------------------------
-- Table `rdap`.`rdap_access_role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rdap`.`rdap_access_role` ;
CREATE TABLE IF NOT EXISTS `rdap`.`rdap_access_role` (
  `rar_name` VARCHAR(45) NOT NULL COMMENT 'Access role\'s name',
  `rar_description` VARCHAR(250) NOT NULL COMMENT 'Access role\'s description',
  PRIMARY KEY (`rar_name`))
ENGINE = InnoDB
COMMENT = 'This table contains a catalog of the access roles that a user could have.';

-- -----------------------------------------------------
-- Table `rdap`.`entity_role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rdap`.`entity_role` ;
CREATE TABLE IF NOT EXISTS `rdap`.`entity_role` (
  `ent_id` BIGINT NOT NULL COMMENT 'Entity\'s id',
  `rol_id` TINYINT NOT NULL COMMENT 'Role\'s id',
  PRIMARY KEY (`ent_id`, `rol_id`),
  INDEX `fk_entity_role_roles1_idx` (`rol_id` ASC),
  UNIQUE INDEX `unique_ent_rol` (`ent_id` ASC, `rol_id` ASC),
  CONSTRAINT `fk_entity_role_entity1`
    FOREIGN KEY (`ent_id`)
    REFERENCES `rdap`.`entity` (`ent_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_entity_role_roles1`
    FOREIGN KEY (`rol_id`)
    REFERENCES `rdap`.`roles` (`rol_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'This table contains the relation between an Entity and its role.';

-- -----------------------------------------------------
-- Table `rdap`.`link_lang`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rdap`.`link_lang` ;
CREATE TABLE IF NOT EXISTS `rdap`.`link_lang` (
  `lin_id` BIGINT NOT NULL COMMENT 'Link\'s id',
  `lan_hreflang` VARCHAR(45) NOT NULL COMMENT 'Language',
  PRIMARY KEY (`lin_id`, `lan_hreflang`),
  CONSTRAINT `fk_link_hreflang_link1`
    FOREIGN KEY (`lin_id`)
    REFERENCES `rdap`.`link` (`lin_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'This table contains the languages related to a link.';

ALTER TABLE `rdap`.`variant_name` 
ADD COLUMN `vna_unicode_name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL AFTER `var_id`;
```

V1.2.0

```
INSERT INTO rdap.status VALUES(19, 'add period');
INSERT INTO rdap.status VALUES(20, 'auto renew period');
INSERT INTO rdap.status VALUES(21, 'client delete prohibited');
INSERT INTO rdap.status VALUES(22, 'client hold');
INSERT INTO rdap.status VALUES(23, 'client renew prohibited');
INSERT INTO rdap.status VALUES(24, 'client transfer prohibited');
INSERT INTO rdap.status VALUES(25, 'client update prohibited');
INSERT INTO rdap.status VALUES(26, 'pending restore');
INSERT INTO rdap.status VALUES(27, 'redemption period');
INSERT INTO rdap.status VALUES(28, 'renew period');
INSERT INTO rdap.status VALUES(29, 'server delete prohibited');
INSERT INTO rdap.status VALUES(30, 'server renew prohibited');
INSERT INTO rdap.status VALUES(31, 'server transfer prohibited');
INSERT INTO rdap.status VALUES(32, 'server update prohibited');
INSERT INTO rdap.status VALUES(33, 'server hold');
INSERT INTO rdap.status VALUES(34, 'transfer period');
```

V1.3.0

```
-- -----------------------------------------------------
-- Table `rdap`.`vcard_contact_uri`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rdap`.`vcard_contact_uri` ;
CREATE TABLE IF NOT EXISTS `rdap`.`vcard_contact_uri` (
  `vcu_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Contact URI\'s id',
  `vca_id` BIGINT NOT NULL COMMENT 'Vcard\'s id',
  `vcu_order` INT UNSIGNED NOT NULL COMMENT 'Order of contact',
  `vcu_uri` VARCHAR(255) NOT NULL COMMENT 'Contact URI',
  PRIMARY KEY (`vcu_id`, `vca_id`),
  INDEX `fk_vcard_contact_uri_vcard1_idx` (`vca_id` ASC),
  CONSTRAINT `fk_vcard_contact_uri_1`
    FOREIGN KEY (`vca_id`)
    REFERENCES `rdap`.`vcard` (`vca_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'This table contains the VCard Contact URI information.';

ALTER TABLE `rdap`.`nameserver` 
CHANGE COLUMN `nse_unicode_name` `nse_unicode_name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;

ALTER TABLE `rdap`.`domain` 
CHANGE COLUMN `dom_unicode_name` `dom_unicode_name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;

ALTER TABLE `rdap`.`vcard_postal_info` 
ADD COLUMN `vpi_country_code` VARCHAR(2) NULL AFTER `vpi_country`;
```

V1.4.0

```
ALTER TABLE `rdap`.`variant_name` 
CHANGE COLUMN `vna_unicode_name` `vna_unicode_name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL;

INSERT INTO rdap.country_code VALUES (531,'CW');
INSERT INTO rdap.country_code VALUES (534,'SX');
INSERT INTO rdap.country_code VALUES (535,'BQ');
```


