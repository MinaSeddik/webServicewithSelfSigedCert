

-- https://vertabelo.com/blog/automobile-repair-shop-data-model/uploads/services-and-offers.png


DROP DATABASE IF EXISTS auto_repair_shop;

CREATE DATABASE auto_repair_shop;

USE auto_repair_shop;

CREATE TABLE country
(
country_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE city
(
city_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
country_id INTEGER UNSIGNED NOT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE address
(
address_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
address VARCHAR(50) NOT NULL,
address2 VARCHAR(50) DEFAULT NULL,
city_id INTEGER UNSIGNED NOT NULL,
zip_code VRACHAR(10) DEFAULT NULL,
phone VARCHAR(20) NOT NULL,
location GEOMETRY NOT NULL SRID 4326,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE title
(
title_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VRACHAR(50) NOT NULL UNIQUE,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE staff
(
staff_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(45) NOT NULL,
last_name VARCHAR(45) NOT NULL,
address_id INTEGER UNSIGNED NOT NULL,
title_id INTEGER UNSIGNED NOT NULL,
picture BLOB,
email VARCHAR(50) NOT NULL,
store_id INTEGER UNSIGNED NOT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
username VARCHAR(20) NOT NULL,
password VARCHAR(40) DEFAULT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE store
(
store_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) DEFAULT NULL,
manager_staff_id INTEGER UNSIGNED NOT NULL,
address_id INTEGER UNSIGNED NOT NULL,
details TEXT,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE schedule
(
schedule_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
store_id INTEGER UNSIGNED NOT NULL,
staff_id INTEGER UNSIGNED NOT NULL,
shecdule_date DATE NOT NULL,
time_start TIME NOT NULL,
time_end TIME NOT NULL,
plan TINYINT(1) NOT NULL,
actual TINYINT(1) NOT NULL DEFAULT '0',
notes TEXT,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE customer
(
customer_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(45) NOT NULL,
last_name VARCHAR(45) NOT NULL,
address_id INTEGER UNSIGNED NOT NULL,
picture BLOB,
email VARCHAR(50) NOT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
username VARCHAR(20) NOT NULL,
password VARCHAR(40) DEFAULT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE contact_type -- lookup / dictionary
(
contact_type_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VRACHAR(50) NOT NULL UNIQUE,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE contact
(
contact_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
contact_type_id INTEGER UNSIGNED NOT NULL UNIQUE,
customer_id INTEGER UNSIGNED NOT NULL,
schedule_id INTEGER UNSIGNED NOT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- model vehicles
-- Using the example of a Nissan Altima, Nissan is the make, while Altima is the model.

CREATE TABLE vechile_type
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
type_name VARCHAR(50) NOT NULL UNIQUE,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE make
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
make_name VARCHAR(50) NOT NULL UNIQUE,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
model_name VARCHAR(50) NOT NULL UNIQUE,
make_id INTEGER UNSIGNED NOT NULL,
vechile_type_id INTEGER UNSIGNED NOT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE vehicle
(
vehicle_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
vin VARCHAR(255) NOT NULL COMMENT('A vehicle identification number, uniquely defining this vehicle, LIKE SERIAL_NUMBER'),
license_plate VARCHAR(64) NOT NULL,
customer_id INTEGER UNSIGNED NOT NULL,
model_id INTEGER UNSIGNED NOT NULL,
manufactured_year YEAR,
details TEXT,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE service
(
service_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
service_name VARCHAR(50) NOT NULL,
description TEXT,
active TINYINT(1) NOT NULL DEFAULT '1',
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE task
(
task_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
service_id INTEGER UNSIGNED NOT NULL,
task_name VARCHAR(50) NOT NULL,
price DECIMAL(10,2) NOT NULL,
ref_interval active TINYINT(1) NOT NULL DEFAULT '0' COMMENT('A flag denoting if we’ll measure interval for this task'),
ref_interval_min INTEGER UNSIGNED,
ref_interval_max INTEGER UNSIGNED,
description TEXT,
active TINYINT(1) NOT NULL DEFAULT '1',
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE offer
(
offer_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
customer_id INTEGER UNSIGNED NOT NULL,
contact_id INTEGER UNSIGNED DEFAULT NULL,
offer_description TEXT,
service_id INTEGER UNSIGNED NOT NULL,
service_discount DECIMAL(10,2) NOT NULL,
offer_price DECIMAL(10,2) NOT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE offer_task
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
offer_id INTEGER UNSIGNED NOT NULL,
task_id  INTEGER UNSIGNED NOT NULL,
task_price DECIMAL(10,2) NOT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE visit
(
visit_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
store_id INTEGER UNSIGNED NOT NULL,
customer_id INTEGER UNSIGNED NOT NULL,
vechile_id INTEGER UNSIGNED NOT NULL,
visit_start_planned DATETIME,
visit_end_planned DATETIME,
visit_start_actual DATETIME,
visit_end_actual DATETIME,
offer_id INTEGER UNSIGNED NOT NULL,
service_id INTEGER UNSIGNED NOT NULL,
service_discount DECIMAL(10,2) DEFUALT NULL,
visit_price DECIMAL(10,2) DEFUALT NULL,
invoice_created DATETIME,
invoice_due DATE,
invoice_charged DATETIME,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE visit_task
(
visit_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
task_id INTEGER UNSIGNED NOT NULL,
value_measured DECIMAL(10,2) DEFUALT NULL,
task_description TEXT,
task_price DECIMAL(10,2) DEFUALT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);












