
--https://vertabelo.com/blog/from-couch-potato-to-marathoner-a-marathon-training-app-data-model/


DROP DATABASE IF EXISTS marathon-training;

CREATE DATABASE IF NOT EXISTS marathon-training;

USE marathon-training;



CREATE TABLE running_event
(
running_event_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
event_name VARCHAR(128) NOT NULL,
city_name VARCHAR(40) NOT NULL,
country_name VARCHAR(40) NOT NULL,
inception_year YEAR NOT NULL,
last_race_held_on DATE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE user
(
user_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
fist_name VARCHAR(40) NOT NULL,
last_name VARCHAR(40) NOT NULL,
gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
date_of_birth DATE NOT NULL,
email VARCHAR(50) NOT NULL,
phone VARCHAR(20) NOT NULL,
username VARCHAR(40) NOT NULL,
passwd VARCHAR(40) NOT NULL,
active TINYINT(1) NOT NULL DEFAULT '1',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE title
(
title_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
title_name VARCHAR(50) NOT NULL,
title_order SMALLINT UNSIGNED NOT NULL,
running_event_id INT UNSIGNED NOT NULL,
distance_to_run DECIMAL(10, 2) NOT NULL,
min_time_to_run DECIMAL(10, 2) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE user_title
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
user_id INT UNSIGNED NOT NULL,
title_id INT UNSIGNED NOT NULL,
achieved_on DATE NOT NULL,
running_distance DECIMAL(10, 2) NOT NULL,
running_time_in_seconds DECIMAL(12, 4) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- goals defined in the app
CREATE TABLE goal
(
goal_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
goal_name VARCHAR(128) NOT NULL,
distance_to_run DECIMAL(10, 2) NOT NULL,
target_time_in_minutes DECIMAL(10, 2) NOT NULL,
gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
starting_age SMALLINT NOT NULL,
closing_age SMALLINT NOT NULL,

-- restrictions
--If these prerequisites are not met, that goal will not be available to the user. However, both of these columns are nullable; there will be some goals that have no prerequisite fitness requirements
current_run_distance_per_week DECIMAL(10, 2) COMMENT('The minimum running distance achieved before a user can set a certain goal'),
current_min_title_id DECIMAL(10, 2) COMMENT('The minimum title users must hold to set this goal.'),

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE activity
(
activity_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
activity_name VARCHAR(128) NOT NULL COMMENT('walking, speed walking, running, swimming, cycling, yoga, stretching, and weightlifting etc.'),
min_pace_in_minutes_per_meter DECIMAL(10, 2),
max_pace_in_minutes_per_meter DECIMAL(10, 2),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE goal_activity
(
goal_activity_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
goal_id INT UNSIGNED NOT NULL,
activity_id INT UNSIGNED NOT NULL,
seq_of_day INT UNSIGNED COMMENT('holds values that signify the day when an activity is to be performed. Obviously, this sequence starts from 1 for any goal. It can never be ZERO or NULL. Numbers may not be consecutive for a goal; this would mean rest days have been set'),
distance_to_cover DECIMAL(10, 2) COMMENT('This is nullable, as there are activities (like yoga, stretching, and weightlifting) in which distance does not matter. '),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE user_goal
(
user_goal_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
user_id INT UNSIGNED NOT NULL,
goal_id INT UNSIGNED NOT NULL,
is_active TINYINT(1) NOT NULL DEFAULT '1' COMMENT('Shows if a user is still progressing on this goal.'),
start_date DATE,
expected_end_date DATE,
actual_end_date DATE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE transition_plan
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
user_goal_id INT UNSIGNED NOT NULL,
goal_activity_id INT UNSIGNED NOT NULL,
original_calendar_date DATE NOT NULL COMMENT('A calendar date denoting when an activity needs to be performed. This value is populated when the app generates a training plan'),
planned_calendar_date DATE COMMENT('Initially, this column remains blank. A date is populated as and when a change is made in the training plan.'),
actual_calendar_date DATE COMMENT('This column is populated as soon as the user marks an activity as complete. This is the date when the activity is actually finished.'),
is_complete TINYINT(1) NOT NULL,
start_timestamp TIMESTAMP NOT NULL,
end_timestamp TIMESTAMP,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



