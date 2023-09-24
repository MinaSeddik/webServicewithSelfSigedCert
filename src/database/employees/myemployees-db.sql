
--https://www3.ntu.edu.sg/home/ehchua/programming/sql/SampleDatabases.html#zz-2.
--https://dev.mysql.com/doc/employee/en/

DROP DATABASE IF EXISTS my_employees;

CREATE DATABASE my_employees;

USE my_employees;

CREATE TABLE employee
(
--emp_no INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
emp_no INTEGER UNSIGNED NOT NULL PRIMARY KEY,
first_name VARCHAR(45) NOT NULL,
last_name VARCHAR(45) NOT NULL,
email VARCHAR(50) DEFAULT NULL,
phone VARCHAR(20) NOT NULL,
hire_date DATE NOT NULL,
last_date DATE DEFAULT NULL,
birth_date DATE NOT NULL,
gender ENUM('M','F') NOT NULL;
);

CREATE TABLE departement
(
--dept_no INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
dept_no INTEGER UNSIGNED NOT NULL PRIMARY KEY,
dept_name VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE dept_emp
(
emp_no INTEGER UNSIGNED NOT NULL,
dept_no INTEGER UNSIGNED NOT NULL,
start_date DATE NOT NULL,
end_date DATE NOT NULL,   # current employee will have endate = max data - will figure out
CONSTRAINT fk_dept_emp_emp_no FOREIGN KEY emp_no REFERENCES employee(emp_no) ON DELETE STRICT ON UPDATE CASCADE
CONSTRAINT fk_dept_emp_dept_no FOREIGN KEY dept_no REFERENCES departement(dept_no) ON DELETE STRICT ON UPDATE CASCADE
);

CREATE TABLE dept_manager
(
dept_no INTEGER UNSIGNED NOT NULL,
dept_manager_no INTEGER UNSIGNED NOT NULL,
start_date DATE NOT NULL,
end_date DATE NOT NULL,
CONSTRAINT fk_dept_emp_dept_no FOREIGN KEY dept_no REFERENCES departement(dept_no) ON DELETE STRICT ON UPDATE CASCADE
CONSTRAINT fk_dept_manager_emp_no FOREIGN KEY emp_no REFERENCES employee(emp_no) ON DELETE STRICT ON UPDATE CASCADE
);


CREATE TABLE salaries
(
salary_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
emp_no INTEGER UNSIGNED NOT NULL,
salary DECIMAL(10, 2) NOT NULL
start_date DATE NOT NULL,
end_date DATE NOT NULL,
CONSTRAINT fk_salaries_emp_no FOREIGN KEY emp_no REFERENCES employee(emp_no) ON DELETE STRICT ON UPDATE CASCADE
);

CREATE TABLE titles
(
title_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
emp_no INTEGER UNSIGNED NOT NULL,
title VARCHAR(50) NOT NULL,
description VRACHAR(100) DEFAULT NULL,
start_date DATE NOT NULL,
end_date DATE NOT NULL,
CONSTRAINT fk_titles_emp_no FOREIGN KEY emp_no REFERENCES employee(emp_no) ON DELETE STRICT ON UPDATE CASCADE
);






