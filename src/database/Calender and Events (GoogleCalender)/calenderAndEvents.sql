
DROP DATABASE IF EXISTS my_canender;

CREATE DATABASE IF NOT EXISTS my_canender;

USE my_canender;

-- could be replaced by enum
CREATE TABLE period_type
(
id TINYINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
period_code CHAR(4) NOT NULL,
period_desc VARCHAR(10) NOT NULL,
sort_order TINYINT NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--period_code,PeriodType,SortOrder
--"d","day(s)",1
--"m","month(s)",3
--"q","quarter(s)",4
--"ww","week(s)",2
--"yyyy","year(s)",5


--------------------------------------------------------

CREATE TABLE event
(
event_id TINYINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
title VARCHAR(100) NOT NULL,
description TEXT DEFAULT NULL,
start_date DATE NOT NULL,
end_date DATE DEFAULT NULL,
recurrent_type ENUM('NONE', 'DAILY', 'WEEKLY', 'MONTHLY', 'ANNULY') NOT NULL,
repeat_every TINYINT NOT NULL DEFAULT '1',
recurrent_week_day ENUM('SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT') DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--write a query to get events between 01/01/2023 : 31/01/2023

SELECT title, start_date
FROM event CROSS JOIN ( SELECT 1 )
WHERE start_date <= <INPUT_START_DATE> AND (end_date IS NULL OR end_date >= <INPUT_END_DATE>)
AND recurrent_type = 'NONE';


WITH RECURSIVE dates AS (
SELECT start_date AS value, id, repeat_every
FROM event
WHERE start_date <= <INPUT_START_DATE> AND (end_date IS NULL OR end_date >= <INPUT_END_DATE>) AND recurrent_type = 'DAILY'
UNION ALL
SELECT value + repeat_every day, id, repeat_every
FROM dates
WHERE value <= INPUT_END_DATE
)
SELECT title, dates.start_date
FROM event
CROSS JOIN ( SELECT value dates ON dates.id = event.id AND dates.value BETWEEN <INPUT_START_DATE> AND <INPUT_END_DATE>-- number of days in the input interval)
WHERE start_date <= <INPUT_START_DATE> AND (end_date IS NULL OR end_date >= <INPUT_END_DATE>)
AND recurrent_type = 'DAILY';







SELECT title, dates.start_date
FROM event CROSS JOIN ( SELECT 1, 2, .... 7 ) dates-- number of days in the input interval
WHERE start_date <= <INPUT_START_DATE> AND (end_date IS NULL OR end_date >= <INPUT_END_DATE>)
AND recurrent_type = 'WEEKLY';


--	[EventID]			Long Integer,
--	[EventDescrip]			Text (510),
--	[EventStart]			DateTime NOT NULL,
--	[RecurCount]			Long Integer,
--	[PeriodFreq]			Long Integer NOT NULL,
--	[PeriodTypeID]			Text (8),
--	[Comment]			Text (510)
--
--EventID,EventDescrip,EventStart,RecurCount,PeriodFreq,PeriodTypeID,Comment
--1,"Birthday","01/01/90 00:00:00",109,1,"yyyy","Not likely to have 110 birthdays."
--2,"Monday classes (fortnightly)","02/18/08 00:00:00",7,2,"ww","Runs for 16 weeks only."
--3,"Monthly board meeting","01/15/08 00:00:00",11,1,"m","Just this year."
--4,"Quarterly tax return","01/01/08 00:00:00",3,1,"q","Just this year."
--5,"28-day mantenance","01/01/08 00:00:00",,28,"d","Never ends"
--6,"One-off event","03/01/08 00:00:00",0,1,"d","It doesn't matter what you have for PeriodFreq and PeriodTypeID if there are zero recurrences."
--

--------------------------------------------------------

CREATE TABLE [tblEventException]
 (
	[EventID]			Long Integer,
	[InstanceID]			Long Integer,
	[InstanceDate]			DateTime,
	[IsCanned]			Integer NOT NULL,
	[InstanceComment]			Text (510)
);

EventID,InstanceID,InstanceDate,IsCanned,InstanceComment
2,1,"03/04/08 00:00:00",0,"Monday March 3rd is a public holiday, so Tuesday this week."
2,4,"04/15/08 00:00:00",0,"No reason"
2,5,,-1,"Not on tonight"
3,5,"06/16/08 00:00:00",0,"testing reschedule"
3,9,,-1,"No October meeting: everyone at convention."
4,0,"01/02/08 00:00:00",0,"New Years day public holiday"

-----------------------------------------------------------------

--Now we need another table to handle the instances that are rescheduled to another date, or to be skipped. tblEventException has these fields:


CREATE TABLE event_exception
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
event_id INT UNSIGNED NOT NULL,
instance_id INT UNSIGNED NOT NULL,
instance_date DATE NOT NULL,   -- new date (rescheduled to)
canceled TINYINT(1) NOT NULL
comment VARCHAR(150) DEFAULT NULL,  -- Why this was cancelled/rescheduled.
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

