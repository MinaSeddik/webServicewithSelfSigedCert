
DROP DATABASE IF EXISTS my_uber;

CREATE DATABASE IF NOT EXISTS my_uber;

USE my_uber;


CREATE TABLE user
(
user_id  PK
first_name
last_name
phone
email
user_name
password
active
created_at
updated_at
);


CREATE TABLE passenger
(
passenger_id  PK
user_id  FK
active
created_at
updated_at
);



CREATE TABLE veichle
(
veichle_id   PK
make
model
year
color
driver_id   FK
is_current_vehicle
created_at
updated_at
);


CREATE TABLE driver
(
driver_id   PK
user_id  FK
created_at
updated_at
);


CREATE TABLE driver_rating
(
id
ride_id  FK
driver_id   FK
rating
comment TEXT
passenger_id  FK
created_at
updated_at
);



CREATE TABLE ride
(
ride_id
passenger_id
driver_id
vechile_id
Starting_location POINT
end_location POINT
source_address
dest_address
start_time
end_time

fare
tip
status ENUM('INITIATED', 'STARTED', 'FINISHED')


Surge_factor *****

created_at
updated_at
);


--script :

create table drivers
(
id varchar(10),
start_time time,
end_time time,
start_loc varchar(10),
end_loc varchar(10)
);

insert into drivers values
('dri_1', '09:00', '09:30', 'a','b'),
('dri_1', '09:30', '10:30', 'b','c'),
('dri_1','11:00','11:30', 'd','e');

insert into drivers values
('dri_1', '12:00', '12:30', 'f','g'),
('dri_1', '13:30', '14:30', 'c','h');

insert into drivers values
('dri_2', '12:15', '12:30', 'f','g'),
('dri_2', '13:30', '14:30', 'c','h');

select id, sum(profit_rides) as profit_rides
from (
    select  id,
            IF(end_loc = lead(start_loc) over(partition by id order by start_time), 1, 0) as profit_rides
            from drivers
     ) as foo
group by id;


select *
from drivers d1, drivers d2
where d1.id = d2.id and d1.start_time = d2.end_time and d1.end_loc = d2.start_loc


