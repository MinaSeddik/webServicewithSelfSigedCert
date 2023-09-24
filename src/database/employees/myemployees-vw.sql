
CREATE OR REPLACE VIEW dept_emp_latest_date
AS
SELECT emp_no, MAX(from_date) AS from_date, MAX(to_date) AS to_date
FROM dept_emp
GROUP BY emp_no;


CREATE OR REPLACE VIEW current_dept_emp
AS
SELECT d.emp_no, d.dept_no, d.from_date, d.to_date
FROM dept_emp d
INNER JOIN
(SELECT emp_no, MAX(from_date) AS from_date, MAX(to_date) AS to_date
FROM dept_emp
GROUP BY emp_no) latest

ON d.emp_no = latest.emp_no
AND d.from_date = latest.from_date AND d.to_date = latest.to_date;



