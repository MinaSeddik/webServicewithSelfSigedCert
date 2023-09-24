
drop TABLE event;
drop TABLE weekly_event;

CREATE TABLE event
(
event_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
title VARCHAR(100) NOT NULL,
description TEXT DEFAULT NULL,
start_date DATE NOT NULL,  -- index here will be great
end_date DATE DEFAULT NULL,
start_time TIME NOT NULL,
end_time TIME NOT NULL,
recurrent_type ENUM('NONE', 'DAILY', 'WEEKLY', 'MONTHLY', 'ANNULY') NOT NULL,
repeat_every TINYINT NOT NULL DEFAULT '1',
max_accurance INT UNSIGNED DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE weekly_event
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
event_id INT UNSIGNED NOT NULL,
recurrent_week_day ENUM('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN') DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_weekly_event_event FOREIGN KEY (event_id) REFERENCES event(event_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE event_exception
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
event_id INT UNSIGNED NOT NULL,
instance_id INT UNSIGNED NOT NULL,
instance_date DATE NOT NULL,   -- new date (rescheduled to)
start_time TIME DEFAULT NULL,   -- rescheduled time or in case of canceled
end_time TIME DEFAULT NULL,     -- rescheduled time or in case of canceled
canceled TINYINT(1) NOT NULL
comment VARCHAR(150) DEFAULT NULL,  -- Why this was cancelled/rescheduled.
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


truncate TABLE weekly_event;
truncate TABLE event;

INSERT INTO event (title, description, start_date, end_date, start_time, end_time, recurrent_type) values
('My title - non recurring', 'my description', '2023-08-06',  '2024-08-06', '11:00:00', '12:00:00', 'NONE');

INSERT INTO event (title, description, start_date, end_date, start_time, end_time, recurrent_type) values
('My title - daily', 'my description', '2023-01-01',  '2024-01-01', '11:00:00', '12:00:00', 'DAILY');

INSERT INTO event (title, description, start_date, end_date, start_time, end_time, recurrent_type) values
('My title - weekly', 'my description', '2023-01-01',  '2024-01-01', '11:00:00', '12:00:00', 'WEEKLY');

INSERT INTO event_exception (event_id, instance_id, instance_date, start_time, end_time, canceled, comment ) values
(2, ??, ......);    -- should be filled-in from the UI

INSERT INTO weekly_event (event_id, recurrent_week_day) values
(3, 'MON');

INSERT INTO weekly_event (event_id, recurrent_week_day) values
(3, 'WED');


---------------------------------------------------------------------
---------------------------------------------------------------------
--      HANDLE NONE RECURRING EVENTS
---------------------------------------------------------------------
---------------------------------------------------------------------
SELECT NULL as instance_id, e.title, e.start_date, e.start_time, e.end_time
FROM event e
WHERE e.start_date BETWEEN '2023-08-01' AND '2023-08-07'
AND e.recurrent_type = 'NONE';
---------------------------------------------------------------------
---------------------------------------------------------------------
---------------------------------------------------------------------
---------------------------------------------------------------------


---------------------------------------------------------------------
---------------------------------------------------------------------
--      HANDLE DAILY EVENTS
---------------------------------------------------------------------
---------------------------------------------------------------------
WITH RECURSIVE dates AS (
    SELECT event_id, repeat_every, start_date AS date, max_accurance, 1 AS serial_no
    FROM event
    WHERE start_date <= '2023-08-01' AND (end_date IS NULL OR end_date >= '2023-08-07')
    AND recurrent_type = 'DAILY'
    UNION ALL
    SELECT event_id, repeat_every, DATE_ADD(date, INTERVAL repeat_every DAY) AS date, max_accurance, serial_no + 1 AS serial_no
    FROM dates
    WHERE date <= '2023-08-07' AND (max_accurance IS NULL OR serial_no < max_accurance)
)
SELECT foo.serial_no AS instance_id, e.title, foo.date, e.start_time, e.end_time
FROM event e
CROSS JOIN (SELECT event_id, serial_no, repeat_every, date FROM dates) foo ON foo.event_id = e.event_id AND foo.date BETWEEN '2023-08-01' AND '2023-08-07'
WHERE e.start_date <= '2023-08-01' AND (e.end_date IS NULL OR e.end_date >= '2023-08-07')
AND e.recurrent_type = 'DAILY';
---------------------------------------------------------------------
---------------------------------------------------------------------
---------------------------------------------------------------------
---------------------------------------------------------------------
-- testing
UPDATE event SET repeat_every=3 WHERE event_id = 1;
UPDATE event SET max_accurance=30 WHERE event_id = 1;

---------------------------------------------------------------------
---------------------------------------------------------------------
--      HANDLE WEEKLY EVENTS
---------------------------------------------------------------------
---------------------------------------------------------------------

DELIMITER $$

CREATE FUNCTION next_day_of_week(p_date DATE, p_day_of_week INT) RETURNS DATE
    DETERMINISTIC
    READS SQL DATA
BEGIN

    DECLARE v_next_matching_day_of_week DATE;

    SELECT p_date + INTERVAL ((7 - weekday(p_date) - 1) + p_day_of_week) % 7 DAY INTO v_next_matching_day_of_week;

    RETURN v_next_matching_day_of_week;
END $$
DELIMITER ;


WITH RECURSIVE dates AS (
    SELECT event.event_id, repeat_every, next_day_of_week(start_date, we.recurrent_week_day) AS date,
    max_accurance, serial_no, events_per_week
    FROM event
    INNER JOIN (SELECT event_id, recurrent_week_day,
        ROW_NUMBER() OVER (PARTITION BY event_id ORDER BY recurrent_week_day) AS serial_no,
        COUNT(*) OVER (PARTITION BY event_id) AS events_per_week
        FROM weekly_event) we ON event.event_id = we.event_id
    WHERE start_date <= '2023-08-01' AND (end_date IS NULL OR end_date >= '2023-08-07')
    AND recurrent_type = 'WEEKLY'
    UNION ALL
    SELECT event_id, repeat_every, DATE_ADD(date, INTERVAL 7 DAY) AS date, max_accurance,
    serial_no + events_per_week AS serial_no, events_per_week
    FROM dates
    WHERE date < '2023-08-07' AND (max_accurance IS NULL OR serial_no < max_accurance)
)
SELECT foo.serial_no AS instance_id, e.title, foo.date, e.start_time, e.end_time
FROM event e
CROSS JOIN (SELECT event_id, serial_no, repeat_every, date FROM dates) foo ON foo.event_id = e.event_id AND foo.date BETWEEN '2023-08-01' AND '2023-08-07'
WHERE e.start_date <= '2023-08-01' AND (e.end_date IS NULL OR e.end_date >= '2023-08-07')
AND e.recurrent_type = 'WEEKLY';

---------------------------------------------------------------------
---------------------------------------------------------------------
---------------------------------------------------------------------
---------------------------------------------------------------------



-- you can then continue
--  - WEEKDAY
--  - MONTHLY
--  - ANNUALLY












