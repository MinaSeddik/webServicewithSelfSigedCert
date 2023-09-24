DROP DATABASE IF EXISTS my_appointment;

CREATE DATABASE IF NOT EXISTS my_appointment;

USE my_appointment;

CREATE TABLE user
(
user_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(45) NOT NULL,
last_name VARCHAR(45) NOT NULL,
email VARCHAR(50) NOT NULL,
phone VARCHAR(50) NOT NULL,
username VARCHAR(30) NOT NULL,
password VARCHAR(100) NOT NULL,
user_type ENUM('DOCTOR', 'CUSTOMER') NOT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE doctor
(
doctor_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
ssn VARCHAR(10) DEFAULT NULL,
user_id INT UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_doctor_user FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE customer
(
customer_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
user_id INT UNSIGNED NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE hours_of_operation
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
doctor_id INT UNSIGNED NOT NULL,
week_day ENUM('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN') DEFAULT NULL,
start_time TIME NOT NULL,
end_time TIME NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_hours_of_operation_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE appointment
(
doctor_id INT UNSIGNED NOT NULL,
appointment_date DATE NOT NULL,
start_time TIME NOT NULL, -- check statement to be 30 minutes start offset
end_time TIME NOT NULL,
create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (doctor_id, appointment_date, start_time),
CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT mustStartAndEndOnThirtyMinuteBoundary CHECK (
        EXTRACT(MINUTE FROM start_time) % 30 = 0
        AND EXTRACT(SECOND FROM start_time) = 0
    ),
CONSTRAINT mustEndOnThirtyMinuteBoundary CHECK (
        EXTRACT(MINUTE FROM end_time) % 30 = 0
        AND EXTRACT(SECOND FROM end_time) = 0
    )
);


INSERT INTO user(first_name, last_name, email, phone, username, password, user_type) values
('John', 'Smith', 'john.smith@gmail.com', '4561782596', 'john.smith', 'XXXXXXXX', 'DOCTOR');

INSERT INTO doctor(ssn, user_id) values
('123456789', 1);

INSERT INTO hours_of_operation(doctor_id, week_day, start_time, end_time) values
(1, 'MON', '00:00:00', '23:00:00'),
(1, 'TUE', '07:30:00', '14:30:00'),
(1, 'TUE', '16:30:00', '23:00:00'),
(1, 'WED', '19:00:00', '00:30:00'),
(1, 'THU', '09:00:00', '17:00:00'),
(1, 'THU', '13:00:00', '15:30:00'),
(1, 'THU', '18:00:00', '22:00:00');

INSERT INTO hours_of_operation(doctor_id, week_day, start_time, end_time) values
(1, 'FRI', '19:00:00', '00:00:00');

DELETE FROM hours_of_operation WHERE week_day = 'FRI';

--get all 30-minutes slots for a certain date
-- input doctor_id, date to show the slots
--doctor_id = 1
--date = '2023-08-08'
WITH RECURSIVE slots AS (
    SELECT start_time, IF(end_time = '00:00:00', '24:00:00', end_time) AS end_time
    FROM hours_of_operation
    WHERE doctor_id = 1
    AND week_day = ELT(DAYOFWEEK('2023-08-11'), 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT')
    UNION ALL
    SELECT DATE_ADD(start_time,INTERVAL 30 MINUTE) AS start_time, end_time
    FROM slots
    WHERE DATE_ADD(start_time,INTERVAL 30 MINUTE) < end_time
)
SELECT s.start_time, IF(a.start_time IS NULL, 'Available', 'Reserved') AS status
FROM slots s
LEFT JOIN appointment a ON a.doctor_id = 1 AND s.start_time = a.start_time
ORDER BY s.start_time;


-- Reserve a slot 02:30:00
-- input doctor_id, date and start time of the slot to be reserved
--doctor_id = 1
--date = '2023-08-07'
--start_time = '02:30:00'
INSERT INTO appointment(doctor_id, appointment_date, start_time, end_time) VALUES
(1, '2023-08-08', '21:00:00', '21:30:00');


