
--https://vertabelo.com/blog/creating-a-data-warehouse-part-3-a-subscription-business-data-model/


--First, we’ll populate dimension tables with new values (where needed).
--After that, we’ll populate fact tables.

--The general idea is to create stored procedures that we could regularly use to populate the DWH dimension
--tables as well as fact tables

--Dimension tables
--=================
--- dim_time
--- dim_city
--- dim_product
--- dim_delivery_status



--we’ll run this procedure daily, after the close of business (COB)


--As mentioned before, I’ll go with the assumption that we’ll run the DWH import exactly once a day.
--If that’s not the case, we’d need additional code to delete newly-inserted data from the dimension and fact tables.
--For the dimension tables, this would be limited to deleting the given date.


--I would definitely recommend is placing the whole process inside a transaction. That would ensure that either all
--insertions succeed or none are made.



DELIMITER //

DROP PROCEDURE IF EXISTS p_update_dimensions //

CREATE PROCEDURE p_update_dimensions ()
BEGIN

    SET @time_exists = 0;
    SET @time_date = DATE_ADD(DATE(NOW()), INTERVAL -1 DAY);
    -- procedure populates dimension tables with new values

    -- dim_time
    SET @time_exists = (
                        SELECT COUNT(*)
                        FROM subscription_dwh.dim_time dim_time
                        WHERE dim_time.time_date = @time_date
                        );


    IF (@time_exists = 0) THEN

        INSERT INTO subscription_dwh.`dim_time`(`time_date`, `time_year`, `time_month`, `time_week`, `time_weekday`, `ts`)
        SELECT
            @time_date AS time_date,
            YEAR(@time_date) AS time_year,
            MONTH(@time_date) AS time_month,
            WEEK(@time_date) AS time_week,
            WEEKDAY(@time_date) AS time_weekday,
            NOW() AS ts;

    END IF;



    -- dim_city
    INSERT INTO subscription_dwh.`dim_city`(`city_name`, `postal_code`, `country_name`, `ts`)
    SELECT
        city_live.city_name,
        city_live.postal_code,
        country_live.country_name,
        Now()
    FROM subscription_live.city city_live
    INNER JOIN subscription_live.country country_live
        ON city_live.country_id = country_live.id
    LEFT JOIN subscription_dwh.dim_city city_dwh
        ON city_live.city_name = city_dwh.city_name
        AND city_live.postal_code = city_dwh.postal_code
        AND country_live.country_name = city_dwh.country_name
    WHERE city_dwh.id IS NULL;



END //


DELIMITER ;



-- CALL p_update_dimensions();

DELIMITER //

DROP PROCEDURE IF EXISTS p_update_facts //


CREATE PROCEDURE p_update_facts ()
BEGIN

    SET @time_date = DATE_ADD(DATE(NOW()), INTERVAL -1 DAY);

    -- procedure populates fact tables with new values


    -- fact_customer_subscribed
    INSERT INTO `fact_customer_subscribed`(`dim_city_id`, `dim_time_id`, `total_active`, `total_inactive`, `daily_new`, `daily_canceled`, `ts`)
    SELECT
        city_dwh.id AS dim_ctiy_id,
        time_dwh.id AS dim_time_id,
        SUM(CASE WHEN customer_live.active = 1 THEN 1 ELSE 0 END) AS total_active,
        SUM(CASE WHEN customer_live.active = 0 THEN 1 ELSE 0 END) AS total_inactive,
        SUM(CASE WHEN customer_live.active = 1 AND DATE(customer_live.time_updated) = @time_date THEN 1 ELSE 0 END) AS daily_new,
        SUM(CASE WHEN customer_live.active = 0 AND DATE(customer_live.time_updated) = @time_date THEN 1 ELSE 0 END) AS daily_canceled,
        MIN(NOW()) AS ts
    FROM subscription_live.`customer` customer_live
    INNER JOIN subscription_live.`city` city_live ON customer_live.city_id = city_live.id
    INNER JOIN subscription_live.`country` country_live ON city_live.country_id = country_live.id
    INNER JOIN subscription_dwh.dim_city city_dwh
        ON city_live.city_name = city_dwh.city_name
        AND city_live.postal_code = city_dwh.postal_code
        AND country_live.country_name = city_dwh.country_name
    INNER JOIN subscription_dwh.dim_time time_dwh ON time_dwh.time_date = @time_date
    GROUP BY
        city_dwh.id,
        time_dwh.id;



    -- fact_subscription_status
    INSERT INTO `fact_subscription_status`(`dim_city_id`, `dim_time_id`, `total_active`, `total_inactive`, `daily_new`, `daily_canceled`, `ts`)
    SELECT
        city_dwh.id AS dim_ctiy_id,
        time_dwh.id AS dim_time_id,
        SUM(CASE WHEN subscription_live.active = 1 THEN 1 ELSE 0 END) AS total_active,
        SUM(CASE WHEN subscription_live.active = 0 THEN 1 ELSE 0 END) AS total_inactive,
        SUM(CASE WHEN subscription_live.active = 1 AND DATE(subscription_live.time_updated) = @time_date THEN 1 ELSE 0 END) AS daily_new,
        SUM(CASE WHEN subscription_live.active = 0 AND DATE(subscription_live.time_updated) = @time_date THEN 1 ELSE 0 END) AS daily_canceled,
        MIN(NOW()) AS ts
    FROM subscription_live.`customer` customer_live
    INNER JOIN subscription_live.`subscription` subscription_live ON subscription_live.customer_id = customer_live.id
    INNER JOIN subscription_live.`city` city_live ON customer_live.city_id = city_live.id
    INNER JOIN subscription_live.`country` country_live ON city_live.country_id = country_live.id
    INNER JOIN subscription_dwh.dim_city city_dwh
        ON city_live.city_name = city_dwh.city_name
        AND city_live.postal_code = city_dwh.postal_code
        AND country_live.country_name = city_dwh.country_name
    INNER JOIN subscription_dwh.dim_time time_dwh ON time_dwh.time_date = @time_date
    GROUP BY
        city_dwh.id,
        time_dwh.id;


END //


DELIMITER ;


-- CALL p_update_facts();





