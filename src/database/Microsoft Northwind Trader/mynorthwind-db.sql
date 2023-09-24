
/*
- Add order discount feature, coupons
    - https://vertabelo.com/blog/offers-deals-and-discounts-a-product-pricing-data-model/
- Add order shipment feature
- Add order taxes feature
- apply rating and stars
- gift card and wish list
- prime customer
- edit order and generate new invoice
-

*/


DROP DATABASE IF EXISTS northwind;

CREATE DATABASE IF NOT EXISTS northwind;

USE northwind;

CREATE TABLE country
(
country_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE city
(
city_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
country_id INTEGER UNSIGNED NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_city_country FOREIGN KEY (country_id) REFERENCES country(country_id) ON DELETE RESTRICT ON UPDATE CASCADE
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
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_address_city FOREIGN KEY (city_id) REFERENCES city(city_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE department
(
dept_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
dept_name VARCHAR(50) NOT NULL,
dept_code CHAR(5) NOT NULL UNIQUE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE employee
(
employee_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
employee_code CHAR(10) NOT NULL UNIQUE,
first_name VARCHAR(30) NOT NULL,
last_name VARCHAR(30) NOT NULL,
address_id INT UNSIGNED NOT NULL,
email VARCHAR(50) DEFAULT NULL,
dept_id INT UNSIGNED NOT NULL,
birth_date DATE NOT NULL,
hire_date DATE NOT NULL,
end_date DATE DEFAULT NULL,
employee_type ENUM('FULL-TIME','PART-TIME') NOT NULL,
gender ENUM('M','F') NOT NULL,
notes TEXT DEFAULT NULL,
photo BLOB DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_employee_address FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_employee_dept FOREIGN KEY (dept_id) REFERENCES department(dept_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE department_manager
(
dept_id INT UNSIGNED NOT NULL,
employee_id INT UNSIGNED NOT NULL,
start_date DATE NOT NULL,
end_date DATE NOT NULL,   -- max DATE 9999-01-01
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (dept_id, employee_id, start_date)
);


CREATE TABLE employee_title
(
employee_id INT UNSIGNED NOT NULL,
title VARCHAR(50) NOT NULL,
start_date DATE NOT NULL,
end_date DATE NOT NULL,   -- max DATE 9999-01-01
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (employee_id, start_date)
);

CREATE TABLE employee_salary
(
employee_id INT UNSIGNED NOT NULL,
salary DECIMAL(10, 2) NOT NULL,
start_date DATE NOT NULL,
end_date DATE NOT NULL,   -- max DATE 9999-01-01
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (employee_id, start_date)
);


CREATE TABLE customer -- user: in case of ecommerce web site / online store
(
customer_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(30) NOT NULL,
last_name VARCHAR(30) DEFAULT NULL,
address_id INT UNSIGNED DEFAULT NULL,
email VARCHAR(50) DEFAULT NULL,
usename VARCHAR(50) NOT NULL,
password VARCHAR(50) NOT NULL,
reward_points INT NOT NULL DEFAULT '0',
notes TEXT DEFAULT NULL,
photo BLOB DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_customer_address FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE customer_rewardpoint_log
(
customer_id INT UNSIGNED NOT NULL,
reward_points INT NOT NULL DEFAULT '0',
order_id INT UNSIGNED NOT NULL,
product_id INT UNSIGNED NOT NULL,
rewarded_on DATETIME NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_customer_rewardpoint_log_order FOREIGN KEY (order_id) REFERENCES order(order_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_customer_rewardpoint_log_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE supplier
(
supplier_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
company_name VARCHAR(50) NOT NULL,
address_id INT UNSIGNED DEFAULT NULL,
contact_person_first_name VARCHAR(30) NOT NULL,
contact_person_first_last VARCHAR(30) NOT NULL,
contact_person_title VARCHAR(30) NOT NULL,
contact_person_phone VARCHAR(20) NOT NULL,
contact_person_phone_ext VARCHAR(5) NOT NULL,
contact_person_email VARCHAR(50) NOT NULL,
contact_fax VARCHAR(50) NOT NULL,
webpage VARCHAR(255) DEFAULT NULL,
notes LONGTEXT DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_supplier_address FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE supplier_email
(
supplier_id INT UNSIGNED NOT NULL,
email VARCHAR(50) NOT NULL,
is_primary TINYINT(1) DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_supplier_email_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE category
(
category_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
description VARCHAR(100) DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE product
(
product_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
product_name VARCHAR(30) NOT NULL,
product_code VARCHAR(18) NOT NULL UNIQUE,   -- QR code or bar code, usually it's a UUID
sku CHAR(10) NOT NULL,
description VARCHAR(100) DEFAULT NULL,
available_quantity INT NOT NULL,

--product_price to keep history
--unit_price DECIMAL(10, 2) NOT NULL,

category_id INT UNSIGNED NOT NULL,  -- Assume that product belongs to just one category
supplier_id INT UNSIGNED NOT NULL,
quantity_per_package INT NOT NULL,
tax_id INT NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_product_tax FOREIGN KEY (tax_id) REFERENCES canada_tax(tax_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE product_price
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
product_id INT UNSIGNED NOT NULL,
retail_price DECIMAL(10, 2) NOT NULL,
start_date DATE NOT NULL,
valid_untill DATE NOT NULL,   -- max DATE 9999-01-01
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_product_price_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE RESTRICT ON UPDATE CASCADE,
PRIMARY KEY(product_id, start_date)
);


CREATE TABLE product_discount
(
product_id INT UNSIGNED NOT NULL,
discount DECIMAL(10, 2) NOT NULL,
discount_quantity INT NOT NULL DEFAULT '1',
discount_type ENUM('SINGLE_FLAT', 'SINGLE_PERCENTAGE', 'QUANTITY_FLAT', 'QUANTITY_PERCENTAGE')  NOT NULL,
start_date DATE NOT NULL,
valid_untill DATE NOT NULL,   -- max DATE 9999-01-01
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_product_discount_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE product_rewardpoints
(
product_id INT UNSIGNED NOT NULL,
reward_points INT NOT NULL default '0',
start_date DATE NOT NULL,
valid_untill DATE NOT NULL,   -- max DATE 9999-01-01
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_product_rewardpoints_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE store
(
store_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) DEFAULT NULL,
store_code VARCHAR(20) NOT NULL,
manager_staff_id INTEGER UNSIGNED NOT NULL,
address_id INTEGER UNSIGNED NOT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_store_staff FOREIGN KEY (manager_staff_id) REFERENCES staff(staff_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_store_address FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


create TABLE order_status
(
status_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
status     -- NEW, PROCESSED, COMPLETED, CANCELED   (those status for in-location orders), we may need another statuses for online shopping
description
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);


CREATE TABLE order
(
order_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
order_code VARCHAR(18) NOT NULL,   -- QR code or bar code, usually it's a UUID
order_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
store_id INT UNSIGNED DEFAULT NULL, -- NULL if online
employee_id INT UNSIGNED DEFAULT NULL,  -- may be ordered online, employee_id = NULL
customer_id INT UNSIGNED DEFAULT NULL,  -- may be the customer is not recorded i the db, customer_id = NULL
order_status_id INT UNSIGNED NOT NULL,
canceled_by INT UNSIGNED DEFAULT NULL, -- employee_id who canceled the order
canceled_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_order_employee FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_order_status FOREIGN KEY (order_status_id) REFERENCES order_status(order_status_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE order_details
(
order_id INT UNSIGNED NOT NULL,
product_id INT UNSIGNED NOT NULL,
quantity INT NOT NULL DEFAULT '1',
unit_price DECIMAL(10, 2) NOT NULL,
total_price DECIMAL(10, 2) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (order_id, product_id)
);


CREATE TABLE customer_rewardpoint_log
(
customer_id INT UNSIGNED NOT NULL,
reward_points INT NOT NULL DEFAULT '0',
order_id INT UNSIGNED NOT NULL,
product_id INT UNSIGNED NOT NULL,
rewarded_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_customer_rewardpoint_log_order FOREIGN KEY (order_id) REFERENCES order(order_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_customer_rewardpoint_log_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE canada_province
(
province_id TINYINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
province_name VARCHAR(20) NOT NULL,
province_code CHAR(2) NOT NULL,
desc VRACHAR(100) DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE canada_tax_province -- create corresponding view
(
province_id TINYINT UNSIGNED NOT NULL,
tax_name CHAR(3) NOT NULL,   -- PST, GST, GST  and HST
tax_desc VRACHAR(30) DEFAULT NULL,
tax_value DECIMAL(10,3) NOT NULL
from_date DATE NOT NULL,
valid_until DATE NOT NULL,  -- max value for current applied tax type
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (province_id, tax_code, from_date)
);

-- create function CALCULATE_TAX(province_code, product_code, tax_code)
CREATE TABLE canada_tax
(
tax_id TINYINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
tax_code CHAR(3) NOT NULL,  -- A, B, R, S, X
desc VRACHAR(30) DEFAULT NULL,
note VRACHAR(100) DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE payment(
payment_id
trnasction_id -- from payment portal
placed_at
status
last_4_disgits_of_the_card
);

CREATE TABLE invoice        -- we can call it receipt
(
invoice_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
order_id INT UNSIGNED NOT NULL,

-- CONCAT( 'INV-', LPAD(id,7,'0') )
invoice_number

invoice_date
subtotal
tax_amount
total

payment_id DEFAULT NULL FK,  -- point to payment portal transaction_id
-- let's see
payment_method ENUM('CASH', 'CREDIT_CARD' , 'DEBIT_CARD', 'REWARD_POINTS', 'BUY_NOW_PAY_LATER',
'CHECK', 'MOBILE_WALLET', 'BANK_TRANSFER', 'CREPTO_CURRENCT') DEFAULT NULL,
);


CREATE TABLE product_review
(
review_id   PK
product_id  FK
rating TINYINT NOT NULL,
title VARCHAR(100) NOT NULL,
content TEXT NOT NULL,
helpfull INT UNSIGNED NOT NULL DEFAULT '0',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE cart
(
cart_id
product_id
quantity

);

CREATE TABLE cart
(
cart_id
customer_id  (user_id)

sessio_id (token)  -- based on security setup and user authentication
status -- New, Checkout ?? , Paid, Complete, and Abandoned.

);

CREATE TABLE wishlist
(
wishlist_id INT  PK
wishlist_name
wishlist_code
customer_id  (or user_id or owner_id)  FK

created_date DATETIME
privacy_mode ENUM ('PRIVATE', 'SHARED', 'PUBLIC')  DEFAULT 'PRIVATE'
shared_mode ENUM ('VIEW_ONLY', 'VIEW_EDIT')  DEFAULT 'VIEW_ONLY'
description
shipping_address FK
is_default TINYINT(1)

);

CREATE TABLE wishlist_product
(
wishlist_product FK
wishlist_id INT  PK
product_id FK
ordering  INT UNSIGNED NOT NULL DEFAULT '1',
priority ENUM(L, H, C, ....)
addred_date DATETIME
);


CREATE TABLE wishlist_member
(
wishlist_id INT  PK
friend_id   FK customer_id or user_id
shared_at DATETIME

PK (wishlist_id, friend_id)
);


CREATE TABLE wishlist_shared
(
customer_id   FK  or user_id
wishlist_id INT  FK
shared_at DATETIME

PK (wishlist_id, friend_id)
);













