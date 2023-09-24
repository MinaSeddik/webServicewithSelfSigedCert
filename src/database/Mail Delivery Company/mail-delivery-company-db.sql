
-- https://vertabelo.com/blog/a-mail-delivery-company-data-model/


DROP DATABASE IF EXISTS mail-delivery-company-db;

CREATE DATABASE IF NOT EXISTS mail-delivery-company-db;

USE mail-delivery-company-db;



CREATE TABLE country
(
country_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) UNIQUE NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE city
(
city_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
country_id INTEGER UNSIGNED NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE post_office
(
post_office_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) UNIQUE NOT NULL,
city_id INTEGER UNSIGNED NOT NULL,
address VARCHAR(255) NOT NULL,
address2 VARCHAR(255) DEFAULT NULL,
details TEXT,
contact_details TEXT,
contact_person VARCHAR(100),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


--the physical locations used by our service, from household mailboxes to mail drop-off points to the local and central post offices.
--this table will store post offices (again) as well as mailboxes and other drop-off points.
CREATE TABLE location
(
location_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
location_name VARCHAR(50) UNIQUE NOT NULL,
post_office_in_charge INTEGER UNSIGNED NOT NULL,
post_office_id INTEGER UNSIGNED NOT NULL COMMENT('The ID of the post office. This attribute will contain a value only when this location is also a post_office.'),
description TEXT,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


--service catalog
CREATE TABLE service
(
service_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
service_name VARCHAR(50) UNIQUE NOT NULL,
description TEXT,
active TINYINT(1) NOT NULL DEFAULT '1',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE location_service
(
location_id INTEGER UNSIGNED NOT NULL,
service_id INTEGER UNSIGNED NOT NULL,
details TEXT,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (location_id, service_id)
);

--The collection_catalog lists all the ways a sender could give us mail.
--This could be via personal mailbox, at a collection point, or by way of an employee in the office.
CREATE TABLE collecting_catalog
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
option_name VARCHAR(50) UNIQUE NOT NULL COMMENT('personal mailbox, collection point, office'),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


--The processing_catalog stores all the internal activities we perform on the mail.
CREATE TABLE processing_catalog
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
option_name VARCHAR(50) UNIQUE NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE collecting_option
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
location_id INTEGER UNSIGNED NOT NULL,
collecting_catalog_id INTEGER UNSIGNED NOT NULL,
detailes TEXT,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE processing_option
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
location_id INTEGER UNSIGNED NOT NULL,
processing_catalog_id INTEGER UNSIGNED NOT NULL,
detailes TEXT,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


--All possible types of relations that could be assigned to a pair of locations are stored in the connection_type dictionary. T
CREATE TABLE connection_type
(
connection_type_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
type_name VARCHAR(128) UNIQUE COMMENT('“daily delivery”, “checking weekly (every Monday)”, etc.'),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE location_connection
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
location_from INTEGER UNSIGNED NOT NULL,
location_to INTEGER UNSIGNED NOT NULL,
connection_type_id INTEGER UNSIGNED NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE mail_category
(
mail_category_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
category_name VARCHAR(255) UNIQUE NOT NULL COMMEENT('LETTER, PACKAGE, ..... '),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE mail
(
mail_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
mail_code VARCHAR(64) NOT NULL,
mail_category_id INTEGER UNSIGNED NOT NULL,
sender_address VARCHAR(255) NOT NULL COMMENT('The address of the person who sent that item. This address will be used to return the item if it can’t be delivered to the recipient.'),
recipient_address VARCHAR(255) NOT NULL,
location_start_id INTEGER UNSIGNED NOT NULL, -- FK
location_end_id INTEGER UNSIGNED NOT NULL, -- FK
time_inserted DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
time_delivered DATETIME DEFAULT NULL COMMENT('When this mail was delivered to the recipient.'),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE mail_carrier -- staff
(
mail_carrier_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
employee_code CHAR(12) NOT NULL UNIQUE,
first_name VARCHAR(40) NOT NULL,
last_name VARCHAR(40) NOT NULL,
active_from DATE NOT NULL,
active_to DATE DEFAULT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--Each mail carrier (staff employee) is usually assigned to one or more locations.
CREATE TABLE location_assigned
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
mail_carrier_id INTEGER UNSIGNED NOT NULL,
location_id INTEGER UNSIGNED NOT NULL,
start_date DATE NOT NULL,
end_date DATE DEFAULT NULL,
details TEXT,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



--We can expect that mail items will be grouped and then each group will be assigned to a mail carrier (staff).
--We need to know who handled each mail item and when, so we’ll use the assigned_to table.
CREATE TABLE assigned_to
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
mail_id INTEGER UNSIGNED NOT NULL,
mail_carrier_id INTEGER UNSIGNED NOT NULL,
time_assigned DATETIME NOT NULL COMMENT('When this item was assigned to the carrier.'),
time_completed DATE DEFAULT NULL COMMENT('When the carrier finished the assignment. This could be because the item was delivered or because it was assigned to another carrier.'),
notes TEXT,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE status_catalog
(
status_catalog_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
status_name VARCHAR(255) UNIQUE NOT NULL COMMENT '“new mail in the system” to “mail delivered” ',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE mail_status
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
mail_id INTEGER UNSIGNED NOT NULL,
status_catalog_id INTEGER UNSIGNED NOT NULL,
mail_carrier_id INTEGER UNSIGNED NOT NULL,
status_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT 'will record all events related with each mail item';




