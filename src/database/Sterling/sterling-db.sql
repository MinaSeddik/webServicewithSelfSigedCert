DROP DATABASE IF EXISTS my_sterling;

CREATE DATABASE IF NOT EXISTS my_sterling;

USE my_sterling;

-- Non-Normalized table
CREATE TABLE store
(
store_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
store_code VARCHAR(50) NOT NULL,
store_name VARCHAR(50) NOT NULL,
address1 VARCHAR(30) NOT NULL,
address2 VARCHAR(30) DEFAULT NULL,
city VARCHAR(50) NOT NULL,    -- could be city_id points to another table for Normalization
state VARCHAR(50) NOT NULL,
country VARCHAR(50) NOT NULL,
postal_code VARCHAR(10) NOT NULL,
phone VARCHAR(20) NOT NULL,
email VARCHAR(50) NOT NULL,
location POINT NOT NULL SRID 4326,
active TINYINT(1) NOT NULL DEFAULT '1',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE hours_of_operation
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
store_id INT UNSIGNED NOT NULL,
week_day ENUM('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN') DEFAULT NULL,
open_from TIME NOT NULL,
open_to TIME NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_hours_of_operation_store FOREIGN KEY (store_id) REFERENCES store(store_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


INSERT INTO store(store_code, store_name, address1, address2, city, state,
country, postal_code, phone, email, location) values
(uuid(), 'My Store', 'address-1', 'address-2', 'San Diego', 'California', 'USA', '12345',
'4852634874', 'mysore@gamil.com',
ST_GeomFromText( 'POINT(32.70405577169982 -117.157665913614)', 4326));


INSERT INTO hours_of_operation(store_id, week_day, open_from, open_to) values
(1, 'MON', '00:00:00', '23:59:59'),
(1, 'TUE', '07:30:00', '14:30:00'),
(1, 'TUE', '16:15:00', '23:00:00'),
(1, 'WED', '19:00:00', '00:30:00'),
(1, 'THU', '09:00:00', '18:00:00');

--To query IsOpen?, just:

SELECT CASE WHEN @desiredtime BETWEEN open_from AND open_to THEN 1 ELSE 0 END
FROM hours_of_operation
WHERE store = @store;

--example:
SELECT CASE WHEN CURRENT_TIME() BETWEEN open_from AND open_to THEN 1 ELSE 0 END AS 'Open Now'
FROM hours_of_operation
WHERE store_id = 1 AND week_day = 'TUE' AND CURRENT_TIME() BETWEEN open_from AND open_to;


SELECT IF( EXISTS(
    SELECT *
    FROM hours_of_operation
    WHERE store_id = 1 AND week_day = 'TUE' AND CURRENT_TIME() BETWEEN open_from AND open_to)
, 1, 0) As 'Open Now';

SELECT IF( EXISTS(
    SELECT *
    FROM hours_of_operation
    WHERE store_id = 1
    AND ELT(DAYOFWEEK('2023-08-08'), 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat') = week_day
    AND '16:30' BETWEEN open_from AND open_to)
, 1, 0) AS Available;



-- query to display Hours of operation
WITH days_of_week AS (
    SELECT 'MON' as day, 'Monday' as day_name, 1 as serial
    UNION
    SELECT 'TUE' as day, 'Tuesday' as day_name, 2 as serial
    UNION
    SELECT 'WED' as day, 'Wednesday' as day_name, 3 as serial
    UNION
    SELECT 'THU' as day, 'Thursday' as day_name, 4 as serial
    UNION
    SELECT 'FRI' as day, 'Friday' as day_name, 5 as serial
    UNION
    SELECT 'SAT' as day, 'Saturday' as day_name, 6 as serial
    UNION
    SELECT 'SUN' as day, 'Sunday' as day_name, 7 as serial
)
SELECT s.store_name, dw.day_name,
IFNULL(GROUP_CONCAT( CONCAT(TIME_FORMAT(open_from, '%h:%i %p'), ' - ', TIME_FORMAT(open_to, '%h:%i %p')) ORDER BY open_from SEPARATOR ' & '), 'Closed') AS status
FROM days_of_week dw
LEFT JOIN hours_of_operation ho ON dw.day = ho.week_day
CROSS JOIN (SELECT store_name FROM store WHERE store_id = 1) s
GROUP BY s.store_name, dw.day_name, dw.serial
ORDER BY dw.serial;



CREATE TABLE appointment
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
store_id INT UNSIGNED NOT NULL,
week_day ENUM('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN') DEFAULT NULL,
from_time TIME NOT NULL,
to_time TIME NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_hours_of_operation_store FOREIGN KEY (store_id) REFERENCES store(store_id) ON DELETE RESTRICT ON UPDATE CASCADE
);



DELIMITER $$

CREATE FUNCTION is_available(p_store_id DATE, p_date DATE, p_time TIME) RETURNS BOOLEAN
    DETERMINISTIC
    READS SQL DATA
BEGIN

    DECLARE v_available BOOLEAN;

    SELECT IF( EXISTS(
        SELECT *
        FROM hours_of_operation
        WHERE store_id = p_store_id
        AND ELT(DAYOFWEEK(p_date), 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat') = week_day
        AND p_time BETWEEN open_from AND open_to)
    , 1, 0) INTO v_available;


    RETURN v_available;
END $$
DELIMITER ;


--Is there is an availability
-- input store_id, date, time


