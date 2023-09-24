
DROP DATABASE IF EXISTS my_uber_eat;

CREATE DATABASE IF NOT EXISTS my_uber_eat;

USE my_uber_eat;

CREATE TABLE country
(

);

CREATE TABLE state
(

);

CREATE TABLE city
(

);


CREATE TABLE address
(

);

CREATE TABLE customer
(

address_id  -- delivery address for the customer
);


CREATE TABLE restaurant
(

address_id  -- delivery address for the restaurant

menu -- may be another entity
);

CREATE TABLE driver
(

vehicle_id      -- Vehicle details: The details of the driver’s vehicle.
BOOLEAN                -- Availability: The availability of the driver.  ???
);



CREATE TABLE order
(
order_id
restaurant_id

customer_id
delivery_address
driver_id
order_date
);


CREATE TABLE order_item
(
order_id
order_item

);

CREATE TABLE payment
(
order_id
total_price
payment_date

);



