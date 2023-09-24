
--Reference: https://www.sqlservercentral.com/articles/departures-from-origins-and-arrivals-at-destinations

DROP DATABASE IF EXISTS my_flight_reservation;

CREATE DATABASE IF NOT EXISTS my_flight_reservation;

USE my_flight_reservation;

CREATE TABLE address
(
address_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
address VARCHAR(50) NOT NULL,
address2 VARCHAR(50) DEFAULT NULL,
city_id INTEGER UNSIGNED NOT NULL,   # <---- go back to the chain city, state, country
postal_code VARCHAR(10) DEFAULT NULL,
phone VARCHAR(20) DEFAULT NULL,
location GEOMETRY NOT NULL SRID 4326,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--Here is Global Airport database
--http://www.partow.net/miscellaneous/airportdatabase/
--https://www.sqlservercentral.com/articles/departures-from-origins-and-arrivals-at-destinations
CREATE TABLE airport
(
airport_code CHAR(3) NOT NULL PRIMARY KEY,   -- IATA 3-letter code
airport_icao_code CHAR(4) NOT NULL UNIQUE,
airport_name VARCHAR(200) NOT NULL,
city VARCHAR(30) NOT NULL,  -- duplicated value as it is found in address
address_id INT UNSIGNED DEFAULT NULL,   -- should not be defaulted to NULL, I make it null, just for simplicity
timezone VARCHAR(40) NOT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
longitude_degree INT NOT NULL,
longitude_minute INT NOT NULL,
longitude_second INT NOT NULL,
longitude_direction CHAR(1) NOT NULL,
longitude_decimal_degree DOUBLE NOT NULL,
# longitude_decimal_degree DOUBLE AS ((CASE longitude_direction WHEN 'E' THEN 1.0 WHEN 'W' THEN -1.0 END) * (longitude_degree + (longitude_minute / 60) + (longitude_second / 3600))) NOT NULL,
latitude_degree INT NOT NULL,
latitude_minute INT NOT NULL,
latitude_second INT NOT NULL,
latitude_direction CHAR(1) NOT NULL,
latitude_decimal_degree DOUBLE NOT NULL,
# latitude_decimal_degree DOUBLE AS ((CASE latitude_direction WHEN 'N' THEN 1.0 WHEN 'S' THEN -1.0 END) * (latitude_degree + (latitude_minute / 60) + (latitude_second / 3600))) NOT NULL,
location GEOMETRY AS (ST_GeomFromText( CONCAT('POINT(', latitude_decimal_degree, ' ',  longitude_decimal_degree,')'), 4326)),
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
# CONSTRAINT chk_longitude_direction CHECK (longitude_direction IN('E','W')),
# CONSTRAINT chk_latitude_direction CHECK (latitude_direction IN('N','S'))
);

# sudo cp ~/Desktop/airport_info.csv /var/lib/mysql-files/
# sudo chown mysql:mysql /var/lib/mysql-files/airport_info.csv


LOAD DATA INFILE '/var/lib/mysql-files/airport_info.csv'
IGNORE -- forces to ignore existed records
INTO TABLE airport
FIELDS TERMINATED BY ','
ENCLOSED BY "'"
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES
(airport_icao_code, airport_code, airport_name, city, @country,
latitude_degree, latitude_minute, latitude_second, latitude_direction, latitude_decimal_degree,
longitude_degree, longitude_minute, longitude_second, longitude_direction, longitude_decimal_degree,
@altitude)
SET timezone = 'America/Montreal';


SELECT airport_code, city, timezone
FROM airport
WHERE airport_code IN ('', '');


CREATE TABLE aircraft
(
aircraft_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
type VARCHAR(30) NOT NULL,
model VARCHAR(30) DEFAULT NULL,
max_seats INT UNSIGNED NOT NULL,
year YEAR DEFAULT NULL,
description TEXT DEFAULT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
# CONSTRAINT chk_aircraft_type_list CHECK (type IN('Airbus A380','Boeing 747'))
);


# sudo cp ~/Desktop/aircraft_info.csv /var/lib/mysql-files/
# sudo chown mysql:mysql /var/lib/mysql-files/aircraft_info.csv


LOAD DATA INFILE '/var/lib/mysql-files/aircraft_info.csv'
INTO TABLE aircraft
FIELDS TERMINATED BY ','
ENCLOSED BY "'"
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES
(type, max_seats, model);


CREATE TABLE airline
(
airline_code CHAR(2) NOT NULL PRIMARY KEY,
airline_name VARCHAR(50) NOT NULL,
call_sign VARCHAR(150) DEFAULT NULL,  -- The radio call sign used when flight or ground crews refer to a flight over the air.
country VARCHAR(100) NOT NULL,
logo LONGBLOB DEFAULT NULL,
description TEXT DEFAULT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO airline(airline_code, airline_name, country) VALUES
('PX', 'Air Niugini Limited', 'Asia Pacific'),
('CG', 'AIRLINES PNG', 'Papua New Guinea');


CREATE TABLE terminal
(
terminal_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
description VARCHAR(150) DEFAULT NULL,
airport_code CHAR(3) NOT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_terminal_airport FOREIGN KEY (airport_code) REFERENCES airport(airport_code) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO terminal(name, airport_code) VALUES
('XXX-Terminal-1', 'HYN'),
('YYY-Terminal-2', 'HYN');

drop table flight;

CREATE TABLE flight   -- flight leg (from one point to another point)
(
flight_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
airline_code CHAR(2) NOT NULL,
flight_code VARCHAR(4) NOT NULL,
flight_number VARCHAR(6) GENERATED ALWAYS AS (CONCAT(airline_code, flight_code)),
source_airport_code CHAR(3) NOT NULL,
destination_airport_code CHAR(3) NOT NULL,
departure_time TIME NOT NULL,   -- local time
arrival_time TIME NOT NULL,     -- local time
day CHAR(2) GENERATED ALWAYS AS (IF(departure_time > arrival_time, '+1', NULL)),
week_day TINYINT UNSIGNED DEFAULT '127',  -- Default = every day of the week
effective_from DATETIME DEFAULT NULL,
valid_untill DATETIME DEFAULT NULL,
flight_time_minutes INT UNSIGNED DEFAULT '0',
flight_distance_miles INT UNSIGNED DEFAULT '0',
flight_distance_km DOUBLE GENERATED ALWAYS AS (ROUND(flight_distance_miles * 1.60934, 2)),
aircraft_id INT UNSIGNED NOT NULL,
source_airport_terminal_id INT UNSIGNED NOT NULL,
destination_airport_terminal_id INT UNSIGNED NOT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
# CONSTRAINT chk_dates CHECK (departure_time < arrival_time),
# CONSTRAINT chk_airports CHECK (source_airport_code != destination_airport_code),
CONSTRAINT fk_flight_source_airport FOREIGN KEY (source_airport_code) REFERENCES airport(airport_code) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_flight_destination_airport FOREIGN KEY (destination_airport_code) REFERENCES airport(airport_code) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_flight_aircraft FOREIGN KEY (aircraft_id) REFERENCES aircraft(aircraft_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_flight_airline FOREIGN KEY (airline_code) REFERENCES airline(airline_code) ON DELETE RESTRICT ON UPDATE CASCADE
# CONSTRAINT fk_flight_source_airport_terminal FOREIGN KEY (source_airport_terminal_id) REFERENCES terminal(terminal_id) ON DELETE RESTRICT ON UPDATE CASCADE,
# CONSTRAINT fk_flight_destination_airport_terminal FOREIGN KEY (destination_airport_terminal_id) REFERENCES terminal(terminal_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

# sudo cp ~/Desktop/flight_info.csv /var/lib/mysql-files/
# sudo chown mysql:mysql /var/lib/mysql-files/flight_info.csv

SET FOREIGN_KEY_CHECKS=0;
LOAD DATA INFILE '/var/lib/mysql-files/flight_info.csv'
# IGNORE -- forces to ignore existed records
INTO TABLE flight
FIELDS TERMINATED BY ','
ENCLOSED BY "'"
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES
(airline_code, flight_code, source_airport_code, destination_airport_code, departure_time, arrival_time)
SET aircraft_id = 1,
source_airport_terminal_id = 3,
destination_airport_terminal_id = 4;
SET FOREIGN_KEY_CHECKS=1;


SELECT flight_number, source_airport_code AS origin, destination_airport_code AS dest,
departure_time, arrival_time, day
FROM flight
WHERE flight_number in ('PX008', 'PX009');

+---------------+--------+------+----------------+--------------+------+
| flight_number | origin | dest | departure_time | arrival_time | day  |
+---------------+--------+------+----------------+--------------+------+
| PX008         | POM    | HKG  | 16:35:00       | 20:55:00     | NULL |
| PX009         | HKG    | POM  | 22:55:00       | 07:35:00     | +1   |
+---------------+--------+------+----------------+--------------+------+


UPDATE airport SET timezone = 'Asia/Hong_Kong' WHERE airport_code = 'HKG';
UPDATE airport SET timezone = 'Pacific/Port_Moresby' WHERE airport_code = 'POM';
POM is +10:00 and HKG is +08:00

SELECT CONVERT_TZ('2035-10-03 10:30:45', 'UTC', 'Asia/Hong_Kong');
SELECT CONVERT_TZ('2035-10-03 10:30:45', 'UTC', 'Pacific/Port_Moresby');

SELECT airport_code, city, timezone
FROM airport
WHERE airport_code IN ('POM', 'HKG');

+--------------+--------------+----------------------+
| airport_code | city         | timezone             |
+--------------+--------------+----------------------+
| HKG          | HONG KONG    | Asia/Hong_Kong       |
| POM          | PORT MORESBY | Pacific/Port_Moresby |
+--------------+--------------+----------------------+



FlightNo Origin Destination DepartureTime ArrivalTime ArrivalDay SegmentTime
PX008    POM    HKG         16:35         20:55                  380
PX009    HKG    POM         22:55         07:35       +1         400

SELECT f.flight_number, f.source_airport_code AS origin, f.destination_airport_code AS dest,
f.departure_time, f.arrival_time, f.day,
TIMESTAMPDIFF(MINUTE,
    CONVERT_TZ(f.departure_time + CAST('2023-01-01' AS DATETIME), a1.timezone ,'UTC'),
    CONVERT_TZ(DATE_ADD(f.arrival_time + CAST('2023-01-01' AS DATETIME), INTERVAL IFNULL(f.day, 0) DAY) , a2.timezone ,'UTC')
    ) As 'Flight duration (Minutes)'
FROM flight f
INNER JOIN airport a1 ON f.source_airport_code = a1.airport_code
INNER JOIN airport a2 ON f.destination_airport_code = a2.airport_code
WHERE f.flight_number in ('PX008', 'PX009');

+---------------+--------+------+----------------+--------------+------+---------------------------+
| flight_number | origin | dest | departure_time | arrival_time | day  | Flight duration (Minutes) |
+---------------+--------+------+----------------+--------------+------+---------------------------+
| PX008         | POM    | HKG  | 16:35:00       | 20:55:00     | NULL |                       380 |
| PX009         | HKG    | POM  | 22:55:00       | 07:35:00     | +1   |                       400 |
+---------------+--------+------+----------------+--------------+------+---------------------------+



SELECT f.flight_number, f.source_airport_code AS origin, f.destination_airport_code AS dest,
ROUND(ST_Distance_Sphere( a1.location, a2.location ) / 1000, 2) AS 'Distance (KM)'
FROM flight f
INNER JOIN airport a1 ON f.source_airport_code = a1.airport_code
INNER JOIN airport a2 ON f.destination_airport_code = a2.airport_code
WHERE f.flight_number in ('PX008', 'PX009');

+---------------+--------+------+---------------+
| flight_number | origin | dest | Distance (KM) |
+---------------+--------+------+---------------+
| PX008         | POM    | HKG  |       5063.58 |
| PX009         | HKG    | POM  |       5063.58 |
+---------------+--------+------+---------------+

--------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------
-------------           SEARCH FLIGHT       ------------------------------------------------------------
--------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------
Inputs:
--------
1) origin_airport = 'POM'
2) dest_airport = 'HKG'
3) date = '2023-08-14'
4) stops = 0

-- flight with 0 stops
SELECT f.flight_number, f.source_airport_code AS origin, f.destination_airport_code AS dest,
f.departure_time, f.arrival_time,
DATE_ADD(CAST('2023-08-14' AS DATETIME) + f.departure_time, INTERVAL 0 DAY) AS depart_date_time,
DATE_ADD(CAST('2023-08-14' AS DATETIME) + f.arrival_time, INTERVAL IFNULL(f.day, 0) DAY) AS arrival_date_time,
TIMESTAMPDIFF(MINUTE,
    CONVERT_TZ(f.departure_time + CAST('2023-08-14' AS DATETIME), a1.timezone ,'UTC'),
    CONVERT_TZ(DATE_ADD(f.arrival_time + CAST('2023-08-14' AS DATETIME), INTERVAL IFNULL(f.day, 0) DAY) , a2.timezone ,'UTC')
    ) As 'Duration (Minutes)',
ROUND(ST_Distance_Sphere( a1.location, a2.location ) / 1000, 2) AS 'Distance (KM)',
0 As stops
FROM flight f
INNER JOIN airport a1 ON f.source_airport_code = a1.airport_code
INNER JOIN airport a2 ON f.destination_airport_code = a2.airport_code
WHERE f.source_airport_code = 'POM'
    AND f.destination_airport_code = 'HKG'
    AND (f.week_day / ELT(DAYOFWEEK('2023-08-14'), 1, 2, 4, 8, 16, 32, 64)) % 2 = 1;


-- flight with 1 stop
SELECT
f1.flight_number,
f1.source_airport_code AS origin,
f1.destination_airport_code AS dest,
f1.departure_time,
f1.arrival_time,
DATE_ADD(CAST('2023-08-14' AS DATETIME) + f1.departure_time, INTERVAL 0 DAY) AS depart_date_time,
DATE_ADD(CAST('2023-08-14' AS DATETIME) + f1.arrival_time, INTERVAL IFNULL(f1.day, 0) DAY) AS arrival_date_time,
TIMESTAMPDIFF(MINUTE,
    CONVERT_TZ(f1.departure_time + CAST('2023-08-14' AS DATETIME), a1.timezone ,'UTC'),
    CONVERT_TZ(DATE_ADD(f1.arrival_time + CAST('2023-08-14' AS DATETIME), INTERVAL IFNULL(f1.day, 0) DAY) , a2.timezone ,'UTC')
    ) As 'Duration (Minutes)',
ROUND(ST_Distance_Sphere( a1.location, a2.location ) / 1000, 2) AS 'Distance (KM)',
1 As stops
FROM flight f1
INNER JOIN flight f2 ON f1.destination_airport_code = f2.source_airport_code
INNER JOIN airport a1 ON f1.source_airport_code = a1.airport_code
INNER JOIN airport a2 ON f2.destination_airport_code = a2.airport_code
WHERE f1.source_airport_code = 'POM'
    AND f2.destination_airport_code = 'HKG'
    AND (f1.week_day / ELT(DAYOFWEEK('2023-08-14'), 1, 2, 4, 8, 16, 32, 64)) % 2 = 1
    AND (f2.week_day / ELT(DAYOFWEEK(DATE_ADD(CAST('2023-08-14' AS DATETIME), INTERVAL IFNULL(f1.day, 0) DAY)), 1, 2, 4, 8, 16, 32, 64)) % 2 = 1
    AND DATE_ADD(f1.arrival_time, INTERVAL 1 HOUR) <= f2.departure_time; -- max 1 hour between 2 flights





-- flight with 2 stops
SELECT
f1.flight_number,
f1.source_airport_code AS origin,
f1.destination_airport_code AS dest,
f1.departure_time,
f1.arrival_time,
DATE_ADD(CAST('2023-08-14' AS DATETIME) + f1.departure_time, INTERVAL 0 DAY) AS depart_date_time,
DATE_ADD(CAST('2023-08-14' AS DATETIME) + f1.arrival_time, INTERVAL IFNULL(f1.day, 0) DAY) AS arrival_date_time,
TIMESTAMPDIFF(MINUTE,
    CONVERT_TZ(f1.departure_time + CAST('2023-08-14' AS DATETIME), a1.timezone ,'UTC'),
    CONVERT_TZ(DATE_ADD(f1.arrival_time + CAST('2023-08-14' AS DATETIME), INTERVAL IFNULL(f1.day, 0) DAY) , a2.timezone ,'UTC')
    ) As 'Duration (Minutes)',
ROUND(ST_Distance_Sphere( a1.location, a2.location ) / 1000, 2) AS 'Distance (KM)',
2 As stops
FROM flight f1
INNER JOIN flight f2 ON f1.destination_airport_code = f2.source_airport_code
INNER JOIN flight f3 ON f2.destination_airport_code = f3.source_airport_code
INNER JOIN airport a1 ON f1.source_airport_code = a1.airport_code
INNER JOIN airport a2 ON f2.destination_airport_code = a2.airport_code
INNER JOIN airport a3 ON f2.destination_airport_code = a3.airport_code
WHERE f1.source_airport_code = 'POM'
    AND f3.destination_airport_code = 'HKG'
    AND (f1.week_day / ELT(DAYOFWEEK('2023-08-14'), 1, 2, 4, 8, 16, 32, 64)) % 2 = 1
    AND (f2.week_day / ELT(DAYOFWEEK(DATE_ADD(CAST('2023-08-14' AS DATETIME), INTERVAL IFNULL(f1.day, 0) DAY)), 1, 2, 4, 8, 16, 32, 64)) % 2 = 1
    AND (f3.week_day / ELT(DAYOFWEEK(DATE_ADD(CAST('2023-08-14' AS DATETIME), INTERVAL IFNULL(f1.day, 0) + IFNULL(f2.day, 0) DAY)), 1, 2, 4, 8, 16, 32, 64)) % 2 = 1
    AND DATE_ADD(f1.arrival_time, INTERVAL 1 HOUR) <= f2.departure_time -- max 1 hour between 2 flights
    AND DATE_ADD(f2.arrival_time, INTERVAL 1 HOUR) <= f3.departure_time; -- max 1 hour between 2 flights














CREATE TABLE travel_class
(
travel_class_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
class_name VARCHAR(30) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT chk_name_list CHECK (class_name IN('First Class','Business Class','Premium Economy','Economy Class','Basic Economy'))
);

INSERT INTO travel_class(class_name) VALUES
('First Class'),
('Business Class'),
('Premium Economy'),
('Economy Class'),
('Basic Economy');



CREATE TABLE seat
(
seat_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
seat_code CHAR(10) NOT NULL,
seat_type ENUM('AISLE', 'WINDOW') NOT NULL,
flight_id INT UNSIGNED NOT NULL,
travel_class_id INT UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_seat_flight FOREIGN KEY (flight_id) REFERENCES flight(flight_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_seat_travel_class FOREIGN KEY (travel_class_id) REFERENCES travel_class(travel_class_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE flight_cost
(
seat_id INT UNSIGNED NOT NULL,
effective_from DATE NOT NULL,
valid_until DATE NOT NULL,
price DECIMAL(10,2) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (seat_id, effective_from),
CONSTRAINT fk_flight_cost_seat FOREIGN KEY (seat_id) REFERENCES seat(seat_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE service
(
service_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
service_name VARCHAR(100) NOT NULL,
CONSTRAINT service_chk CHECK(service_name IN ('Food', 'French Wine', 'Wifi', 'Entertainment', 'Lounge'))
);

CREATE TABLE service_offering
(
travel_class_id INT UNSIGNED NOT NULL,
service_id INT UNSIGNED NOT NULL,
from_month TINTINT NOT NULL,
to_month TINTINT NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (travel_class_id, service_id),
CONSTRAINT fk_service_offering_travel_class FOREIGN KEY (travel_class_id) REFERENCES travel_class(travel_class_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_service_offering_service FOREIGN KEY (service_id) REFERENCES service(service_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE passenger
(
passenger_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(45) NOT NULL,
last_name VARCHAR(45) NOT NULL,
-- VIRTUAL by default
full_name varchar(120) GENERATED ALWAYS AS (CONCAT(first_name, ' ', last_name)) COMMENT 'Auto-generated column',
passport_num VARCHAR(50) DEFAULT NULL,
email VARCHAR(50) NOT NULL,
address_id INT UNSIGNED DEFAULT NULL,   -- should not be defaulted to NULL, I make it null, just for simplicity
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE reservation
(
reservation_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
passenger_id INT UNSIGNED NOT NULL,
seat_id INT UNSIGNED NOT NULL,
reservation_date DATE NOT NULL,
reservation_code_bin BINARY(16) NOT NULL UNIQUE,  -- QR code represented as UUID 16-bytes binary
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_reservation_passenger FOREIGN KEY (passenger_id) REFERENCES passenger(passenger_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_reservation_seat FOREIGN KEY (seat_id) REFERENCES seat(seat_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE reservation_seat
(
reservation_id INT UNSIGNED NOT NULL,
seat_id INT UNSIGNED NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);



