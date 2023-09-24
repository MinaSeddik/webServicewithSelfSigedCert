DROP DATABASE IF EXISTS my_facebook;

CREATE DATABASE IF NOT EXISTS my_facebook;

USE my_facebook;

CREATE TABLE user
(
user_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(30) NOT NULL,
last_name VARCHAR(30) DEFAULT NULL,
address_id INT UNSIGNED DEFAULT NULL,   -- address table FK
email VARCHAR(50) DEFAULT NULL,
usename VARCHAR(50) NOT NULL,
password VARCHAR(50) NOT NULL,
phone VRACHAR(20) NOT NULL,
notes TEXT DEFAULT NULL,
photo BLOB DEFAULT NULL,
background_photo LONGBLOB DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- registered at
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_profile
(
user_id BIGINT UNSIGNED NOT NULL,   -- FK
date_of_birth
month_of_birth
year_of_birth
language    -- FK
country  -- FK
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Friendship version-1 adds 2 records for each friendship
-- say user 123 and user 456
--if user 123 initiate the friendship with user 456, then
--user_id   friend_id   status
------------------------------
-- 123      456         PENDING
-- 456      123         OFFERED

--in case of Rejection
--user_id   friend_id   status
------------------------------
-- 123      456         DECLINED
-- 456      123         REJECTED

--in case of Acceptance
--user_id   friend_id   status
------------------------------
-- 123      456         CONFIRMED
-- 456      123         CONFIRMED

CREATE TABLE friendship_version1
(
user_id BIGINT UNSIGNED NOT NULL,
friend_id BIGINT UNSIGNED NOT NULL,
added_date DATETIME NOT NULL,
status ENUM('PENDING', 'OFFERED', 'DECLINED', 'REJECTED', 'CONFIRMED', 'BLOCKED') NOT NULL,

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY(user_id, friend_id),
CONSTRAINT fk_friends_user FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
CONSTRAINT fk_friends_friend FOREIGN KEY (friend_id) REFERENCES user(user_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);


-- whenever the status changes
CREATE TABLE friendship_history_version1
(
user_id BIGINT UNSIGNED NOT NULL,
friend_id BIGINT UNSIGNED NOT NULL,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
status ENUM('PENDING', 'OFFERED', 'DECLINED', 'REJECTED', 'CONFIRMED', 'BLOCKED') NOT NULL,

);
-- Friendship version-2 adds only one single record for each friendship
-- say user 123 and user 456
--if user 123 initiate the friendship with user 456, then
--user_id   friend_id   status
------------------------------
-- 123      456         PENDING

--in case of Rejection
--user_id   friend_id   status
------------------------------
-- 123      456         DECLINED

--in case of Acceptance
--user_id   friend_id   status
------------------------------
-- 123      456         CONFIRMED


SELECT friend_id AS friend
FROM friendship_version2
where status = 'CONFIRMED' AND user_id = 456
UNION
SELECT user_id AS friend
FROM friendship_version2
where status = 'CONFIRMED' AND friend_id = 456


CREATE TABLE friendship_version2
(
user_id BIGINT UNSIGNED NOT NULL,
friend_id BIGINT UNSIGNED NOT NULL,
added_date DATETIME NOT NULL,
status ENUM('PENDING', 'OFFERED', 'DECLINED', 'REJECTED', 'CONFIRMED', 'BLOCKED') NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY(user_id, friend_id),
CONSTRAINT fk_friends_user FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
CONSTRAINT fk_friends_friend FOREIGN KEY (friend_id) REFERENCES user(user_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);



CREATE TABLE post
(
post_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
user_id BIGINT UNSIGNED NOT NULL,
post_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
title
content

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);

CREATE TABLE post_like
(
post_id BIGINT UNSIGNED NOT NULL,
user_id BIGINT UNSIGNED NOT NULL,
like_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
like_type ENUM('LIKE', 'LOVE', 'CARE', 'HAHA', 'WOW', 'SAD', 'ANGRY') NOT NULL,

);


CREATE TABLE post_comment
(
comment_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
post_id BIGINT UNSIGNED NOT NULL,
user_id BIGINT UNSIGNED NOT NULL,
comment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
message -- could be TEXT or BLOB to allow binary data like images, stickers
parent_id BIGINT DEFAULT NULL,

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);


CREATE TABLE post_comment_like
(
comment_id BIGINT UNSIGNED NOT NULL,  --FK
user_id BIGINT UNSIGNED NOT NULL,
like_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
like_type ENUM('LIKE', 'LOVE', 'CARE', 'HAHA', 'WOW', 'SAD', 'ANGRY') NOT NULL,

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);

CREATE TABLE group
(
group_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
group_name
created_date
created_by
privacy_mode ENUM('SHARED', 'PRIVATE', 'PUBLIC') NOT NULL,

updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);


CREATE TABLE group_member
(
group_id BIGINT UNSIGNED NOT NULL,
user_id
regitered_date
status ENUM('PENDING', 'DECLINED', 'ACTIVE', 'BLOCKED') NOT NULL,
privacy_mode ENUM('SHARED', 'PRIVATE', 'PUBLIC') NOT NULL,
member_type ENUM('ADMIN', 'REGULAR') NOT NULL,

banner_photo BLOB,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);


CREATE TABLE action
(
action_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
action_code VARCHAR(20) NOT NULL


action_code
-----------
ADD_POST
UPDATE_POST
DELETE_POST
LIKE_POST
MENTIONED_IN_POST
ADD_COMMENT
UPDATE_COMMENT
DELETE_COMMENT
LIKE_COMMENT
MENTIONED_IN_COMMENT
CREATE_EVENT
UPDATE_EVENT
HAS_BIRTHDAY
....
);

CREATE TABLE notification
(
notification_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
receiver_id BIGINT UNSIGNED NOT NULL,   -- FK to user who will receive the notification
actor_id BIGINT UNSIGNED NOT NULL,      -- FK to user who did the action
actor_type ENUM('USER', 'GROUP') NOT NULL,
action_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
action_id BIGINT UNSIGNED NOT NULL,   -- FK to action
object_id BIGINT UNSIGNED DEFAULT NULL,
object_type ENUM('POST', 'COMMENT', 'EVENT', ...)
);

CREATE TABLE group
(
group_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
owner_user_id  -- FK
created_at
group_name
group_code   -- uuid
title
photo
banner_photo
description
privacy_mode ENUM('modes ...')
active

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);

CREATE TABLE group_follower
(
group_id
user_id
added_date
follower_type ENUM('OWNER', 'LIKE','DISLIKE','FOLLOW')

updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE group_post
(
group_post_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
group_id  -- FK
user_id BIGINT UNSIGNED NOT NULL,
post_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
title
content

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);

CREATE TABLE group_post_like
(
post_id BIGINT UNSIGNED NOT NULL,
user_id BIGINT UNSIGNED NOT NULL,
like_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
like_type ENUM('LIKE', 'LOVE', 'CARE', 'HAHA', 'WOW', 'SAD', 'ANGRY') NOT NULL,

);


CREATE TABLE group_post_comment
(
comment_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
post_id BIGINT UNSIGNED NOT NULL,
user_id BIGINT UNSIGNED NOT NULL,
comment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
message -- could be TEXT or BLOB to allow binary data like images, stickers
parent_id BIGINT DEFAULT NULL,

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);


CREATE TABLE group_post_comment_like
(
comment_id BIGINT UNSIGNED NOT NULL,  --FK
user_id BIGINT UNSIGNED NOT NULL,
like_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
like_type ENUM('LIKE', 'LOVE', 'CARE', 'HAHA', 'WOW', 'SAD', 'ANGRY') NOT NULL,

created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);


CREATE TABLE user_chat_message(
messge_id   -- PK
from_user_id -- FK
to_user_id  -- FK
parent_id -- FK
message TEXT,
msg_date_time DATE_TIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
trash
from_read
to_read
edited
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);

CREATE TABLE group_chat(
group_chat_id
name
created_by
created_at
trash
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);


CREATE TABLE group_chat_member(
group_chat_id
user_id
role enum('admin', 'regular')
status (ACTIVE, REMOVED, BLOCKED, DISMISSED)
added_at
removed_at
trash
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);


CREATE TABLE user_chat_message(
messge_id   -- PK
group_chat_id  -- FK
from_user_id -- FK
parent_id -- FK
message TEXT,
msg_date_time DATE_TIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
trash
edited
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);