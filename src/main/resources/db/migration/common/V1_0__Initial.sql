CREATE TABLE USERS (ID INT AUTO_INCREMENT PRIMARY KEY, USERID VARCHAR(45));

INSERT INTO USERS (ID, USERID) VALUES (1, 'tutorialspoint.com');

CREATE TABLE product(
    id SERIAL PRIMARY KEY ,
    name varchar(255),
    price double precision
);