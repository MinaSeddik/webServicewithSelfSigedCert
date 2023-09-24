DROP DATABASE IF EXISTS my_hairdressing_salon;

CREATE DATABASE IF NOT EXISTS my_hairdressing_salon;

USE my_hairdressing_salon;



CREATE TABLE country
(
country_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE state
(
state_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,   -- state or province (in case of Canada)
country_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_state_country FOREIGN KEY (country_id) REFERENCES country(country_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE city
(
city_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
state_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
CONSTRAINT fk_city_state FOREIGN KEY (state_id) REFERENCES state(state_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE address
(
address_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
address VARCHAR(50) NOT NULL,
address2 VARCHAR(50) DEFAULT NULL,
city_id INTEGER UNSIGNED NOT NULL,
postal_code VRACHAR(10) DEFAULT NULL,
phone VARCHAR(20) NOT NULL,
location GEOMETRY NOT NULL SRID 4326,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
CONSTRAINT fk_address_city FOREIGN KEY (city_id) REFERENCES city(city_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE user
(
user_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(45) NOT NULL,
last_name VARCHAR(45) NOT NULL,
address_id INTEGER UNSIGNED DEFAULT NULL,
picture BLOB,
email VARCHAR(50) NOT NULL,
username VARCHAR(20) NOT NULL,
password VARCHAR(40) DEFAULT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_staff_address FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE customer
(
customer_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
user_id INTEGER UNSIGNED DEFAULT NULL,
gender ENUM('M', 'F', 'O') NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE staff
(
staff_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
store_id INTEGER UNSIGNED NOT NULL,
hire_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
end_date DATE DEFAULT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_staff_store FOREIGN KEY (store_id) REFERENCES store(store_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE store
(
store_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
store_name VARCHAR(100) DEFAULT NULL,
store_code BINARY(16) NOT NULL UNIQUE,           -- uuid() binary format
manager_staff_id INTEGER UNSIGNED NOT NULL,
address_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
CONSTRAINT fk_store_staff FOREIGN KEY (manager_staff_id) REFERENCES staff(staff_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_store_address FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE schedule
(
shift_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
store_id INTEGER UNSIGNED NOT NULL,
staff_id INTEGER UNSIGNED NOT NULL,
shift_date DATE NOT NULL,
start_time TIME NOT NULL,
end_time TIME NOT NULL,
status ENUM('NEW', 'PUBLISHED') NOT NULL,
notes TEXT,
holiday TINYINT(1) NOT NULL DEFAULT '0',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT unique_index_reservation_date_and_time UNIQUE KEY (store_id, staff_id, shift_date, start_time),
CONSTRAINT date_chk CHECK (start_time < end_time),
CONSTRAINT fk_schedule_store FOREIGN KEY (store_id) REFERENCES store(store_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_schedule_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--                   SCHEDULE TRIGGERS - to avoid overlapping
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------

DELIMITER $$

CREATE TRIGGER schedule_shift_insert_overlap
    BEFORE INSERT
    ON schedule FOR EACH ROW
BEGIN

    DECLARE rowcount INT;

    SELECT COUNT(*) INTO rowcount
    FROM schedule
    WHERE store_id = NEW.store_id
      AND staff_id = NEW.staff_id
      AND shift_date = NEW.shift_date
      AND (NEW.start_time < end_time AND NEW.end_time > start_time);

    IF rowcount > 0 THEN
        signal sqlstate '45000' set message_text = 'Shift Interval Overlaps with Existing Scheduled Shift';
    END IF;

END $$

DELIMITER ;




DELIMITER $$

CREATE TRIGGER schedule_shift_update_overlap
    BEFORE UPDATE
    ON schedule FOR EACH ROW
BEGIN
    DECLARE rowcount INT;

    SELECT COUNT(*) INTO rowcount
    FROM schedule
    WHERE store_id = NEW.store_id
      AND staff_id = NEW.staff_id
      AND shift_date = NEW.shift_date
      AND (NEW.start_time < end_time AND NEW.end_time > start_time);

    IF rowcount > 0 THEN
        signal sqlstate '45000' set message_text = 'Shift Interval Overlaps with Existing Scheduled Shift';
    END IF;

END $$


DELIMITER ;


------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------

CREATE TABLE holidays
(
year YEAR NOT NULL,
holiday_date DATE NOT NULL,
name VARCHAR(100) NOT NULL,
state_id INTEGER UNSIGNED NOT NULL,                     -- in case of state-related / province-related holiday
country_id INTEGER UNSIGNED NOT NULL,                    -- in case of nation-wide holiday
active TINYINT(1) NOT NULL DEFAULT '1',
description VARCHAR(150) DEFAULT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (year, holiday_date),
CONSTRAINT fk_holidays_state FOREIGN KEY (state_id) REFERENCES state(state_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_holidays_country FOREIGN KEY (country_id) REFERENCES country(country_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE category
(
category_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
code VARCHAR(36) NOT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE service
(
service_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
service_name VARCHAR(50) NOT NULL,
service_code VARCHAR(36) NOT NULL,
category_id INT UNSIGNED NOT NULL,
duration_in_minutes INT SMALLINT NOT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_service_category FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE RESTRICT ON UPDATE CASCADE,
);


CREATE TABLE service_pricing
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
service_id INT UNSIGNED NOT NULL,
price DECIMAL(10, 2) NOT NULL,
effective_from DATE NOT NULL,
valid_until DATE NOT NULL DEFAULT '9999-12-31',   -- max date allowed for DATE
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT service_pricing_service FOREIGN KEY (service_id) REFERENCES service(service) ON DELETE RESTRICT ON UPDATE CASCADE,
);


CREATE VIEW vw_service_pricing
AS
SELECT id, service_id, price, effective_from, valid_until, created_at, updated_at
FROM reservation_pricing
WHERE id IN (
    SELECT MAX(id)
    FROM service_pricing
    WHERE now() BETWEEN effective_from AND valid_until and active = 1;
    GROUP BY availability_id
);



CREATE TABLE appointment
(
appointment_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
store_id INTEGER UNSIGNED NOT NULL,
staff_id INTEGER UNSIGNED NOT NULL,   -- assigned to
customer_id INTEGER UNSIGNED NOT NULL,
appointment_date DATE NOT NULL,
start_time TIME NOT NULL,
end_time TIME NOT NULL,
appointment_code BINARY(16) NOT NULL UNIQUE,  -- QR code represented as UUID 16-bytes binary
status ENUM('SCHEDULED', 'CHECKED_IN', 'DONE', 'CANCELED', 'MOVED') NOT NULL,
notes TEXT,
holiday TINYINT(1) NOT NULL DEFAULT '0',
created_by_staff_id INTEGER UNSIGNED DEFAULT NULL,    -- if the appointment is made by phone
last_updated_by_staff_id INTEGER UNSIGNED DEFAULT NULL,    -- if the appointment is made by phone
canceled_by_staff_id INTEGER UNSIGNED DEFAULT NULL,    -- if the appointment is made by phone
canceled_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT unique_index_appointment_date_and_time UNIQUE KEY (store_id, staff_id, appointment_date, start_time),
CONSTRAINT date_chk CHECK (start_time < end_time),
CONSTRAINT fk_appointment_store FOREIGN KEY (store_id) REFERENCES store(store_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_appointment_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_appointment_created_by_staff FOREIGN KEY (created_by_staff_id) REFERENCES staff(staff_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_appointment_last_updated_by_staff_id FOREIGN KEY (last_updated_by_staff_id) REFERENCES staff(staff_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_appointment_canceled_by_staff_id FOREIGN KEY (canceled_by_staff_id) REFERENCES staff(staff_id) ON DELETE RESTRICT ON UPDATE CASCADE
);



DELIMITER $$

CREATE TRIGGER appointment_slot_insert_overlap
    BEFORE INSERT
    ON appointment FOR EACH ROW
BEGIN

    DECLARE rowcount INT;

    SELECT COUNT(*) INTO rowcount
    FROM appointment
    WHERE appointment_date = NEW.appointment_date
      AND (NEW.start_time < end_time AND NEW.end_time > start_time);

    IF rowcount > 0 THEN
        signal sqlstate '45000' set message_text = "Appointment Interval Overlaps with Existing Appointment's Intervals";
    END IF;

END $$

DELIMITER ;




DELIMITER $$

CREATE TRIGGER appointment_slot_update_overlap
    BEFORE UPDATE
    ON appointment FOR EACH ROW
BEGIN
    DECLARE rowcount INT;

    SELECT COUNT(*) INTO rowcount
    FROM appointment
    WHERE appointment_date = NEW.appointment_date
      AND (NEW.start_time < end_time AND NEW.end_time > start_time);

    IF rowcount > 0 THEN
        signal sqlstate '45000' set message_text = "Appointment Interval Overlaps with Existing Appointment's Intervals";
    END IF;

END $$


DELIMITER ;


CREATE TABLE service_booked
(
appointment_id INT UNSIGNED NOT NULL,
service_id INT UNSIGNED NOT NULL,
price DECIMAL(10, 2) NOT NULL,
created_by_staff_id INTEGER UNSIGNED DEFAULT NULL,    -- if the appointment is made by phone
last_updated_by_staff_id INTEGER UNSIGNED DEFAULT NULL,    -- if the appointment is made by phone
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_appointment_service_appointment FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_appointment_service_service FOREIGN KEY (service_id) REFERENCES service(service_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE appointment_status_log
(
appointment_id INT UNSIGNED NOT NULL,
status ENUM('SCHEDULED', 'CHECKED_IN', 'DONE') NOT NULL,
effected_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (appointment_id, status),
CONSTRAINT fk_appointment_status_log_appointment FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


-- if he/she changed his/her mind and add/remove services during the appointment
CREATE TABLE service_provided
(
service_provided_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
appointment_id INT UNSIGNED NOT NULL,
service_id INT UNSIGNED NOT NULL,
price DECIMAL(10, 2) NOT NULL,
created_by_staff_id INTEGER UNSIGNED DEFAULT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_appointment_service_appointment FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_appointment_service_service FOREIGN KEY (service_id) REFERENCES service(service_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE invoice  -- or bill
(
invoice_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
invoice_number VARCHAR(40) NOT NULL UNIQUE,
service_provided_id INT UNSIGNED NOT NULL,
staff_id INT UNSIGNED NOT NULL,
customer_id INT UNSIGNED NOT NULL,
invoice_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
subtotal_price DECIMAL(10, 2) NOT NULL,
taxes DECIMAL(10, 2) NOT NULL,
total_price DECIMAL(10, 2) NOT NULL,
payment_id INT UNSIGNED NOT NULL,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_service_provided_id_invoice FOREIGN KEY (service_provided_id) REFERENCES service_provided(service_provided_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_service_provided_id_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_service_provided_id_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_service_provided_id_payment FOREIGN KEY (payment_id) REFERENCES payment(payment_id) ON DELETE RESTRICT ON UPDATE CASCADE,
);


CREATE TABLE payment
(
payment_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
payment_type ENUM('CASH', 'DEBIT_CREDIT') NOT NULL,
payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
payment_transaction_id  VARCHAR(60) DEFAULT NULL, -- payment portal transaction id, null if payment type is cash
payment_status ENUM('PASS', 'FAIL') NOT NULL,
failed_reason TEXT DEFAULT NULL
);


WITH RECURSIVE current_week AS (
    -- get Monday of the current week
    SELECT DATE_ADD(CURDATE(), INTERVAL - WEEKDAY(CURDATE()) DAY) AS date
    UNION ALL
    SELECT DATE_ADD(date, INTERVAL 1 DAY) AS date
    FROM current_week
    WHERE WEEKDAY(date) < 6
)
SELECT CONCAT(ELT(DAYOFWEEK(date), 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat') , ' ',
DATE_FORMAT(date, '%m/%d')) AS 'date'
FROM current_week;


--Input:
--- date of the Monday of the week to get the next week
WITH RECURSIVE next_week AS (
    -- get Monday of the current week
    SELECT DATE_ADD(DATE_ADD('2023-08-07', INTERVAL - WEEKDAY('2023-08-07') DAY), INTERVAL 7 DAY) AS date
    UNION ALL
    SELECT DATE_ADD(date, INTERVAL 1 DAY) AS date
    FROM next_week
    WHERE WEEKDAY(date) < 6
)
SELECT CONCAT(ELT(DAYOFWEEK(date), 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat') , ' ',
DATE_FORMAT(date, '%m/%d')) AS 'date'
FROM next_week;


--Input:
--- date of the Monday of the week to get the next week
WITH RECURSIVE previous_week AS (
    -- get Monday of the current week
    SELECT DATE_SUB(DATE_ADD('2023-08-07', INTERVAL - WEEKDAY('2023-08-07') DAY), INTERVAL 7 DAY) AS date
    UNION ALL
    SELECT DATE_ADD(date, INTERVAL 1 DAY) AS date
    FROM previous_week
    WHERE WEEKDAY(date) < 6
)
SELECT CONCAT(ELT(DAYOFWEEK(date), 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat') , ' ',
DATE_FORMAT(date, '%m/%d')) AS 'date'
FROM previous_week;


WITH RECURSIVE current_month AS (
    -- get Monday of the current week
    SELECT DATE_ADD(CURDATE(),INTERVAL -DAY(CURDATE())+1 DAY)  AS date
    UNION ALL
    SELECT DATE_ADD(date, INTERVAL 1 DAY) AS date
    FROM current_month
    WHERE date < LAST_DAY(CURDATE())
)
SELECT CONCAT(ELT(DAYOFWEEK(date), 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat') , ' ',
DATE_FORMAT(date, '%m/%d')) AS 'date'
FROM current_month;






