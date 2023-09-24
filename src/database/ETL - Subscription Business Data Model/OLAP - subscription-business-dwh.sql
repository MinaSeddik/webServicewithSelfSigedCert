

--https://vertabelo.com/blog/creating-a-dwh-part-two-a-subscription-business-data-model/


DROP DATABASE IF EXISTS subscription_business_dwh;

CREATE DATABASE subscription_business_dwh;

USE subscription_business_dwh;

-------------------------------------------------------------------------------
-------------------------------------------------------------------------------
-------------------------------------------------------------------------------
--                          DIMENSSION TABLES
-------------------------------------------------------------------------------
-------------------------------------------------------------------------------
-------------------------------------------------------------------------------


--The idea is to store these values separately in order to avoid additional operations when we query the DWH
CREATE TABLE dim_time
(
dim_time_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
time_date DATE,
time_year TINYINT,
time_month TINYINT,
time_week TINYINT COMMENT('Week number of the year [1-52]'),
time_day_of_week TINYINT COMMENT('could be enum'),
ts TIMESTAMP NOT NULL COMMENT('This way, we could check if the ETL process was already
performed that day. If it had been, we could delete the previously inserted data for the same date and
insert everything from the start of the day. (Note: This type of column has no business meaning and is
commonly referred to as a "technical column" in database modeling.)')
);

CREATE TABLE dim_city
(
dim_city_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
city_name VARCHAR(50),
postal_code VARCHAR(20),
county_name VARCHAR(128),
ts TIMESTAMP NOT NULL,
CONSTRAINT dim_city_unqiue UNIQUE KEY(city_name, postal_code, county_name)
);


CREATE TABLE dim_delivery_status
(
dim_delivery_status_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
in_transit TINYINT(1),
cancelled TINYINT(1),
delivered TINYINT(1),
ts TIMESTAMP NOT NULL
);

CREATE TABLE dim_product
(
dim_product_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
product_name VARCHAR(50) NOT NULL UNIQUE,
ts TIMESTAMP NOT NULL
);

-------------------------------------------------------------------------------
-------------------------------------------------------------------------------
-------------------------------------------------------------------------------
--                          FACT TABLES
-------------------------------------------------------------------------------
-------------------------------------------------------------------------------
-------------------------------------------------------------------------------

CREATE TABLE fact_customer_subscriber
(
dim_city_id INT UNSIGNED NOT NULL,
dim_time_id INT UNSIGNED NOT NULL,
total_active INT UNSIGNED NOT NULL,
total_inactive INT UNSIGNED NOT NULL,
daily_new INT UNSIGNED NOT NULL,
daily_canceled INT UNSIGNED NOT NULL,
ts TIMESTAMP NOT NULL
);


CREATE TABLE fact_subscription_status
(
dim_city_id INT UNSIGNED NOT NULL,
dim_time_id INT UNSIGNED NOT NULL,
total_active INT UNSIGNED NOT NULL,
total_inactive INT UNSIGNED NOT NULL,
daily_new INT UNSIGNED NOT NULL,
daily_canceled INT UNSIGNED NOT NULL,
ts TIMESTAMP NOT NULL
);


CREATE TABLE fact_product_quantities
(
dim_product_id INT UNSIGNED NOT NULL,
dim_city_id INT UNSIGNED NOT NULL,
dim_time_id INT UNSIGNED NOT NULL,
ordered INT UNSIGNED NOT NULL COMMENT('Number of the ordered products'),
price DECIMAL(10, 2),
delivered INT UNSIGNED NOT NULL COMMENT('Number of the delivered products'),
ts TIMESTAMP NOT NULL,
CONSTRAINT dim_product_city_time_unqiue UNIQUE KEY (dim_product_id, dim_city_id, dim_time_id)
);