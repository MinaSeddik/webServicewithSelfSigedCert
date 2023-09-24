--Reference: https://stackoverflow.com/questions/29868565/time-slot-database-design

DROP DATABASE IF EXISTS my_badminton_courts;

CREATE DATABASE IF NOT EXISTS my_badminton_courts;

USE my_badminton_courts;

--to express club branch
CREATE TABLE club
(
club_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
club_code VARCHAR(30) NOT NULL UNIQUE,
club_name VARCHAR(30) NOT NULL UNIQUE,
location GEOMETRY NOT NULL SRID 4326,
description VARCHAR(100) DEFAULT NULL,
address VARCHAR(100) DEFAULT NULL,
contact_person VARCHAR(100) DEFAULT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO club (club_code, club_name, location, description, address, contact_person) VALUES
('CLUB-5684', 'My Club', ST_GeomFromText( 'POINT(32.70405577169982 -117.157665913614)', 4326),
'this is a description', '2358 Rue Decarie', 'John smith');


CREATE TABLE court
(
court_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
club_id INT UNSIGNED NOT NULL,
court_code VARCHAR(30) NOT NULL UNIQUE,
location VARCHAR(50) DEFAULT NULL,
description VARCHAR(100) DEFAULT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_court_club FOREIGN KEY (club_id) REFERENCES club(club_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO court (club_id, court_code, location, description) VALUES
(1, 'CLUB-5684-CRT-12', 'Third from the EAST', 'this is a description');

CREATE TABLE court_availability
(
availability_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
court_id INT UNSIGNED NOT NULL,
week_day ENUM('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN') NOT NULL,
from_time TIME NOT NULL,
to_time TIME NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_court_availability_court FOREIGN KEY (court_id) REFERENCES court(court_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO court_availability (court_id, week_day, from_time, to_time) VALUES
(1, 'SUN', '12:00:00', '21:00:00'),
(1, 'MON', '00:00:00', '23:00:00'),
(1, 'TUE', '07:00:00', '14:00:00'),
(1, 'TUE', '16:00:00', '23:00:00'),
(1, 'WED', '19:00:00', '00:00:00'),
(1, 'THU', '09:00:00', '17:00:00'),
(1, 'THU', '13:00:00', '15:00:00'),
(1, 'THU', '18:00:00', '22:00:00'),
(1, 'FRI', '18:00:00', '22:00:00'),
(1, 'SAT', '09:00:00', '14:00:00'),
(1, 'SAT', '15:00:00', '24:00:00');


CREATE TABLE reservation_pricing
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
availability_id INT UNSIGNED NOT NULL,
hourly_price DECIMAL(10, 2) NOT NULL,
starts_from DATETIME NOT NULL,
valid_untill DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59',   -- max date allowed for DATETIME
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_reservation_pricing_court_availability FOREIGN KEY (availability_id) REFERENCES court_availability(availability_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO reservation_pricing (availability_id, hourly_price, starts_from) VALUES
(1, 30.00, DATE_SUB(now(),INTERVAL 1 WEEK)),
(2, 20.00, DATE_SUB(now(),INTERVAL 1 WEEK)),
(3, 20.00, DATE_SUB(now(),INTERVAL 1 WEEK)),
(4, 25.00, DATE_SUB(now(),INTERVAL 1 WEEK)),
(5, 20.00, DATE_SUB(now(),INTERVAL 1 WEEK)),
(6, 15.00, DATE_SUB(now(),INTERVAL 1 WEEK)),
(7, 20.00, DATE_SUB(now(),INTERVAL 1 WEEK)),
(8, 25.00, DATE_SUB(now(),INTERVAL 1 WEEK)),
(8, 25.00, DATE_SUB(now(),INTERVAL 1 WEEK)),
(9, 25.00, DATE_SUB(now(),INTERVAL 1 WEEK)),
(10, 30.00, DATE_SUB(now(),INTERVAL 1 WEEK));


CREATE TABLE court_availability_duration
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
duration TINYINT UNSIGNED NOT NULL,
duration_unit ENUM('MINUTE', 'HOUR') NOT NULL,
starts_from DATETIME NOT NULL,
valid_untill DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO court_availability_duration (duration, duration_unit, starts_from) VALUES
(1, 'HOUR', DATE_SUB(now(),INTERVAL 1 WEEK));


CREATE TABLE reservation
(
reservation_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
court_id INT UNSIGNED NOT NULL,
reservation_date DATE NOT NULL,

reservation_code VARCHAR(36) NOT NULL UNIQUE,  -- QR code represented as UUID 36 ASCII char
reservation_code_bin BINARY(16) NOT NULL UNIQUE,  -- QR code represented as UUID 16-bytes binary

reserved_username VARCHAR(50) NOT NULL,
reserved_user_phone VARCHAR(50) NOT NULL,
reserved_user_email VARCHAR(50) DEFAULT NULL,
from_time TIME NOT NULL,
to_time TIME NOT NULL,
price DECIMAL(10, 2) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT unique_index_reservation_date_and_time UNIQUE KEY (reservation_date, from_time),
CONSTRAINT date_chk CHECK (from_time < to_time),
CONSTRAINT positive_price CHECK (price > 0),
# CONSTRAINT court_available CHECK ( is_court_available(court_id, reservation_date, from_time, to_time) = 1 ),  -- should be moved to a trigger
CONSTRAINT fk_reservation_court FOREIGN KEY (court_id) REFERENCES court(court_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE tax
(
tax_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
tax_name VARCHAR(100) NOT NULL COMMENT('TaxFree', 'Vat', 'DiscountedVat', 'Other'),
tax_rate_percentage DECIMAL(10, 2) NOT NULL COMMENT('0.0', '17.50', '15.00', '20.00'),
effective_from DATE NOT NULL,
valid_untill DATE DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE invoice
(
invoice_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
invoice_number VARCHAR(20) NOT NULL UNIQUE,
invoice_number2 VARCHAR(40) GENERATED ALWAYS AS CONCAT('INV-', invoice_id) STORED,
payment_id  -- FK to payment table
tax_id INT UNSIGNED NOT NULL,
sub-total DECIMAL(10, 2) NOT NULL COMMENT 'sub-total value',
taxes DECIMAL(10, 2) NOT NULL,
total DECIMAL(10, 2) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--                    VIEWS
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------

CREATE VIEW vw_reservation_pricing
AS
SELECT id, availability_id, hourly_price, starts_from, valid_untill, created_at, updated_at
FROM reservation_pricing
WHERE id IN (
    SELECT MAX(id)
    FROM reservation_pricing
    WHERE now() BETWEEN starts_from AND valid_untill
    GROUP BY availability_id
);

CREATE VIEW vw_court_reservation_duration
AS
SELECT id, duration, duration_unit, starts_from, valid_untill, created_at, updated_at
FROM court_availability_duration
WHERE id = (
    SELECT MAX(id)
    FROM court_availability_duration
    WHERE now() BETWEEN starts_from AND valid_untill
);


------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--                    QUERIES
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--implement https://www.youtube.com/shorts/d21zTK3vICE

1) write query to get status of all courts in specifc date
2) write query to get status of all courts between specific date range
3) write query to get status of secific court in specific date
4) write query to get status of secific court in specific date range
5) Make Reservation for a single interval
5) Make Reservation for a multiple intervals of the same price
5) Make Reservation for a multiple intervals of the different price




------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--                    FUNCTION AND PROCEDURES
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
DELIMITER $$

CREATE FUNCTION is_court_available(p_court_id INTEGER, p_date DATE, p_start_time TIME,p_end_time TIME) RETURNS BOOLEAN
    DETERMINISTIC
    READS SQL DATA
BEGIN

    DECLARE v_court_available BOOLEAN;

     # sanity check
     # 1) check valid reservation_date (the court is available and open)
     # 2) check valid reservation start time (the court is available and open)
     # 3) check valid reservation duration (the court is available and open till the end of the reservation)


    SELECT IF( EXISTS(
                    SELECT *
                    FROM court_availability
                    WHERE court_id = p_court_id
                    AND week_day = ELT(DAYOFWEEK(p_date), 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT')
                    AND p_start_time BETWEEN from_time AND to_time
                    AND p_end_time <= to_time ), 1, 0) INTO v_court_available;


      RETURN v_court_available;

END $$

DELIMITER ;



DELIMITER $$

CREATE PROCEDURE make_reservation(  IN p_club_code VARCHAR(30), IN p_court_code VARCHAR(30),
                                    IN p_date DATE, IN p_time TIME,
                                    IN p_reserved_username VARCHAR(50), IN p_reserved_user_phone VARCHAR(50),
                                    IN p_reserved_user_email VARCHAR(50))
    READS SQL DATA
    COMMENT 'Make a Reservation for a Single interval'
BEGIN

--    DECLARE v_duration INT;
--    DECLARE v_duration_unit CHAR(10);
--
--    SELECT duration, duration_unit INTO v_duration, v_duration_unit
--    FROM vw_court_reservation_duration;


    -- this procedure is error prone
    --1) check valid p_club_code   (should be done from the application, anyway sql will raise exception)
    --2) check valid p_court_code    (should be done from the application, anyway sql will raise exception)
    --3) check valid reservation_date (the court is available and open)
    --4) check valid reservation start time (the court is available and open)
    --5) check valid reservation duration (the court is available and open till the end of the reservation)
    --6) check there is no overlap between reservation intervals  (this should be handled in a trigger before insert & before update)


    INSERT INTO reservation (court_id, reservation_date, reserved_username, reserved_user_phone, reserved_user_email, from_time, to_time, price) VALUES
    (
         (SELECT court_id
         FROM court
         WHERE club_id = (SELECT club_id FROM club WHERE club_code = p_club_code)
         AND court_code = p_court_code),

        p_date,
        p_reserved_username, p_reserved_user_phone, p_reserved_user_email,
        p_time,
        (DATE_ADD(p_time, INTERVAL 1 HOUR)),

         (SELECT rp.hourly_price
         FROM court_availability ca
         INNER JOIN vw_reservation_pricing rp ON ca.availability_id = rp.availability_id
            AND ca.week_day = ELT(DAYOFWEEK(p_date), 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT')
            AND p_time BETWEEN from_time AND to_time)

     );

END $$

DELIMITER ;

------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--                    TRIGGERS
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------

DELIMITER $$

CREATE TRIGGER reservation_interval_insert_overlap
    BEFORE INSERT
    ON reservation FOR EACH ROW
BEGIN

    DECLARE rowcount INT;
    DECLARE is_available TINYINT(1);

    SELECT IF( EXISTS(
                    SELECT *
                    FROM court_availability ca
                    WHERE ca.court_id = NEW.court_id
                    AND ca.week_day = ELT(DAYOFWEEK(NEW.reservation_date), 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT')
                    AND NEW.from_time BETWEEN ca.from_time AND ca.to_time
                    AND NEW.to_time <= ca.to_time ), 1, 0) INTO is_available;

    IF is_available = 0 THEN
        signal sqlstate '45000' set message_text = 'Reservation Interval is Not available';
    END IF;


    SELECT COUNT(*) INTO rowcount
    FROM reservation
    WHERE reservation_date = NEW.reservation_date
      AND (NEW.from_time < to_time AND NEW.to_time > from_time);

    IF rowcount > 0 THEN
        signal sqlstate '45000' set message_text = "Reservation Interval Overlaps with Existing Reservation's Intervals";
    END IF;

END $$

DELIMITER ;




DELIMITER $$

CREATE TRIGGER reservation_interval_update_overlap
    BEFORE UPDATE
    ON reservation FOR EACH ROW
BEGIN
    DECLARE rowcount INT;
    DECLARE is_available TINYINT(1);

    SELECT IF( EXISTS(
                    SELECT *
                    FROM court_availability ca
                    WHERE ca.court_id = NEW.court_id
                    AND ca.week_day = ELT(DAYOFWEEK(NEW.reservation_date), 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT')
                    AND NEW.from_time BETWEEN ca.from_time AND ca.to_time
                    AND NEW.to_time <= ca.to_time ), 1, 0) INTO is_available;

    IF is_available = 0 THEN
        signal sqlstate '45000' set message_text = 'Reservation Interval is Not available';
    END IF;

    SELECT COUNT(*) INTO rowcount
    FROM reservation
    WHERE reservation_date = NEW.reservation_date
      AND (NEW.from_time < to_time AND NEW.to_time > from_time);

    IF rowcount > 0 THEN
        signal sqlstate '45000' set message_text = "Reservation Interval Overlaps with Existing Reservation's Intervals";
    END IF;

END $$


DELIMITER ;


------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--                    Make Reservation
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--Input:
--------
--1) club_code = 'CLUB-5684'   -- should be validated - valid club code
--2) court_code = 'CLUB-5684-CRT-12'  -- should be validated - valid court code
--3) reservation_date = '2023-08-08'
--4) reservation_name = 'John Smith'
--5) reservation_phone = '438-971-2548'
--6) reservation_email = 'john.smith@gamil.com'
--7) start_time = '11:00'
--8) duration_in_hours = 2  -- OR (end_time = '13:00')

     # sanity check
     # 1) check valid reservation_date (the court is available and open)
     # 2) check valid reservation start time (the court is available and open)
     # 3) check valid reservation duration (the court is available and open till the end of the reservation)

    SELECT IF( EXISTS(
                    SELECT *
                    FROM court_availability
                    WHERE court_id = (  SELECT court_id
                                         FROM court
                                         WHERE club_id = (SELECT club_id FROM club WHERE club_code = 'CLUB-5684')
                                         AND court_code = 'CLUB-5684-CRT-12')
                    AND week_day = ELT(DAYOFWEEK('2023-08-08'), 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT')
                    AND STR_TO_DATE('11:00', '%h:%i') BETWEEN from_time AND to_time
                    AND STR_TO_DATE('11:00', '%h:%i') <= to_time ), 1, 0) AS 'Is Avail.';

--    OR

    SELECT IF( EXISTS(
                    SELECT *
                    FROM court_availability
                    WHERE court_id = (  SELECT court_id
                                         FROM court
                                         WHERE club_id = (SELECT club_id FROM club WHERE club_code = 'CLUB-5684')
                                         AND court_code = 'CLUB-5684-CRT-12')
                    AND week_day = ELT(DAYOFWEEK('2023-08-07'), 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT')
                    AND STR_TO_DATE('11:00', '%h:%i') BETWEEN from_time AND to_time
                    AND DATE_ADD(STR_TO_DATE('11:00', '%h:%i'), INTERVAL 2 HOUR) <= to_time ), 1, 0) AS 'Is Avail.';





    INSERT INTO reservation (   court_id, reservation_code, reservation_code_bin, reservation_date,
                                reserved_username, reserved_user_phone, reserved_user_email,
                                from_time, to_time, price) VALUES
    (
         (SELECT court_id
         FROM court
         WHERE club_id = (SELECT club_id FROM club WHERE club_code = 'CLUB-5684')
         AND court_code = 'CLUB-5684-CRT-12'),

        uuid(),
        UUID_TO_BIN(uuid(), true),

        '2023-08-07',
        'John Smith', '438-971-2548', 'john.smith@gamil.com',
        STR_TO_DATE('11:00', '%h:%i'),
        (DATE_ADD(STR_TO_DATE('11:00', '%h:%i'), INTERVAL 2 HOUR)),

         (SELECT rp.hourly_price * 2
         FROM court_availability ca
         INNER JOIN vw_reservation_pricing rp ON ca.availability_id = rp.availability_id
            AND ca.week_day = ELT(DAYOFWEEK('2023-08-07'), 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT')
            AND STR_TO_DATE('11:00', '%h:%i') BETWEEN from_time AND to_time)

     );

------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--                    Cancel Reservation
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--Input:
--------
--1) reservation_code = '3ad44257-090e-478c-bde0-8d519e20a386'

DELETE FROM reservation
WHERE reservation_code = '3ad44257-090e-478c-bde0-8d519e20a386';

--OR

DELETE FROM reservation
WHERE BIN_TO_UUID(reservation_code, true) = '3ad44257-090e-478c-bde0-8d519e20a386';


------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--                    list available free slots
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--Input:
--------
1) club_code = 'CLUB-5684'   -- should be validated - valid club code
2) court_code = 'CLUB-5684-CRT-12'  -- should be validated - valid court code
3) target_date = '2023-08-06'
4) jump_interval_in_minutes = 30
5) duration_in_hours = 2


WITH RECURSIVE slots AS (
    SELECT CONCAT(ELT(DAYOFWEEK('2023-08-07'), 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'), ' ', '2023-08-07') as date,
     from_time AS start, DATE_ADD(from_time,INTERVAL 2 HOUR) AS end, to_time AS end_time
    FROM court_availability
    WHERE court_id = (  SELECT court_id
                         FROM court
                         WHERE club_id = (SELECT club_id FROM club WHERE club_code = 'CLUB-5684')
                         AND court_code = 'CLUB-5684-CRT-12')
    AND week_day = ELT(DAYOFWEEK('2023-08-07'), 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT')

    UNION ALL

    SELECT date,
    DATE_ADD(start,INTERVAL 15 MINUTE) AS start,
    DATE_ADD(DATE_ADD(start,INTERVAL 15 MINUTE),INTERVAL 2 HOUR) AS end, end_time
    FROM slots
    WHERE DATE_ADD(DATE_ADD(start,INTERVAL 15 MINUTE),INTERVAL 2 HOUR) <= end_time
)
SELECT s.*, IF(s.start < r.to_time AND s.end > r.from_time, 'Un-avaliable', 'Avaliable') AS 'Is Available'
FROM slots s
LEFT JOIN reservation r ON r.reservation_date = '2023-08-07'
ORDER BY start;


