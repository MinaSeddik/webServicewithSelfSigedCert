

--https://vertabelo.com/blog/modeling-an-open-marketplace-for-education/

DROP DATABASE IF EXISTS open_marketplace_education_db;

CREATE DATABASE IF NOT EXISTS open_marketplace_education_db;

USE open_marketplace_education_db;


CREATE TABLE teacher
(
teacher_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name
last_name
gender
date_of_birth
email
phone
username
password
confirmation_code
confirmation_time
active

max_travel_distance  COMMENT 'represents the maximum distance a teacher is able to travel to meet with a student. A value of zero indicates that the teacher can’t travel to teach students'
cost_to_travel
profile_image
teaching_since COMMENT 'years of experiance'
brief_description
timezone_id

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE teaching_location_type
(
teaching_location_type_id --PK,
teaching_location_type_name VARCHAR(50) NOT NULL UNIQUE COMMENT ' online, the teacher’s place, the student’s place, or some neutral location',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE address
(
address_id --PK,
address1
address2
city
state
location
app_number
postal_code
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE teacher_teaching_location
(
id
teacher_id INTEGER UNSIGNED NOT NULL,                   -- FK
teaching_location_type_id INTEGER UNSIGNED NOT NULL,    -- FK
address_id INTEGER UNSIGNED NOT NULL,                   -- FK

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE exp_level_teach_teacher
(
exp_level_id --PK,
exp_level_name VARCHAR(50) NOT NULL UNIQUE COMMENT 'beginner, intermediate, expert',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE student_comfortability
(
student_comfortability_id --PK,
teacher_id VARCHAR(50) NOT NULL UNIQUE COMMENT 'some teachers are uncomfortable teaching certain age groups, such as the elderly. Thus, the portal allows teachers to also list their preferences for students with respect to age and gender.',
gender ENUM()
starting_age
age_upto

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE teacher_availability
(
id
teacher_id
start_date_time
duration_in_min
) COMMENT 'stores the teacher’s availability for the next two weeks, and up to a month in advance. These details are modified by teachers on a periodic basis.'


CREATE TABLE timezone
(
timezone_id
timezone_name
start_date_time
duration_in_min
);


CREATE TABLE teacher_earning
(
id
teacher_id
cost_for_30min
cost_for_60min
cost_for_90min
cost_for_120min
);


CREATE TABLE student
(
student_id
first_name
last_name
email
phone
user_name
passwd
is_active

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE teacher_reservation
(
reservation_id
teacher_id
student_id
start_datetime
duration_in_minutes
teaching_location_type_id
teacher_availability_id
);


CREATE TABLE recorded_lesson
(
id
subject --title
lesson_category_id  -- FK
teacher_id  -- FK
lesson_description
video_location  COMMENT 'typically, videos are stored on server file systems, and their locations are stored in this column.',
lesson_transcript
cost_to_subscribe
);

CREATE TABLE lesson_subscription
(
id
student_id
recorded_lesson_id
subscription_date COMMNET 'stores the date when the subscription began. It is usually the same date as when the payment was made for the subscription.'
is_lifetime_subscription
subscription_expiring_on

);