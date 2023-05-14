
DROP TABLE IF EXISTS `book`;


-- in myapp database

CREATE TABLE book
(
id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
Code VARCHAR(100),
isbn VARCHAR(50),
author VARCHAR(50),
firstname VARCHAR(50),
lastname VARCHAR(50),
is_shipped bool DEFAULT 0,
price INTEGER,
status VARCHAR(50),
title VARCHAR(150),
statuses VARCHAR(50),
date_printed_at date,
datetime_printed_at datetime,
issued_at date
);