
USE my_sakila;

CREATE VIEW actor_info
AS

SELECT a.actor_id, a.first_name, a.last_name,
GROUP_CONCAT(DISTINCT
    CONCAT(c.name, ': ', (SELECT GROUP_CONCAT(f.title ORDER BY f.title SEPARATOR ',') FROM film f
        INNER JOIN film_category fc ON f.film_id = fc.film_id
        INNER JOIN film_actor fa ON fa.film_id = fc.film_id
        WHERE fa.actor_id = a.actor_id AND fc.category_id = c.category_id
        )
    )
ORDER BY c.name SEPARATOR '; ')
FROM actor a
INNER JOIN film_actor fa ON a.actor_id = fa.actor_id
INNER JOIN film_category fc ON fc.film_id =fa.film_id
INNER JOIN category c ON c.category_id = fc.category_id
GROUP BY a.actor_id, a.first_name, a.last_name;

----------------------------------------------------------------------

CREATE VIEW customer_list
AS
SELECT c.customer_id AS ID, CONCAT(c.first_name, ' ', c.last_name) AS Name,
a.address, a.postal_code AS 'Zip Code', a.phone, cty.city, cntry.country,
IF (c.active, 'Active', 'Inactive') AS notes, c.store_id AS SID
FROM customer c
LEFT JOIN address a ON a.address_id = c.address_id
LEFT JOIN city cty ON a.city_id = cty.city_id
LEFT JOIN country cntry ON cntry.country_id = cty.country_id;

----------------------------------------------------------------------

CREATE VIEW film_list
AS
SELECT f.film_id AS FID, f.title AS title, f.description AS description,
c.name AS 'category', f.rental_rate, f.length, f.rating,
GROUP_CONCAT( DISTINCT CONCAT(a.first_name, ' ', a.last_name) ORDER BY a.first_name SEPARATOR ', ')
FROM film f
LEFT JOIN film_category fc ON fc.film_id = f.film_id
LEFT JOIN category c ON c.category_id = fc.category_id
LEFT JOIN film_actor fa ON f.film_id = fa.film_id
LEFT JOIN actor a ON a.actor_id = fa.actor_id
GROUP BY f.film_id, c.name;

CREATE VIEW film_list2
AS
SELECT f.film_id AS FID, f.title AS title, f.description AS description,
c.name AS 'category', f.rental_rate, f.length, f.rating,
( SELECT GROUP_CONCAT( DISTINCT CONCAT(a.first_name, ' ', a.last_name)
                                ORDER BY a.first_name SEPARATOR ', ')
FROM actor a
    LEFT JOIN film_actor fa ON fa.actor_id= a.actor_id
    WHERE fa.film_id = f.film_id)
FROM film f
LEFT JOIN film_category fc ON fc.film_id = f.film_id
LEFT JOIN category c ON c.category_id = fc.category_id;

----------------------------------------------------------------------

CREATE VIEW sales_by_film_category
AS
SELECT c.name, SUM(p.amount) AS total_sales
FROM category c
LEFT JOIN film_category fc ON c.category_id = fc.category_id
LEFT JOIN inventory i ON i.film_id = fc.film_id
LEFT JOIN rental r ON r.inventory_id = i.inventory_id
LEFT JOIN payment p ON p.rental_id = r.rental_id
GROUP BY c.name
ORDER BY total_sales DESC;

----------------------------------------------------------------------

CREATE VIEW sales_by_store
AS
SELECT CONCAT(city.city, ', ', country.country) AS store,
CONCAT(st.first_name, ' ', st.last_name) AS manager,
SUM(amount) AS total_sales
FROM store s
LEFT JOIN address a ON a.address_id = s.address_id
LEFT JOIN city ON a.city_id = city.city_id
LEFT JOIN country ON city.country_id = country.country_id
LEFT JOIN staff st ON s.manager_staff_id = st.staff_id
LEFT JOIN inventory i ON s.store_id = i.store_id
LEFT JOIN rental r ON r.inventory_id = i.inventory_id
LEFT JOIN payment p ON p.rental_id = r.rental_id
GROUP BY s.store_id
ORDER BY total_sales DESC;

----------------------------------------------------------------------

CREATE VIEW staff_list
AS
SELECT s.staff_id AS ID, CONCAT(s.first_name, ' ', s.last_name) AS name, a.address,
a.postal_code, a.phone, city.city, country.country, st.store_id AS SID
FROM staff s
LEFT JOIN store st ON s.staff_id = st.manager_staff_id
LEFT JOIN address a ON a.address_id = s.address_id
LEFT JOIN city ON a.city_id = city.city_id
LEFT JOIN country ON city.country_id = country.country_id;

----------------------------------------------------------------------


