
--Reference: https://stackoverflow.com/questions/4048151/what-are-the-options-for-storing-hierarchical-data-in-a-relational-database?rq=1

DROP DATABASE IF EXISTS hier;

CREATE DATABASE IF NOT EXISTS hier;

USE hier;


CREATE TABLE employee
(
employee_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(45) NOT NULL,
last_name VARCHAR(45) NOT NULL,
manager_id INTEGER UNSIGNED ,
CONSTRAINT fk_employee_manager FOREIGN KEY (manager_id) REFERENCES employee(employee_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO employee (first_name, last_name, manager_id) VALUES
('Alice', 'Johnson', NULL),
('Bob', 'Smith', 1),
('Carol', 'Brown', 2),
('Dave', 'Miller', 1),
('Emily', 'White', 2);




WITH RECURSIVE employee_hierarchy (employee_id, first_name, last_name, manager_id, level)
AS
(
    -- Anchor member
    SELECT employee_id, first_name, last_name, manager_id, 1 AS level
    FROM employee
    WHERE manager_id IS NULL

    UNION ALL

    -- Recursive member
    SELECT e.employee_id, e.first_name, e.last_name, e.manager_id, level + 1 AS level
    FROM employee e INNER JOIN employee_hierarchy eh ON e.manager_id = eh.employee_id
    WHERE eh.Level < 3  -- to limit level depth
)
SELECT * FROM employee_hierarchy;



