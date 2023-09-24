DROP DATABASE IF EXISTS my_pharma;

CREATE DATABASE my_pharma;

USE my_pharma;

CREATE TABLE product
(
product_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name
manufactor
unit_type ENUM(vial, ampule, stripe, powder, ..... )
unit_price     -- product_price table for better management
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE batch
(
batch_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
batch_code
expiration_date DATE


description
notes
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
