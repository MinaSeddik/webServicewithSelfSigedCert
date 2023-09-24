
CREATE OR REPLACE product_price_vw
AS
SELECT product_id, retail_price, start_date, valid_untill
FROM product_price p
INNER JOIN (
SELECT MAX(id) AS id
FROM product_price
WHERE CURDATE() BETWEEN start_date AND valid_untill
GROUP BY product_id
) latest ON p.id = latest.id


--SELECT product_id, retail_price, start_date, valid_untill
--FROM product_price p
--INNER JOIN (
--SELECT product_id, MAX(start_date) AS start_date, MAX(valid_untill) AS valid_untill
--FROM product_price
--GROUP BY product_id
--) latest ON p.product_id = latest.product_id AND p.start_date = latest.start_date AND p.valid_untill = latest.valid_untill;


CREATE OR REPLACE product_discount_vw
AS
SELECT product_id, discount, discount_quantity, discount_type, start_date, valid_untill
FROM product_discount p
INNER JOIN (
SELECT product_id, MAX(start_date) AS start_date, MAX(valid_untill) AS valid_untill
FROM product_discount
GROUP BY product_id
) latest ON p.product_id = latest.product_id AND p.start_date = latest.start_date AND p.valid_untill = latest.valid_untill;


CREATE OR REPLACE product_rewardpoints_vw
AS
SELECT product_id, reward_points, start_date, valid_untill
FROM product_rewardpoints p
INNER JOIN (
SELECT product_id, MAX(start_date) AS start_date, MAX(valid_untill) AS valid_untill
FROM product_rewardpoints
GROUP BY product_id
) latest ON p.product_id = latest.product_id AND p.start_date = latest.start_date AND p.valid_untill = latest.valid_untill;



