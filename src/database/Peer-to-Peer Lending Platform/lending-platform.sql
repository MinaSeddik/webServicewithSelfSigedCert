
-- https://vertabelo.com/blog/connecting-borrowers-and-lenders-a-peer-to-peer-lending-platform-data-model/

DROP DATABASE IF EXISTS lending_platform;

CREATE DATABASE lending_platform;

USE lending_platform;


CREATE TABLE investor
(
investor_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(30) NOT NULL,
last_name VARCHAR(30) DEFAULT NULL,
email VARCHAR(50) DEFAULT NULL,
phone VARCHAR(50) NOT NULL,
usename VARCHAR(50) NOT NULL,
password VARCHAR(50) NOT NULL,
address_id INT UNSIGNED NOT NULL,
tax_id INT UNSIGNED NOT NULL COMMENT('references taxes table: outside the scope of this model'),
kyc_complete TINYINT(1) NOT NULL DEFAULT '0' COMMENT('The KYC (Know Your Customer) process is performed to capture investors’ complete details.'),
escrow_account_number VARCHAR(20),
fund_committed BIGINT UNSIGNED COMMENT('The amount that the investor has committed for investment (so far).'),
date_of_birth DATE NOT NULL,
ssn CHAR(9),
notes TEXT DEFAULT NULL,
photo BLOB DEFAULT NULL,
confirmation_code VARCHAR(36) NOT NULL,
confirmation_time DATETIME COMMENT('When the registration/confirmation was completed.'),
active TINYINT(1) NOT NULL DEFAULT '1',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE nominee
(
nominee_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(30) NOT NULL,
last_name VARCHAR(30) DEFAULT NULL,
email VARCHAR(50) DEFAULT NULL,
phone VARCHAR(50) DEFAULT NULL,
date_of_birth DATE NOT NULL,
investor_id INT UNSIGNED NOT NULL,
relationship_with_investor VARCHAR(150) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE account_statement
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
investor_id INT UNSIGNED NOT NULL,
transaction_type_code CHAR(1) NOT NULL COMMENT('(W)ithdrawal transaction or (D)eposit trasaction'), -- it could be ENUM
transaction_amount BIGINT UNSIGNED NOT NULL,
transaction_date DATETIME NOT NULL,
closing_balance TINYINT(1) NOT NULL DEFAULT '0' COMMENT('closing_balance is updated based on if the investor withdraw all his amount or not'),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE payment_method
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
investor_id INT UNSIGNED NOT NULL,
account_number VARCHAR(100),
account_holder_name VARCHAR(100),
wire_trnasfer_code CHAR(3),
bank_name VARCHAR(50),
account_type ENUM('SAVING', 'CURRENT'),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE borrower
(
borrower_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(30) NOT NULL,
last_name VARCHAR(30) DEFAULT NULL,
email VARCHAR(50) DEFAULT NULL,
phone VARCHAR(50) NOT NULL,
usename VARCHAR(50) NOT NULL,
password VARCHAR(50) NOT NULL,
address_id INT UNSIGNED NOT NULL,
kyc_complete TINYINT(1) NOT NULL DEFAULT '0' COMMENT('The KYC (Know Your Customer) process is performed to capture investors’ complete details.'),
highest_qualification VARCHAR(200) NOT NULL,
passout_year YEAR,
university_name VARCHAR(100),
date_of_birth DATE NOT NULL,
ssn CHAR(9),
notes TEXT DEFAULT NULL,
photo BLOB DEFAULT NULL,
confirmation_code VARCHAR(36) NOT NULL,
confirmation_time DATETIME COMMENT('When the registration/confirmation was completed.'),
active TINYINT(1) NOT NULL DEFAULT '1',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE employment_details
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
borrower_id INT UNSIGNED NOT NULL,
from_year YEAR NOT NULL,
to_year YEAR,
from_month TINYINT NOT NULL,
to_month TINYINT,
title VRACHAR(120) NOT NULL,
organization VARCHAR(100) NOT NULL,
employment_type ENUM('FULL_TIME', 'PART_TIME', 'CONSULTANT'),
employment_description TEXT,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE loan_ticket
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
borrower_id INT UNSIGNED NOT NULL,
loan_amount BIGINT UNSIGNED NOT NULL,
loan_tenure_in_month INT UNSIGNED NOT NULL,
interest_rate DECIMAL(10, 2) NOT NULL,
risk_rate ENUM('A', 'B', 'C', 'D', 'E', 'F'),
reason_for_loan TEXT,
ability_to_repay TEXT COMMENT('The portal captures bullet points referring to the borrower’s ability to repay a loan.'),
risk_factors TEXT COMMENT('Stores information captured by the portal with reference to the risks associated with investing in this loan.'),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE borrower_liability
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
loan_ticket_id INT UNSIGNED NOT NULL,
liability_outstanding BIGINT UNSIGNED NOT NULL,
liability_type ENUM('HOME_LOAN', 'CAR_LOAN', 'EDUCATION_LOAN', 'OTHER') NOT NULL,
monthly_repayment_amount DECIMAL(10, 2) NOT NULL,
liability_start_date DATE,
liability_end_date DATE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE borrower_asset
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
loan_ticket_id INT UNSIGNED NOT NULL,
asset_type ENUM('FIXED_DEPOSIT', 'REAL_ESITATE', 'INVESTMENT') NOT NULL,
asset_value BIGINT UNSIGNED NOT NULL,
ownership_percentage DECIMAL(10, 2) NOT NULL,
possession_since DATE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE investor_proposal
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
investor_id INT UNSIGNED NOT NULL,
loan_ticket_id INT UNSIGNED NOT NULL,
proposal_amount BIGINT UNSIGNED NOT NULL,
proposal_date DATETIME NOT NULL,
cancel_date DATETIME DEFAULT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE loan_ticket_fulfilment
(
loan_ticket_fulfillment_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
loan_ticket_id INT UNSIGNED NOT NULL,
investor_id INT UNSIGNED NOT NULL,
release_date_from_investor DATE,
disburse_date_to_borrower DATE,
pre_emi_amount BIGINT UNSIGNED NOT NULL COMMENT('It is the monthly amount you must pay your lender to repay a loan or debit'),
pre_emi_due_date DATE,
emi_amount BIGINT UNSIGNED NOT NULL,
emi_start_date DATE,
emi_end_date DATE
num_of_total_emi INT UNSIGNED NOT NULL,
pre_closure_flag TINYINT(1) NOT NULL DEFAULT '0' COMMENT('Pre-closure is the process when one repays the loan before the loan tenure ends.'),
pre_closure_date DATE
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- records are inserted into this table for each EMI payment schedule
CREATE TABLE loan_repayment_schedule
(
id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
loan_ticket_fulfillment_id INT UNSIGNED NOT NULL,
emi_due_date DATE,
due_interest_amount DECIMAL(10, 2) NOT NULL,
due_principle_amount DECIMAL(10, 2) NOT NULL,
due_emi_amount DECIMAL(10, 2) NOT NULL,
penality_amount DECIMAL(10, 2) NOT NULL,
receive_date DATE,
is_emi_payment_defaulted TINYINT(1) NOT NULL COMMENT('If the EMI is not paid by the due date, this column is updated with ‘Y’. By default, this column remains blank.'),
is_emi_payment_advanced TINYINT(1) NOT NULL COMMENT('If one or more future EMIs have already been paid, this column is updated to ‘Y’ against all those records'),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

Formula to Calculate EMI on Loans
===================================
P x R x (1+R)^N / [(1+R)^N-1]


P: Principal loan amount = INR 10,000,00

N: Loan tenure in months = 120 months

R: Interest rate per month [7.2/12/100] = 0.006

EMI = INR 10,00,000 * 0.006 * (1 + 0.006)120 / ((1 + 0.006)120 – 1) = INR 11,714.


