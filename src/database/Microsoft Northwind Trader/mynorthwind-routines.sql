
DELIMITER $$

DROP FUNCTION IF EXISTS `CreateNewOrder`$$

CREATE FUNCTION CreateNewOrder(p_employee_id INT, p_customer_id INT) RETURNS INT
DETERMINISTIC
BEGIN
        DECLARE v_order_id INT;

        INSERT ONTO order (order_code, employee_id, customer_id) VALUES(uuid(), p_employee_id, p_customer_id);

        SELECT LAST_INSERT_ID() INTO v_order_id;

        RETURN v_order_id;
END $$


DROP FUNCTION IF EXISTS `Add2Order`$$

CREATE FUNCTION Add2Order(p_order_id INT, p_product_code Varchar(50), qty INT)
DETERMINISTIC
BEGIN

        DECLARE v_product_id INT;
        DECLARE v_retail_price DECIMAL(10, 2);
        DECLARE v_discount DECIMAL(10, 2);

        SELECT product_id INTO v_product_id
        FROM product
        WHERE product_code = p_product_code;

        SELECT retail_price INTO v_retail_price
        FROM product_price_vw
        WHERE product_id = v_product_id;

--        SELECT discount INTO v_discount
--        CASE
--            WHEN discount_type = SINGLE_FLAT THEN discount
--            WHEN discount_type = SINGLE_PERCENTAGE THEN discount / 100
--            WHEN discount_type = QUANTITY_FLAT THEN discount / 100
--            ELSE 0
--        END
--        FROM product_discount_vw
--        WHERE product_id = v_product_id;


--        Add product to order details
       INSERT INTO order_details(order_id, product_id, quantity, unit_price) VALUES(p_order_id,
       v_product_id,
       qty,
--       v_retail_price - IFNULL(v_discount, 0))
       v_retail_price
       ON DUPLICATE KEY UPDATE
       quantity= quantity + qty,
       unit_price = v_retail_price - IFNULL(v_discount, 0);

END $$


DROP FUNCTION IF EXISTS `RemoveFromOrder`$$

CREATE FUNCTION RemoveFromOrder(p_order_id INT, p_product_code Varchar(50), qty INT)
DETERMINISTIC
BEGIN

        DECLARE v_product_id INT;
        DECLARE v_retail_price DECIMAL(10, 2);
        DECLARE v_discount DECIMAL(10, 2);

        SELECT product_id INTO v_product_id
        FROM product
        WHERE product_code = p_product_code;

        SELECT retail_price INTO v_retail_price
        FROM product_price_vw
        WHERE product_id = v_product_id;

--        SELECT discount INTO v_discount
--        CASE
--            WHEN discount_type = SINGLE_FLAT THEN discount
--            WHEN discount_type = SINGLE_PERCENTAGE THEN discount / 100
--            ELSE 0
--        END
--        FROM product_discount_vw
--        WHERE product_id = v_product_id;

        UPDATE order_details
        SET quantity= IF(quantity - qty > 0, quantity - qty, 0),
--        unit_price = v_retail_price - IFNULL(v_discount, 0)
        unit_price = v_retail_price
        WHERE order_id = p_order_id AND product_id = v_product_id;

        -- delete from order_details if quantity is zero
        DELETE
        FROM order_details
        WHERE order_id = p_order_id AND product_id = v_product_id AND quantity <= 0;


END $$


DROP FUNCTION IF EXISTS `PlaceOrder`$$

CREATE FUNCTION PlaceOrder(p_order_id INT)
DETERMINISTIC
BEGIN



        SELECT p.code, p.name, od.quantity, pp.retail_price AS original_unit_price,
        total_price = pp.retail_price * od.quantity AS total_original_price,
        IFNULL(CASE
            WHEN pd.discount_type = SINGLE_FLAT THEN pd.discount * od.quantity
            WHEN pd.discount_type = SINGLE_PERCENTAGE THEN (pd.discount / 100) * od.quantity
            WHEN pd.discount_type = QUANTITY_FLAT THEN pd.discount * (od.quantity DIV pd.discount_quantity),
            WHEN pd.discount_type = QUANTITY_PERCENTAGE THEN (pd.discount / 100) * (od.quantity DIV pd.discount_quantity)
            ELSE 0
        END), 0) AS total_discount,
        total_original_price - total_discount AS total_saving
        FROM order_details od
        INNER JOIN product p ON od.product_id = p.product_id
        INNER JOIN product_price_vw pp ON pp.product_id = p.product_id
        LEFT JOIN product_discount_vw pd ON pd.product_id = pp.product_id
        WHERE od.order_id = p_order_id;



--        Handle Discount
        UPDATE order_details od
        LEFT JOIN product_discount_vw pd ON od.product_id = pd.product_id
        SET total_price = unit_price * quantity AS total_price,
        - (
        CASE
            WHEN discount_type = SINGLE_FLAT THEN discount
            WHEN discount_type = SINGLE_PERCENTAGE THEN discount / 100
            WHEN discount_type = QUANTITY_FLAT THEN discount / 100
            ELSE 0
        END
        )
        WHERE od.order_id = p_order_id;


        SELECT
        CASE
            WHEN discount_type = SINGLE_FLAT THEN discount
            WHEN discount_type = SINGLE_PERCENTAGE THEN discount / 100
            WHEN discount_type = QUANTITY_FLAT THEN discount / 100
            ELSE 0
        END AS discount
        FROM product_discount_vw
        WHERE product_id = v_product_id;



--        Handle product quantity deduction
        UPDATE product p
        INNER JOIN order_details od ON p.product_id = od.product_id AND od.order_id = p_order_id
        SET p.available_quantity = IF(p.available_quantity - od.quantity > 0, p.available_quantity - od.quantity, 0);

--        Handle rewards points
        UPDATE customer c
        INNER JOIN order o ON c.customer_id = o.customer_id AND o.order_id = p_order_id AND o.customer_id IS NOT NULL
        INNER JOIN order_details od ON o.order_id = od.order_id
        INNER JOIN product_rewardpoints_vw p ON od.product_id = p.product_id AND p.reward_points > 0
        SET c.reward_points = c.reward_points + p.reward_points;

        -- it would be better to add a record in customer_rewardpoint_log
        INSERT INTO customer_rewardpoint_log(customer_id, reward_points, order_id, product_id, rewarded_on) VALUES
        SELECT c.customer_id, p.reward_points, o.order_id, p.product_id, now()
        FROM customer c
        INNER JOIN order o ON c.customer_id = o.customer_id AND o.order_id = p_order_id AND o.customer_id IS NOT NULL
        INNER JOIN order_details od ON o.order_id = od.order_id
        INNER JOIN product_rewardpoints_vw p ON od.product_id = p.product_id AND p.reward_points > 0;

END $$


DROP PROCEDURE IF EXISTS `top_seller_product`$$


CREATE PROCEDURE top_seller_product(IN from_date DATE, IN to_date DATE)
    READS SQL DATA
    LANGUAGE SQL
    COMMENT 'Provides top seller products'
BEGIN

        SELECT p.product_id, p.product_code, SUM(od.quantity) AS sold_quantity
        FROM product p
        INNER JOIN order_details od ON p.product_id =od.product_id
        INNER JOIN order o ON o.order_id = od.order_id
        WHERE order_date BETWEEN from_date AND to_date
        GROUP BY p.product_id
        ORDER BY sold_quantity DESC;


END $$




DROP PROCEDURE IF EXISTS `less_seller_product`$$


CREATE PROCEDURE less_seller_product(IN from_date DATE, IN to_date DATE)
    READS SQL DATA
    LANGUAGE SQL
    COMMENT 'Provides less seller products'
BEGIN

        SELECT p.product_id, p.product_code, SUM(od.quantity) AS sold_quantity
        FROM product p
        INNER JOIN order_details od ON p.product_id =od.product_id
        INNER JOIN order o ON o.order_id = od.order_id
        WHERE order_date BETWEEN from_date AND to_date
        GROUP BY p.product_id

        UNION

        SELECT product_id, product_code, 0 AS sold_quantity
        FROM product
        WHERE product_id NOT IN(
            SELECT DISTINCT p.product_id
            FROM product p
            INNER JOIN order_details od ON p.product_id =od.product_id
            INNER JOIN order o ON o.order_id = od.order_id
            WHERE order_date BETWEEN from_date AND to_date
        )
        ORDER BY sold_quantity;




END $$




