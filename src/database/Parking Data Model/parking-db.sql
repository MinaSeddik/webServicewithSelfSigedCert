
-- https://vertabelo.com/blog/constructing-a-data-model-for-a-parking-lot-management-system/
-- https://vertabelo.com/blog/tap-and-park-a-parking-app-data-model/

DROP DATABASE IF EXISTS parking;

CREATE DATABASE parking;

USE parking;

CREATE TABLE parking_lot
(
parking_lot_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
number_of_blocks SMALLINT UNSIGNED NOT NULL,
is_slot_available TINYINT(1) NOT NULL DEFAULT '1',
address VARCHAR(255) NOT NULL,
zip_code CHAR(5) NOT NULL,
location GEOMETRY NOT NULL SRID 4326,
is_reentry_allowed TINYINT(1) NOT NULL DEFAULT '0' COMMENT('Signifies whether a customer may exit the parking lot and re-enter with the same parking slip. Note that many parking lots typically don’t allow customers to do this.'),
operating_company_name VRACHAR(100) NOT NULL,
is_valet_parking_available TINYINT(1) NOT NULL DEFAULT '0' COMMENT('Signifies whether the parking lot offers valet parking services.'),
operational_in_night TINYINT(1) NOT NULL DEFAULT '0',
minimum_hr_pay TINYINT COMMENT('The minimum fee to park your car in a lot. For example, some lots have a three-hour minimum, meaning that you pay for three hours even if you are only parked for 30 minutes.'),
is_monthly_pass_allowed TINYINT(1) NOT NULL DEFAULT '1',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE block
(
block_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
parking_lot_id INT UNSIGNED NOT NULL,
block_code CHAR(3) NOT NULL,
number_of_floors SMALLINT UNSIGNED NOT NULL COMMENT('The number “1” indicates that this is a ground-level block with no floors.'),
is_block_full TINYINT(1) NOT NULL DEFAULT '0',
active TINYINT(1) NOT NULL DEFAULT '1',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE floor
(
floor_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
block_id INT UNSIGNED NOT NULL,
floor_number CHAR(3) NOT NULL,
max_height_in_inch INT UNSIGNED NOT NULL,
number_of_wings INT UNSIGNED NOT NULL,
number_of_slots INT UNSIGNED NOT NULL,
is_covered TINYINT(1) NOT NULL COMMENT('identifies whether a floor is covered. The top floor of a multi-level parking lot or a ground-level parking lot will never be covered.'),
is_accessible TINYINT(1) NOT NULL COMMENT('indicates whether the floor is easily accessible, especially by the handicapped. If a multi-level lot has an operational elevator, each of its floors is considered to be accessible.'),
is_floor_full TINYINT(1) NOT NULL DEFAULT '0',
is_reserved_reg_cust TINYINT(1) NOT NULL COMMENT('indicates whether a floor is strictly reserved for regular customers.'),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE parking_slot
(
slot_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
floor_id INT UNSIGNED NOT NULL,
slot_number CHAR(5) NOT NULL UNIQUE COMMENT('stores the unique identifier of the slot on a particular floor.'),
wing_code CHAR(1) NOT NULL UNIQUE COMMENT('identifies the wing in which a slot is located.'),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE parking_pricing
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
parking_lot_id INT UNSIGNED NOT NULL,
day_of_week ENUM('SUNDAY', 'MONDAY', 'TUESDAY' ) NOT NULL,
morning_hr_cost DECIMAL(10, 2) NOT NULL COMMENT('morning (6:00 to 11:00 am)'),
midday_hr_cost DECIMAL(10, 2) NOT NULL COMMENT('midday (11:00 am to 5:00 pm)'),
evening_hr_cost DECIMAL(10, 2) NOT NULL COMMENT('evening (5:00 to 10:00 pm)'),
all_day_cost DECIMAL(10, 2) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE parking_pricing_exception
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
parking_lot_id INT UNSIGNED NOT NULL,
day_date DATE NOT NULL COMMENT(' table takes precedence over the `parking_pricing` table.'),
morning_hr_cost DECIMAL(10, 2) NOT NULL COMMENT('morning (6:00 to 11:00 am)'),
midday_hr_cost DECIMAL(10, 2) NOT NULL COMMENT('midday (11:00 am to 5:00 pm)'),
evening_hr_cost DECIMAL(10, 2) NOT NULL COMMENT('evening (5:00 to 10:00 pm)'),
all_day_cost DECIMAL(10, 2) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE offers
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
parking_lot_id INT UNSIGNED NOT NULL,
effective_from DATE NOT NULL,
valid_untill DATE NOT NULL,
discount_in_percentage DECIMAL(5, 2),
discount_in_amount DECIMAL(5, 2),
offer_code VARCHAR(10),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE customer
(
customer_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VRACHAR(40),
last_name  VRACHAR(40),
vechile_number VARCHAR(20) NOT NULL,
registration_date DATETIME NOT NULL,
is_regular TINYINT(1) NOT NULL DEFAULT '0' COMMENT('indicates whether a customer has regular pass. If the column stores a value of true, then there must exist a valid entry in the regular_pass table. Once a pass expires and the customer has not yet renewed it, the value in this column is updated to false.'),
contact_number VARCHAR(20) DEFAULT NULL,
billing address VARCHAR(100) DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


--stores information about regular passes that are issued to customers.
CREATE TABLE regular_pass -- monthly pass
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
customer_id INT UNSIGNED NOT NULL,
purchase_date DATETIME NOT NULL,
start_date DATE NOT NULL,
valid untill DATE NOT NULL,
duration_in_days INT UNSIGNED NOT NULL,
cost DECIMAL(10, 2) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE parking_slot_reservation
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
customer_id INT UNSIGNED,
arrival_timestamp DATETIME NOT NULL,
depature_timestamp DATETIME NOT NULL,
--duration_in_minutes INT UNSIGNED NOT NULL,
booking_date DATETIME NOT NULL,
offer_code VARCHAR(20),
parking_slot_id INT UNSIGNED NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)


CREATE TABLE parking_slip
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
parking_slot_reservation_id INT UNSIGNED NOT NULL,
actual_entry_time DATETIME,
actual_exit_time DATETIME,
basic_cost DECIMAL(10, 2) NOT NULL,
penalty DECIMAL(10, 2) NOT NULL DEFAULT 0,
total_cost DECIMAL(10, 2) NOT NULL,
is_paid TINYINT(1) NOT NULL DEFAULT '0',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE payment
(
payment_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
customer_id INT UNSIGNED NOT NULL,

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--One person can have multiple cars.
CREATE TABLE vechile
(
vechile_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
customer_id INT UNSIGNED NOT NULL,
vechile_number VARCHAR(20) NOT NULL,

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);





