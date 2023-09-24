DROP DATABASE IF EXISTS my_poll;

CREATE DATABASE IF NOT EXISTS my_poll;

USE my_poll;

CREATE TABLE user
(
user_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
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

CREATE TABLE poll
(
poll_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
user_id INT UNSIGNED NOT NULL,   -- FK
question VARCHAR(150) NOT NULL,
start_date DATETIME DEFAULT NULL,
end_date DEFAULT NULL,
published TINYINT(1),       -- default value is set according to business needs
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE poll_answer
(
poll_answer_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
poll_id INT UNSIGNED NOT NULL,  -- FK
answer VARCHAR(50) NOT NULL,
serial_order INT NOT NULL, -- the order which they get listed
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE poll_vote
(
poll_id INT UNSIGNED NOT NULL,   -- we can get rid of it
user_id   -- FK
poll_answer_id  -- FK
answer_date DATETIME,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

WITH total_count AS (
    SELECT COUNT(*) AS total FROM poll_vote WHERE poll_id = 123456789;
)
SELECT pa.answer, count(*) AS count, count(*) / tc.total * 100 AS percent
FROM poll_answer pa, total_count tc
LEFT JOIN poll_vote pv ON pa.poll_answer_id = pv.poll_answer_id
INNER JOIN total_count
WHERE pa.poll_id = 123456789
GROUP BY pa.poll_answer_id



SELECT pa.answer, count(*) AS count, count(*) / tc.total * 100 AS percent
FROM poll_answer pa
CROSS JOIN (select count(*) as total_movies from movies_actors) tc
LEFT JOIN poll_vote pv ON pa.poll_answer_id = pv.poll_answer_id
INNER JOIN total_count
WHERE pa.poll_id = 123456789
GROUP BY pa.poll_answer_id

