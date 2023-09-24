
DROP DATABASE IF EXISTS hospital;

CREATE DATABASE IF NOT EXISTS hospital;

USE hospital;

CREATE TABLE hospital
(
hospital_id
name
branch
address_id
active
created_at
updated_at
);




CREATE TABLE user
(
user_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
tst VARCHAR(100) NOT NULL COMMENT 'test comment'
);


CREATE TABLE staff
(
staff_id
fisrt_name
last_name
email
phone
photo
title   -- many to many to keep history
departement   -- many to many to keep history
joined_date
leave_date
leave_reason
employment_type -- one to many to keep history   FULL-TIME, PART-TIME, CONSULTANT
salary  -- one to many to keep history
active
created_at
updated_at
);




CREATE TABLE title
(
title_id
tittle_name UNIQUE
active
created_at
updated_at
);

examples: NURSE, DOCTOR, CASHIER, ASSISTANT_DOCTOR, SALES, PHARMACIER, OFFICE_BOY


CREATE TABLE staff_title
(
staff_id
title_id
effective_from  DATETIME
effective_untill DATETIME DEFAULT NULL
created_at
updated_at
);

CREATE TABLE staff_department
(
staff_id
dept_id
effective_from  DATETIME
effective_untill DATETIME DEFAULT NULL
created_at
updated_at
);


CREATE TABLE departement
(
dept_id
dept_name
staff_manager_id
active
created_at
updated_at
);

CREATE TABLE schedule
(
staff_id
dept_id
shift_date
start_time
end_time
status ('SCHEDULED', 'DECLINED', 'APPROVED', 'DONE')
created_at
updated_at
);


CREATE TABLE patient
(
patient_id
first_name
last_name
email
phone
created_at
updated_at
);


CREATE TABLE doctor   -- (should be included in staff)
(
doctor_id
first_name
last_name
email
phone
date_joined
speciality (depatment)
created_at
updated_at
);




CREATE TABLE in_patient         -- patient who is residence in the hospital
(
in_patient_id
patient_id
admission_date
dismiss_date
room_id   -- many to many relation
doctor_id
);


CREATE TABLE out_patient         -- patient who is not residence outer clinic patient
(
out_patient_id
patient_id
visit_date
doctor_id
);

CREATE TABLE room
(
room_id
room_code
floor
active
room_type
);


CREATE TABLE bill
(
bill_no
patient_id
doctor_id
doctor_charge
medicine_charge
lab_charge
nursing_charge
);


CREATE TABLE document
(
document_id
doc_name
document_type   -- foreign key of document_type table or ENUM

patient_id
patient_case_id


url
doc_data BLOB,
details TEXT,
created_at
updated_at
);
