
DROP DATABASE IF EXISTS my_hotel_reservation;

CREATE DATABASE IF NOT EXISTS my_hotel_reservation;

USE my_hotel_reservation;

CREATE TABLE country
(
country_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
insert into my_hotel_reservation.country(country_id, name, last_update) select * from sakila.country;


CREATE TABLE state
(
state_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,   -- state or province (in case of Canada)
country_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_state_country FOREIGN KEY (country_id) REFERENCES country(country_id) ON DELETE RESTRICT ON UPDATE CASCADE
);
INSERT INTO state(name, country_id) VALUES
('California', 103),
('Quebec', 20);


CREATE TABLE city
(
city_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL,
state_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_city_state FOREIGN KEY (state_id) REFERENCES state(state_id) ON DELETE RESTRICT ON UPDATE CASCADE
);
insert into my_hotel_reservation.city(name, state_id) select city, 1 from sakila.city;

CREATE TABLE address
(
address_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
address VARCHAR(50) NOT NULL,
address2 VARCHAR(50) DEFAULT NULL,
city_id INTEGER UNSIGNED NOT NULL,
postal_code VARCHAR(10) DEFAULT NULL,
phone VARCHAR(20) DEFAULT NULL,
location GEOMETRY NOT NULL SRID 4326,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_address_city FOREIGN KEY (city_id) REFERENCES city(city_id) ON DELETE RESTRICT ON UPDATE CASCADE
);
SET FOREIGN_KEY_CHECKS=0;  -- disable foreign key constraint
insert into my_hotel_reservation.address(address, address2, city_id, phone, location)
select address, address2, city_id, phone, ST_GeomFromText( 'POINT(32.70405577169982 -117.157665913614)', 4326)
from sakila.address;
SET FOREIGN_KEY_CHECKS=1;  -- enable it back


CREATE TABLE facility   -- lookup table
(
facility_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
code VARCHAR(30) NOT NULL UNIQUE,
title VARCHAR(60) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO facility(code, title) VALUES
('RESTURANT', 'Restaurant'),
('ROOM_SERVICE', 'Room service'),
('BAR', 'Bar'),
('FRONT_DESK', '24-hour front desk'),
('SAUNA', 'Sauna'),
('FITNESS_CENTER', 'Fitness center'),
('GARDEN', 'Garden'),
('TERRACE', 'Terrace'),
('NON_SMOKING_ROOMS', 'Non-smoking rooms'),
('AIRPORT_SHUTTLE', 'Airport shuttle'),
('FAMILY_ROOM', 'Family rooms'),
('HOT_TUB_JACUZZI', 'Hot tub/Jacuzzi'),
('FREE_WIFI', 'Free WiFi'),
('AIR_CONDITIONING', 'Air conditioning'),
('WATER_APRK', 'Water park'),
('ELECTRIC_CHARGING_STATION', 'Electric vehicle charging station'),
('SWIMMING_POOL', 'Swimming pool'),
('BEACH', 'Beach');


CREATE TABLE language   -- lookup table
(
language_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
code VARCHAR(10) NOT NULL UNIQUE,
name VARCHAR(60) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO language(code, name) VALUES
('EN', 'English'),
('IT', 'Italian'),
('JP', 'Japanese'),
('MR', 'Mandarin'),
('FR', 'French'),
('GR', 'German'),
('AR', 'Arabic');


CREATE TABLE breakfast_type   -- lookup table
(
breakfast_type_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
code VARCHAR(30) NOT NULL UNIQUE,
title VARCHAR(60) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO breakfast_type(code, title) VALUES
('A_LA_CARTE', 'A la Carte'),
('AMERICAN', 'American'),
('ASIAN', 'Asian'),
('BREAKFAST_TO_GO', 'Breakfast to go'),
('BUFFET', 'Buffet'),
('CONTINENTAL', 'Continental'),
('FULL_ENGLISH_IRISH', 'Full English/Irish'),
('GLUTTEN_FREE', 'Gluten-free'),
('HALAL', 'Halal'),
('ITALIAN', 'Italian'),
('KOSHER', 'Kosher'),
('VEGAN', 'Vegan'),
('VEGETARIAN', 'Vegetarian');


CREATE TABLE room_type
(
room_type_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(60) NOT NULL,
description VARCHAR(60) DEFAULT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO room_type(name, description) VALUES
('Single', NULL),
('Double', NULL),
('Twin', NULL),
('Twin/Double', NULL),
('Triple', NULL),
('Quad', NULL),
('Suite', NULL),
('Family', NULL),
('Studio', NULL),
('Apartment', NULL),
('Dorm Room', 'Guests can book an entire private room with beds.'),
('Bed in Dorm', 'Guests can book individual beds within a shared room.');

CREATE TABLE bed_type
(
bed_type_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(60) NOT NULL,
description VARCHAR(60) DEFAULT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO bed_type(name, description) VALUES
('Twin bed(s)', '35–51 inches wide'),
('Full bed(s)', '52–59 inches wide'),
('Queen bed(s)', '60–70 inches wide'),
('King bed(s)', '71–81 inches wide'),
('Bunk bed', 'Varying sizes'),
('Sofa bed', 'Varying sizes'),
('Futon bed(s)', 'Varying sizes');


CREATE TABLE bathroom_item   -- lookup table
(
bathroom_item_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
code VARCHAR(30) NOT NULL UNIQUE,
title VARCHAR(60) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO bathroom_item(code, title) VALUES
('TOILET_PAPER', 'Toilet paper'),
('SHOWER', 'Shower'),
('TOILET', 'Toilet'),
('HAIRDRYER', 'Hairdryer'),
('BATHTUB', 'Bathtub'),
('FREE_TOILETRIES', 'Free toiletries'),
('BIDET', 'Bidet'),
('SLIPPERS', 'Slippers'),
('BATHROBE', 'Bathrobe'),
('SPA_TUB', 'Spa tub');

CREATE TABLE amenity   -- lookup table
(
amenity_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(60) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO amenity(name) VALUES
('Clothes rack'),
('Flat-screen TV'),
('Air conditioning'),
('Linens'),
('Desk'),
('Wake-up service'),
('Towels'),
('Wardrobe or closet'),
('Heating'),
('Fan'),
('Safe'),
('Towels/Sheets (extra fee)'),
('Entire unit located on ground floor'),
('Balcony'),
('Terrace'),
('View'),
('Electric kettle'),
('Tea/Coffee maker'),
('Dining area'),
('Dining table'),
('Microwave');

CREATE TABLE view_type  -- lookup table
(
view_type_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(60) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO view_type(name) VALUES
('City View'),
('Garden View'),
('pool View'),
('Lake View'),
('Mauntain View'),
('Sea View'),
('Ocean View'),
('Landmark View'),
('River View'),
('Quit Street view');


CREATE TABLE name_type  -- lookup table
(
name_type_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(60) NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


INSERT INTO name_type(name) VALUES
('Duplex ...'),
('Double room ...'),
('Queen room ...');

------------------------------------------------------------------------------------
------------------------------------------------------------------------------------


CREATE TABLE hotel
(
hotel_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
hotel_code_bin BINARY(16) NOT NULL UNIQUE,
star_rating ENUM('NONE', 'START_1', 'START_2', 'START_3', 'START_4', 'START_5') NOT NULL DEFAULT 'NONE',
address_id INTEGER UNSIGNED NOT NULL,
desctiption TEXT DEFAULT NULL,

-- breakfast
serve_breakfast TINYINT(1) NOT NULL DEFAULT '1',
breakfast_included_in_price TINYINT(1) NOT NULL DEFAULT '1',

-- parking
parking_availability ENUM('NONE', 'FREE', 'PAID') NOT NULL DEFAULT 'NONE',
parking_need_reservation TINYINT(1) NOT NULL DEFAULT '0',
parking_on_site TINYINT(1) NOT NULL DEFAULT '1',
parking_private TINYINT(1) NOT NULL DEFAULT '1',
parking_cost INT DEFAULT NULL,
parking_code_per_duration ENUM('HOUR', 'DAY', 'STAY') DEFAULT NULL,


-- check-in/check-out rules
check_in_start_time TIME NOT NULL,
check_in_end_time TIME NOT NULL,
check_out_start_time TIME NOT NULL,
check_out_end_time TIME NOT NULL,

-- allows children
allow_children TINYINT(1) NOT NULL DEFAULT '1',

-- allows pets
allow_pet ENUM('NO', 'YES', 'YES_REQUESTED') NOT NULL DEFAULT 'NO',
additional_fee_for_pet TINYINT(1) NOT NULL DEFAULT '0',

active TINYINT(1) NOT NULL DEFAULT '1',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,



-- check CONSTRAINT needed, if parking_availability = 'PAID', then parking_cost and parking_code_per_duration not null.
CONSTRAINT chk_paid_parking
    CHECK ( (parking_availability = 'PAID' AND parking_cost IS NOT NULL AND parking_code_per_duration IS NOT NULL)
        OR  (parking_availability IN ('NONE', 'FREE') ) ),

CONSTRAINT fk_hotal_address FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO hotel(name, hotel_code_bin, star_rating, address_id,
check_in_start_time, check_in_end_time, check_out_start_time, check_out_end_time) VALUES
('My Hotel', UUID_TO_BIN(uuid(), true), 'START_5', 64, '11:00', '12:00', '09:30', '10:00');


CREATE TABLE hotel_photos
(
photo_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
hotel_id INTEGER UNSIGNED NOT NULL,
photo LONGBLOB NOT NULL,
is_face_photo TINYINT(1) NOT NULL DEFAULT '0',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_hotel_photos_hotel FOREIGN KEY (hotel_id) REFERENCES hotel(hotel_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

mysql> SHOW VARIABLES LIKE "secure_file_priv";
+------------------+-------+
| Variable_name    | Value |
+------------------+-------+
| secure_file_priv |       |
+------------------+-------+
1 row in set (0.04 sec)
# if the value is not empty, then the file should be located in the specified directory
# sudo cp ~/Desktop/*.jpeg /var/lib/mysql-files/
# sudo chown mysql:mysql /home/mina/Desktop/pexels-photo-783745.jpeg /home/mina/Desktop/pexels-photo-2343465.jpeg

INSERT INTO hotel_photos(hotel_id, photo) VALUES
(1, LOAD_FILE('/var/lib/mysql-files/pexels-photo-783745.jpeg'));

INSERT INTO hotel_photos(hotel_id, photo) VALUES
(1, LOAD_FILE('/var/lib/mysql-files/pexels-photo-2343465.jpeg'));


CREATE TABLE hotel_facility
(
hotel_id INTEGER UNSIGNED NOT NULL,
facility_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY(hotel_id, facility_id),
CONSTRAINT fk_hotel_facility_hotel FOREIGN KEY (hotel_id) REFERENCES hotel(hotel_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_hotel_facility_facility FOREIGN KEY (facility_id) REFERENCES facility(facility_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO hotel_facility(hotel_id, facility_id) VALUES
(1, 2),
(1, 3),
(1, 5),
(1, 6),
(1, 10);

CREATE TABLE hotel_language
(
hotel_id INTEGER UNSIGNED NOT NULL,
language_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY(hotel_id, language_id),
CONSTRAINT fk_hotel_language_hotel FOREIGN KEY (hotel_id) REFERENCES hotel(hotel_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_hotel_language_language FOREIGN KEY (language_id) REFERENCES language(language_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO hotel_language(hotel_id, language_id) VALUES
(1, 1),
(1, 2),
(1, 5),
(1, 7);

CREATE TABLE hotel_breakfast
(
hotel_id INTEGER UNSIGNED NOT NULL,
breakfast_type_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY(hotel_id, breakfast_type_id),
CONSTRAINT fk_hotel_breakfast_hotel FOREIGN KEY (hotel_id) REFERENCES hotel(hotel_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_hotel_breakfast_breakfast_type FOREIGN KEY (breakfast_type_id) REFERENCES breakfast_type(breakfast_type_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


INSERT INTO hotel_breakfast(hotel_id, breakfast_type_id) VALUES
(1, 1),
(1, 2),
(1, 5),
(1, 6);


CREATE TABLE room
(
room_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
hotel_id INTEGER UNSIGNED NOT NULL,
room_code_bin BINARY(16) NOT NULL UNIQUE,
room_type_id INTEGER UNSIGNED NOT NULL,
num_of_rooms MEDIUMINT UNSIGNED NOT NULL,
price_per_night DECIMAL(10,2) NOT NULL,
max_guests MEDIUMINT NOT NULL,
room_area DOUBLE DEFAULT NULL,
room_area_unit ENUM('METER_SQUARE', 'FEET_SQUARE') DEFAULT 'METER_SQUARE',
allows_smoking TINYINT(1) NOT NULL DEFAULT '0',
private_bathroom TINYINT(1) NOT NULL DEFAULT '1',
description VARCHAR(60) DEFAULT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_room_hotel FOREIGN KEY (hotel_id) REFERENCES hotel(hotel_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_room_room_type FOREIGN KEY (room_type_id) REFERENCES room_type(room_type_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO room(hotel_id, room_code_bin, room_type_id, num_of_rooms, price_per_night, max_guests, room_area) VALUES
(1, UUID_TO_BIN(uuid(), true), 2, 10, 100, 7, 23);

INSERT INTO room(hotel_id, room_code_bin, room_type_id, num_of_rooms, price_per_night, max_guests, room_area) VALUES
(1, UUID_TO_BIN(uuid(), true), 3, 10, 80, 4, 52);



CREATE TABLE room_bed
(
room_id INTEGER UNSIGNED NOT NULL,
bed_type_id INTEGER UNSIGNED NOT NULL,
number_of_beds TINYINT UNSIGNED NOT NULL DEFAULT '1',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY(room_id, bed_type_id),
CONSTRAINT fk_room_bed_room FOREIGN KEY (room_id) REFERENCES room(room_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_room_bed_bed_type FOREIGN KEY (bed_type_id) REFERENCES bed_type(bed_type_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO room_bed(room_id, bed_type_id, number_of_beds) VALUES
(1, 3, 2),
(2, 4, 1);



CREATE TABLE room_bathroom_item
(
room_id INTEGER UNSIGNED NOT NULL,
bathroom_item_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY(room_id, bathroom_item_id),
CONSTRAINT fk_room_bathroom_item_room FOREIGN KEY (room_id) REFERENCES room(room_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_room_bathroom_item_bathroom_item FOREIGN KEY (bathroom_item_id) REFERENCES bathroom_item(bathroom_item_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO room_bathroom_item(room_id, bathroom_item_id) VALUES
(1, 3),
(1, 5),
(1, 7),
(2, 3),
(2, 8),
(2, 9),
(2, 10);



CREATE TABLE room_amenity
(
room_id INTEGER UNSIGNED NOT NULL,
amenity_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY(room_id, amenity_id),
CONSTRAINT fk_room_amenity_room FOREIGN KEY (room_id) REFERENCES room(room_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_room_amenity_amenity FOREIGN KEY (amenity_id) REFERENCES amenity(amenity_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO room_amenity(room_id, amenity_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 4),
(2, 5),
(2, 9),
(2, 10);


CREATE TABLE room_view_type
(
room_id INTEGER UNSIGNED NOT NULL,
view_type_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY(room_id, view_type_id),
CONSTRAINT room_view_type_room FOREIGN KEY (room_id) REFERENCES room(room_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT room_view_type_view_type FOREIGN KEY (view_type_id) REFERENCES view_type(view_type_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO room_view_type(room_id, view_type_id) VALUES
(1, 3),
(2, 6);


CREATE TABLE room_name_type
(
room_id INTEGER UNSIGNED NOT NULL,
name_type_id INTEGER UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY(room_id, name_type_id),
CONSTRAINT fk_room_name_type_room FOREIGN KEY (room_id) REFERENCES room(room_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_room_name_type_name_type FOREIGN KEY (name_type_id) REFERENCES name_type(name_type_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


INSERT INTO room_name_type(room_id, name_type_id) VALUES
(1, 2),
(2, 2);


CREATE TABLE reservation
(
reservation_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
hotel_id INT UNSIGNED NOT NULL,
room_id INT UNSIGNED NOT NULL,
reservation_checkin_date DATE NOT NULL,
reservation_checkout_date DATE NOT NULL,
reservation_code_bin BINARY(16) NOT NULL UNIQUE,  -- QR code represented as UUID 16-bytes binary
num_of_rooms MEDIUMINT UNSIGNED NOT NULL DEFAULT '1',
reserved_username VARCHAR(50) NOT NULL,
reserved_user_phone VARCHAR(50) NOT NULL,
reserved_user_email VARCHAR(50) DEFAULT NULL,
price DECIMAL(10, 2) NOT NULL,   -- price per night * num of nights
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT date_chk CHECK (reservation_checkin_date < reservation_checkout_date),
CONSTRAINT positive_price CHECK (price > 0),
CONSTRAINT fk_reservation_hotel FOREIGN KEY (hotel_id) REFERENCES hotel(hotel_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES room(room_id) ON DELETE RESTRICT ON UPDATE CASCADE
);



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

    DECLARE max_room_avail TINYINT;

    SELECT rm.num_of_rooms - SUM(foo.reserved_rooms) AS avail_rooms INTO max_room_avail
    FROM
    (
        SELECT r.room_id, IFNULL(s.num_of_rooms, 0) AS reserved_rooms
        FROM room r
        INNER JOIN reservation s ON r.hotel_id = s.hotel_id AND r.room_id = s.room_id
            AND r.hotel_id = NEW.hotel_id AND r.room_id = NEW.room_id
            AND (NEW.reservation_checkin_date < s.reservation_checkout_date AND NEW.reservation_checkout_date > s.reservation_checkin_date)
    ) AS foo
    INNER JOIN room rm ON foo.room_id = rm.room_id
    GROUP BY foo.room_id;


    IF max_room_avail < NEW.num_of_rooms THEN
        signal sqlstate '45000' set message_text = 'Num of Rooms are not available for this interval';
    END IF;

END $$

DELIMITER ;




DELIMITER $$

CREATE TRIGGER reservation_interval_update_overlap
    BEFORE UPDATE
    ON reservation FOR EACH ROW
BEGIN

    DECLARE max_room_avail TINYINT;

    SELECT rm.num_of_rooms - SUM(foo.reserved_rooms) AS avail_rooms INTO max_room_avail
    FROM
    (
        SELECT r.room_id, IFNULL(s.num_of_rooms, 0) AS reserved_rooms
        FROM room r
        INNER JOIN reservation s ON r.hotel_id = s.hotel_id AND r.room_id = s.room_id
            AND r.hotel_id = NEW.hotel_id AND r.room_id = NEW.room_id
            AND (NEW.reservation_checkin_date < s.reservation_checkout_date AND NEW.reservation_checkout_date > s.reservation_checkin_date)
    ) AS foo
    INNER JOIN room rm ON foo.room_id = rm.room_id
    GROUP BY foo.room_id;


    IF max_room_avail < NEW.num_of_rooms THEN
        signal sqlstate '45000' set message_text = 'Num of Rooms are not available for this interval';
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
--1) hotel_id = 1
--2) room_id = 1
--3) reservation_checkin_date = '2023-08-08'
--4) reservation_checkout_date = '2023-08-08'
--5) num_of_rooms = 2

     # sanity check
        # check valid reservation_dates (checkin and checkout) are available with num of rooms specfied


    SELECT rm.room_id, rm.num_of_rooms - SUM(foo.reserved_rooms) AS avail_rooms
    FROM
    (
        SELECT r.room_id, IFNULL(s.num_of_rooms, 0) AS reserved_rooms
        FROM room r
        LEFT JOIN reservation s ON r.hotel_id = s.hotel_id AND r.room_id = s.room_id
            AND r.hotel_id = 1
            AND r.room_id = 1
            AND ('2023-08-08' < s.reservation_checkout_date
                  AND '2023-08-12' > s.reservation_checkin_date)
    ) AS foo
    INNER JOIN room rm ON foo.room_id = rm.room_id
    GROUP BY foo.room_id;


    INSERT INTO reservation (   hotel_id, room_id, reservation_code_bin, reservation_checkin_date, reservation_checkout_date,
                                num_of_rooms,
                                reserved_username, reserved_user_phone, reserved_user_email, price) VALUES
    (
        1,
        1,
        UUID_TO_BIN(uuid(), true),
        '2023-08-08',
        '2023-08-10',
        2,
        'John Smith', '438-971-2548', 'john.smith@gamil.com',
        (SELECT price_per_night * DATEDIFF('2023-08-10', '2023-08-08') FROM room WHERE room_id = 1)

     );

    INSERT INTO reservation (   hotel_id, room_id, reservation_code_bin, reservation_checkin_date, reservation_checkout_date,
                                num_of_rooms,
                                reserved_username, reserved_user_phone, reserved_user_email, price) VALUES
    (
        1,
        2,
        UUID_TO_BIN(uuid(), true),
        '2023-08-08',
        '2023-08-10',
        1,
        'John Smith', '438-971-2548', 'john.smith@gamil.com',
        (SELECT price_per_night * DATEDIFF('2023-08-10', '2023-08-08') FROM room WHERE room_id = 2)

     );
     ------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
--                    GET ROOMS for HOTEL
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
------------------------------------------------------------------------------------
-- Input:
---------
--1) hotel_code
--2) reservation_checkin_date = '2023-08-08'
--3) reservation_checkout_date = '2023-08-08'


-- Output:
----------
--1) hotel code
--2) room code  if available in given date range
--3) room info  if available in given date range
--4) available num of rooms in a given date range
--5) price for N nights

SELECT  h.hotel_id, r.room_id,
        r.num_of_rooms - IFNULL(rs.reserved_rooms, 0) AS avail_rooms,
        r.price_per_night,
        price_per_night * DATEDIFF('2023-08-12', '2023-08-08') AS total_price
FROM hotel h
INNER JOIN room r ON h.hotel_id = r.hotel_id
LEFT JOIN (SELECT hotel_id, room_id, SUM(num_of_rooms) AS reserved_rooms
            FROM reservation
            WHERE hotel_id = 1
                AND ('2023-08-08' < reservation_checkout_date AND '2023-08-12' > reservation_checkin_date)
            GROUP BY hotel_id, room_id
            ) rs ON h.hotel_id = rs.hotel_id AND r.room_id = rs.room_id;





