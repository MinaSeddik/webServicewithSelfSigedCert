
DELIMITER $$

DROP PROCEDURE IF EXISTS `debug_msg`$$

CREATE PROCEDURE debug_msg(enabled INTEGER, msg VARCHAR(255))
BEGIN
  IF enabled THEN
    select concat('** ', msg) AS '** DEBUG:';
  END IF;
END $$


--get_customer_balance
-- The get_customer_balance function returns the current amount owing on a specified
-- customer's account.

DELIMITER ;

DELIMITER $$

CREATE FUNCTION get_customer_balance2(p_customer_id INTEGER, p_effective_date DATETIME) RETURNS DECIMAL(10,2)
    DETERMINISTIC
    READS SQL DATA
BEGIN

       #OK, WE NEED TO CALCULATE THE CURRENT BALANCE GIVEN A CUSTOMER_ID AND A DATE
       #THAT WE WANT THE BALANCE TO BE EFFECTIVE FOR. THE BALANCE IS:
       #   1) RENTAL FEES FOR ALL PREVIOUS RENTALS
       #   2) ONE DOLLAR FOR EVERY DAY THE PREVIOUS RENTALS ARE OVERDUE
       #   3) IF A FILM IS MORE THAN RENTAL_DURATION * 2 OVERDUE, CHARGE THE REPLACEMENT_COST
       #   4) SUBTRACT ALL PAYMENTS MADE BEFORE THE DATE SPECIFIED

      DECLARE v_total_fees DECIMAL(10 ,2); # Total fees
      DECLARE v_payments DECIMAL(10 ,2); # SUM OF PAYMENTS MADE PREVIOUSLY


        SELECT
            # 1) Calculate rent fees
            IFNULL(SUM(f.rental_rate), 0) +

            # 2) Calculate over fees
            IFNULL(SUM(IF(r.return_date IS NOT NULL AND DATEDIFF(r.return_date, r.rental_date) > f.rental_duration,
                     DATEDIFF(r.return_date, r.rental_date) - f.rental_duration, 0)), 0) +

            # 3) Calculate Replacement fee if applied
            IFNULL(SUM(IF(r.return_date IS NOT NULL AND DATEDIFF(r.return_date, r.rental_date) >= 2 * f.rental_duration,
                     f.replacement_cost, 0)), 0) INTO v_total_fees

       FROM film f
       LEFT JOIN inventory i ON i.film_id = f.film_id
       LEFT JOIN rental r ON i.inventory_id = r.inventory_id
       WHERE r.customer_id = p_customer_id AND r.rental_date <= p_effective_date;



       # 4) All paymnet before date specified
       SELECT IFNULL(SUM(p.amount), 0) INTO v_payments
       FROM payment p
       WHERE p.customer_id = p_customer_id AND p.payment_date <= p_effective_date;


        RETURN v_total_fees - v_payments;
END $$


DELIMITER ;

DELIMITER $$


--The inventory_held_by_customer function returns the customer_id of the customer who has rented out the specified inventory item.

CREATE FUNCTION inventory_held_by_customer2(p_inventory_id INTEGER)  RETURNS INTEGER
    DETERMINISTIC
    READS SQL DATA
BEGIN

    DECLARE v_customer_id DECIMAL(10 ,2);

    SELECT r.customer_id INTO v_customer_id
    FROM rental r
    WHERE r.inventory_id = p_inventory_id AND r.return_date IS NULL;


    RETURN v_customer_id;
END $$

DELIMITER ;

DELIMITER $$

--The inventory_function function returns a boolean value indicating whether the inventory
--item specified is in stock

CREATE FUNCTION inventory_in_stock2(p_inventory_id INTEGER)  RETURNS BOOLEAN
    DETERMINISTIC
    READS SQL DATA
BEGIN

    DECLARE v_rentals INTEGER;
    DECLARE v_out INTEGER;

    SELECT COUNT(*) INTO v_rentals
    FROM rental r
    WHERE r.inventory_id = p_inventory_id;

    IF v_rentals = 0 THEN
        RETURN TRUE;
    END IF;

    SELECT COUNT(rental_id) INTO v_out
    FROM rental r
    WHERE r.inventory_id = p_inventory_id
    AND r.return_date IS NULL;

    IF v_out > 0 THEN
        RETURN FALSE;
    ELSE
      RETURN TRUE;
    END IF;

END $$

DELIMITER ;

DELIMITER $$


--The film_in_stock stored procedure determines whether any copies of a given film are in
--stock at a given store.


CREATE PROCEDURE film_in_stock2(IN p_film_id INTEGER, IN p_store_id INTEGER, OUT p_film_count INTEGER)
    READS SQL DATA
BEGIN


    SELECT inventory_id
    FROM inventory
    WHERE film_id = p_film_id AND store_id = p_store_id AND inventory_in_stock2(inventory_id);

    SELECT COUNT(*) INTO p_film_count
    FROM inventory
    WHERE film_id = p_film_id AND store_id = p_store_id AND inventory_in_stock2(inventory_id);

END $$


DELIMITER ;

DELIMITER $$

--The film_not_in_stock stored procedure determines whether there are any copies of a given
-- film not in stock (rented out) at a given store.



CREATE PROCEDURE film_not_in_stock2(IN p_film_id INTEGER, IN p_store_id INTEGER, OUT p_film_count INTEGER)
    READS SQL DATA
BEGIN


    SELECT inventory_id
    FROM inventory
    WHERE film_id = p_film_id AND store_id = p_store_id AND NOT inventory_in_stock2(inventory_id);

    SELECT COUNT(*) INTO p_film_count
    FROM inventory
    WHERE film_id = p_film_id AND store_id = p_store_id AND NOT inventory_in_stock2(inventory_id);

END $$

DELIMITER ;

DELIMITER $$


--The rewards_report stored procedure generates a customizable list of the top customers for
-- the previous month.


CREATE PROCEDURE rewards_report2(IN min_monthly_purchases INTEGER, IN min_dollar_amount_purchased DECIMAL(10, 2), OUT count_rewardees INTEGER)
    READS SQL DATA
    LANGUAGE SQL
    COMMENT 'Provides a customizable report on best customers'
proc: BEGIN

    DECLARE last_month_start DATE;
    DECLARE last_month_end DATE;

    /* Some sanity checks... */
    IF min_monthly_purchases <= 0 THEN
        SELECT 'Minimum monthly purchases parameter must be > 0';
        LEAVE proc;
    END IF;

    IF min_dollar_amount_purchased <= 0.00 THEN
        SELECT 'Minimum monthly dollar amount purchased parameter must be > $0.00';
        LEAVE proc;
    END IF;

    /* Determine start and end time periods */
    SET last_month_start = DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH);
    SET last_month_start = STR_TO_DATE(CONCAT(YEAR(last_month_start),'-',MONTH(last_month_start),'-01'),'%Y-%m-%d');
    SET last_month_end = LAST_DAY(last_month_start);


    /*
        Create a temporary storage area for
        Customer IDs.
    */
    CREATE TEMPORARY TABLE tmpCustomer LIKE customer;

    INSERT INTO tmpCustomer
    SELECT c.*
    FROM customer c
    LEFT JOIN payment p ON c.customer_id = p.customer_id
    WHERE DATE(p.payment_date) BETWEEN last_month_start AND last_month_end
    GROUP BY c.customer_id
    HAVING COUNT(c.customer_id) >= min_monthly_purchases AND SUM(p.amount) >= min_dollar_amount_purchased;
--    HAVING COUNT(c.customer_id) > min_monthly_purchases AND SUM(p.amount) > min_dollar_amount_purchased;

    /* Populate OUT parameter with count of found customers */
    SELECT COUNT(*) FROM tmpCustomer INTO count_rewardees;

    SELECT * FROM tmpCustomer;

    /* Clean up */
    DROP TABLE tmpCustomer;

END $$

DELIMITER ;


