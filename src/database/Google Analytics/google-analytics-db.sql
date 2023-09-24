
--https://vertabelo.com/blog/website-analytics-a-google-analytics-data-model/

DROP DATABASE IF EXISTS google-analytics-db;

CREATE DATABASE IF NOT EXISTS google-analytics-db;

USE google-analytics-db;


CREATE TABLE country
(
country_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
code CHAR(10) NOT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE city
(
city_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
postal_code VARCHAR(10) NOT NULL,
country_id INTEGER UNSIGNED NOT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_city_country FOREIGN KEY (country_id) REFERENCES country(country_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE table language
(
language_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VRACHAR(50) NOT NULL,
code VRACHAR(3) DEFAULT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE table service_provider
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
sp_name VRACHAR(50) NOT NULL UNIQUE
);


CREATE table device_type
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
device_type_name VRACHAR(255) NOT NULL UNIQUE COMMENT '“desktop”, “tablet” and “mobile”'
);


CREATE table operating_system
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
os_name VRACHAR(255) NOT NULL UNIQUE COMMENT '“Linux”, “Windows”, “Macintosh”, “Android”, “iOS”, and “Chrome OS”'
);


CREATE table browser
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
browser_name VRACHAR(255) NOT NULL UNIQUE COMMENT '"Internet Explorer”, “Edge”, “Chrome”, “Firefox”, “Opera”, “Safari” and “Yandex”'
);

-- Once more (no we don’t open the door ☺ ), these relations to the sessions table are NOT mandatory! We’ll store whatever data we have about the session, and values we don’t have shall contain NULL.


CREATE table acquisision
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
acquisision_name VRACHAR(255) NOT NULL UNIQUE COMMENT ' “direct”, “google”, “facebook”, etc.'
) COMMENT 'Values stored in this table are used to denote the path which led a visitor to our page.';

CREATE table gender
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
gender_name VRACHAR(50) NOT NULL UNIQUE COMMENT ''
);


CREATE table age
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
age_range VRACHAR(32) NOT NULL UNIQUE COMMENT 'ex: "18-24" or “65+” ',
lower_boundry INT UNSIGNED NOT NULL,
upper_boundry INT UNSIGNED
);


CREATE table page
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
page_url VRACHAR(50) NOT NULL UNIQUE
) COMMENT 'Here we’ll store identifying information for each page we want to analyze.';

-- fact table
CREATE table session
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
jsession_id TEXT,
session_start_time TIMESTAMP,
session_end_time TIMESTAMP,
entrance_page_id INT UNSIGNED NOT NULL,
exit_page_id INT UNSIGNED NOT NULL,
is_first_visit TINYINT(1),

-- the following column are the fk to the dimension
entrance_page_id INT UNSIGNED NOT NULL,
exit_page_id INT UNSIGNED NOT NULL,
city_id INT UNSIGNED NOT NULL,
language_id INT UNSIGNED NOT NULL,
service_provider_id INT UNSIGNED NOT NULL,
device_type_id INT UNSIGNED NOT NULL,
operating_system_id INT UNSIGNED NOT NULL,
browser_id INT UNSIGNED NOT NULL,
acquisision_id INT UNSIGNED NOT NULL,
age_id INT UNSIGNED NOT NULL,
gender_id INT UNSIGNED NOT NULL,
is_first_visit TINYINT(1)
);


CREATE table session_page
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
session_id INT UNSIGNED NOT NULL,
page_id INT UNSIGNED NOT NULL COMMENT 'the page opened by means of a particular click.',
page_order INT UNSIGNED NOT NULL COMMENT 'order in which our website pages were opened. For each session, we’ll start with the value 1 for the 1st page opened and increment for each new click. This information allows us to track a customer’s progress through our site from one subpage to another.',
start_time TIMESTAMP,
end_time TIMESTAMP
);



