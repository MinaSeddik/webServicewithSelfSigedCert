
SELECT SRS_NAME, SRS_ID from  INFORMATION_SCHEMA.ST_SPATIAL_REFERENCE_SYSTEMS where SRS_ID = 3857;


CREATE DATABASE IF NOT EXISTS gps;

USE gps;

CREATE TABLE IF NOT EXISTS sterling_locations
(
    friendly_name VARCHAR(50),
    external_location_id VARCHAR(32) NOT NULL UNIQUE,
    phone_number VARCHAR(16),
    email VARCHAR(40),
    address_line1 VARCHAR(40),
    address_line2 VARCHAR(30),
    city VARCHAR(24),
    state CHAR(2),
    zip_code VARCHAR(12),
    country VARCHAR(3),
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    monday_hours_of_operation VARCHAR(24),
    tuesday_hours_of_operation VARCHAR(24),
    wednesday_hours_of_operation VARCHAR(24),
    thursday_hours_of_operation VARCHAR(24),
    friday_hours_of_operation VARCHAR(24),
    saturday_hours_of_operation VARCHAR(24),
    sunday_hours_of_operation VARCHAR(24)
);

mysql> SHOW VARIABLES LIKE "secure_file_priv";
+------------------+-----------------------+
| Variable_name    | Value                 |
+------------------+-----------------------+
| secure_file_priv | /var/lib/mysql-files/ |
+------------------+-----------------------+

sudo cp /home/mina/Desktop/sterling_location.csv /var/lib/mysql-files/sterling_location.csv
sudo chown mysql:mysql /var/lib/mysql-files/sterling_location.csv

LOAD DATA INFILE '/var/lib/mysql-files/sterling_location.csv' INTO TABLE sterling_locations
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES;


create TABLE IF NOT EXISTS sterling_locations_srs
(
    friendly_name VARCHAR(50),
    external_location_id VARCHAR(32) NOT NULL UNIQUE,
    phone_number VARCHAR(16),
    email VARCHAR(40),
    address_line1 VARCHAR(40),
    address_line2 VARCHAR(30),
    city VARCHAR(24),
    state CHAR(2),
    zip_code VARCHAR(12),
    country VARCHAR(3),

--    latitude DECIMAL(10, 7) NOT NULL,
--    longitude DECIMAL(10, 7) NOT NULL,
    position POINT NOT NULL SRID 4326,

    monday_hours_of_operation VARCHAR(24),
    tuesday_hours_of_operation VARCHAR(24),
    wednesday_hours_of_operation VARCHAR(24),
    thursday_hours_of_operation VARCHAR(24),
    friday_hours_of_operation VARCHAR(24),
    saturday_hours_of_operation VARCHAR(24),
    sunday_hours_of_operation VARCHAR(24)
);


insert into sterling_locations_srs
select friendly_name, external_location_id, phone_number, email, address_line1, address_line2, city,
    state, zip_code, country,
    ST_GeomFromText(concat('POINT(', latitude, ' ' , longitude, ')'), 4326),
    monday_hours_of_operation, tuesday_hours_of_operation,
    wednesday_hours_of_operation, thursday_hours_of_operation, friday_hours_of_operation,
    saturday_hours_of_operation, sunday_hours_of_operation
    from sterling_locations;

-- Hilton San Diego Bayfront 32.70405577169982, -117.157665913614
SET @hilton_sanDiego_latitude := 32.70405577169982;
SET @hilton_sanDiego_longtude :=  -117.157665913614;

select @hilton_sanDiego_latitude AS lat_hilton_sanDiego,
    @hilton_sanDiego_longtude AS long_hilton_sanDiego,
    latitude AS lat_location,
    longitude AS long_location,
    -- 6371 in KM
    ( 6371 * acos( cos( radians(@hilton_sanDiego_latitude) ) * cos( radians( latitude ) ) *
    cos( radians( longitude ) - radians(@hilton_sanDiego_longtude) ) +
    sin( radians(@hilton_sanDiego_latitude) ) * sin(radians(latitude)) ) ) AS distance_In_KM
    FROM sterling_locations;

select city, state, address_line1,
    ( 6371 * acos( cos( radians(@hilton_sanDiego_latitude) ) * cos( radians( latitude ) ) *
    cos( radians( longitude ) - radians(@hilton_sanDiego_longtude) ) +
    sin( radians(@hilton_sanDiego_latitude) ) * sin(radians(latitude)) ) )AS distance
    FROM sterling_locations
    WHERE ( 6371 * acos( cos( radians(@hilton_sanDiego_latitude) ) * cos( radians( latitude ) ) *
    cos( radians( longitude ) - radians(@hilton_sanDiego_longtude) ) +
    sin( radians(@hilton_sanDiego_latitude) ) * sin(radians(latitude)) ) ) <= 50.00
    ORDER BY distance;



-- Hilton San Diego Bayfront 32.70405577169982, -117.157665913614
SET @hilton_sanDiego := ST_GeomFromText( 'POINT(32.70405577169982 -117.157665913614)', 4326);

select ST_Latitude( @hilton_sanDiego ) AS lat_hilton_sanDiego,
    ST_Longitude( @hilton_sanDiego ) AS long_hilton_sanDiego,
    ST_Latitude( position ) AS lat_location,
    ST_Longitude( position ) AS long_location,
    ST_Distance_Sphere( @hilton_sanDiego, position ) AS distance
    FROM sterling_locations_srs;

select ST_Latitude( @hilton_sanDiego ) AS lat_hilton_sanDiego,
    ST_Longitude( @hilton_sanDiego ) AS long_hilton_sanDiego,
    ST_Latitude( position ) AS lat_location,
    ST_Longitude( position ) AS long_location,
    ROUND(ST_Distance_Sphere( @hilton_sanDiego, position ) / 1000, 2) AS distance,
    CONCAT( ROUND(ST_Distance_Sphere( @hilton_sanDiego, position ), 2), ' METERS') AS meters,
    CONCAT( ROUND(ST_Distance_Sphere( @hilton_sanDiego, position ) / 1000, 2), ' Km') AS Km
    FROM sterling_locations_srs l
    WHERE external_location_id IN('TUPSS0007', 'TUPSS0090', 'TUPSS0400', 'TUPSS1841', 'TUPSS6767');

select city, state, address_line1,
    round(ST_Distance_Sphere( @hilton_sanDiego, position ) / 1000, 2) AS distance,
    CONCAT( ROUND(ST_Distance_Sphere( @hilton_sanDiego, position ), 2), ' METERS') AS meters,
    CONCAT( ROUND(ST_Distance_Sphere( @hilton_sanDiego, position ) / 1000, 2), ' Km') AS Km
    FROM sterling_locations_srs
    WHERE ROUND(ST_Distance_Sphere( @hilton_sanDiego, position ) / 1000, 2) <= 50.00
    ORDER BY distance;



-- =======================================================================================================

create TABLE IF NOT EXISTS us_zip_locations
(
    zip_code VARCHAR(5) NOT NULL,
    city VARCHAR(32),
    country VARCHAR(32),
    state CHAR(2),
    county_fips SMALLINT UNSIGNED,
    state_fips TINYINT UNSIGNED,
    timezone TINYINT UNSIGNED,
    daylight_savings CHAR(1),
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL
);

sudo cp /home/mina/Desktop/USZIPCodes202303.csv /var/lib/mysql-files/USZIPCodes202303.csv
sudo chown mysql:mysql /var/lib/mysql-files/USZIPCodes202303.csv

LOAD DATA INFILE '/var/lib/mysql-files/USZIPCodes202303.csv' INTO TABLE us_zip_locations
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES
(zip_code, city, country, state, @cfips, @sfips, @tz, @ds, @lat, @longt)
SET county_fips = NULLIF(@cfips, ''),
state_fips = NULLIF(@sfips, ''),
timezone = NULLIF(@tz, ''),
daylight_savings = NULLIF(@ds, ''),
latitude = IF(@lat = '', 0.0,CAST(@lat as DECIMAL(10, 7))),
longitude = IF(@longt = '', 0.0,CAST(@longt as DECIMAL(10, 7)));


create TABLE IF NOT EXISTS us_zip_locations_srs
(
    zip_code VARCHAR(5) NOT NULL,
    city VARCHAR(32),
    country VARCHAR(32),
    state CHAR(2),
    county_fips SMALLINT UNSIGNED,
    state_fips TINYINT UNSIGNED,
    timezone TINYINT UNSIGNED,
    daylight_savings CHAR(1),

--    latitude DECIMAL(10, 7) NOT NULL,
--    longitude DECIMAL(10, 7) NOT NULL
    position POINT NOT NULL SRID 4326
);


insert into us_zip_locations_srs
select zip_code, city, country, state, county_fips, state_fips, timezone, daylight_savings,
    ST_GeomFromText(concat('POINT(', latitude, ' ' , longitude, ')'), 4326)
    from us_zip_locations;

-- Hilton San Diego Bayfront 32.70405577169982, -117.157665913614
SET @hilton_sanDiego := ST_GeomFromText( 'POINT(32.70405577169982 -117.157665913614)', 4326);

select city, country, state,
    round(ST_Distance_Sphere( @hilton_sanDiego, position ) / 1000, 2) AS distance,
    CONCAT( ROUND(ST_Distance_Sphere( @hilton_sanDiego, position ), 2), ' METERS') AS meters,
    CONCAT( ROUND(ST_Distance_Sphere( @hilton_sanDiego, position ) / 1000, 2), ' Km') AS Km
    FROM us_zip_locations_srs
    WHERE ROUND(ST_Distance_Sphere( @hilton_sanDiego, position ) / 1000, 2) <= 50.00
    ORDER BY distance;

select city, country, state,
    round(ST_Distance_Sphere( @hilton_sanDiego, position ) / 1000, 2) AS distance
    FROM us_zip_locations_srs
    WHERE ROUND(ST_Distance_Sphere( @hilton_sanDiego, position ) / 1000, 2) <= 50.00
    ORDER BY distance;

-- =======================================================================================================


create TABLE IF NOT EXISTS ca_zip_locations
(
    postal_code CHAR(7) NOT NULL,
    city VARCHAR(32),
    province CHAR(2),
    timezone TINYINT UNSIGNED,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL
);

sudo cp /home/mina/Desktop/CanadianPostalCodes202303.csv /var/lib/mysql-files/CanadianPostalCodes202303.csv
sudo chown mysql:mysql /var/lib/mysql-files/CanadianPostalCodes202303.csv


LOAD DATA INFILE '/var/lib/mysql-files/CanadianPostalCodes202303.csv' INTO TABLE ca_zip_locations
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES
(postal_code, city, province, timezone, @lat, @longt)
SET latitude = CAST(@lat as DECIMAL(10, 7)),
longitude = CAST(@longt as DECIMAL(10, 7));

create TABLE IF NOT EXISTS ca_zip_locations_srs
(
    postal_code CHAR(7) NOT NULL,
    city VARCHAR(32),
    province CHAR(2),
    timezone TINYINT UNSIGNED,

--    latitude DECIMAL(10, 7) NOT NULL,
--    longitude DECIMAL(10, 7) NOT NULL
    position POINT NOT NULL SRID 4326
);


insert into ca_zip_locations_srs
select postal_code, city, province, timezone,
    ST_GeomFromText(concat('POINT(', latitude, ' ' , longitude, ')'), 4326)
    from ca_zip_locations;

-- Hochelaga/Davidson, Montreal 45.54510224065946, -73.55275300390369
SET @home := ST_GeomFromText( 'POINT(45.54510224065946 -73.55275300390369)', 4326);

select postal_code, city, province,
    round(ST_Distance_Sphere( @home, position ) / 1000, 2) AS distance,
    CONCAT( ROUND(ST_Distance_Sphere( @home, position ), 2), ' METERS') AS meters,
    CONCAT( ROUND(ST_Distance_Sphere( @home, position ) / 1000, 2), ' Km') AS Km
    FROM ca_zip_locations_srs
    WHERE ROUND(ST_Distance_Sphere( @home, position ) / 1000, 2) <= 10.00
    ORDER BY distance;

select postal_code, city, province,
    round(ST_Distance_Sphere( @home, position ) / 1000, 2) AS distance
    FROM ca_zip_locations_srs
    WHERE ROUND(ST_Distance_Sphere( @home, position ) / 1000, 2) <= 2
    ORDER BY distance;

-- the above query is so expensive as it does a full-table scan
explain select postal_code, city, province,
    round(ST_Distance_Sphere( @home, position ) / 1000, 2) AS distance
    FROM ca_zip_locations_srs
    WHERE ROUND(ST_Distance_Sphere( @home, position ) / 1000, 2) <= 2
    ORDER BY distance;


-- let's create another table with Spatial index

create TABLE IF NOT EXISTS ca_zip_locations_srs_with_index
(
    postal_code CHAR(7) NOT NULL,
    city VARCHAR(32),
    province CHAR(2),
    timezone TINYINT UNSIGNED,

--    latitude DECIMAL(10, 7) NOT NULL,
--    longitude DECIMAL(10, 7) NOT NULL
    position POINT NOT NULL SRID 4326,
    SPATIAL INDEX(`position`)
);


insert into ca_zip_locations_srs_with_index
select * from ca_zip_locations_srs;


select postal_code, city, province,
    round(ST_Distance_Sphere( @home, position ) / 1000, 2) AS distance
    FROM ca_zip_locations_srs_with_index
    WHERE ROUND(ST_Distance_Sphere( @home, position ) / 1000, 2) <= 20
    ORDER BY distance;
-- Result set: 63450 rows in set (5.51 sec)


-- the above query is so expensive as it does a full-table scan, we can check it here
explain SELECT postal_code, city, province,
    round(ST_Distance_Sphere( @home, position ) / 1000, 2) AS distance
    FROM ca_zip_locations_srs_with_index
    WHERE ROUND(ST_Distance_Sphere( @home, position ) / 1000, 2) <= 20
    ORDER BY distance;

-- using ST_Buffer instead

SET @searched_area := ST_Buffer( @home, 20 * 1000); -- it draws polygon from the point @home of length 20 * 1000
SELECT ST_AsText(@searched_area);
SELECT ST_AsText(ST_GeometryFromText(@searched_area));

-- mysql will not use the index because of the Right side =1 in ST_Within
SELECT postal_code, city, province,
    round(ST_Distance_Sphere( @home, position ) / 1000, 2) AS distance
    FROM ca_zip_locations_srs_with_index
    WHERE ST_Within( position, ST_Buffer( @home, 20) ) = 1
    ORDER BY distance;

-- It's perfectly safe to remove the "1"
SELECT postal_code, city, province,
    round(ST_Distance_Sphere( @home, position ) / 1000, 2) AS distance
    FROM ca_zip_locations_srs_with_index
    WHERE ST_Within( position, ST_Buffer( @home, 20 * 1000 ) )
    ORDER BY distance;
-- Result set: 63136 rows in set (2.99 sec)

SELECT postal_code, city, province,
    round(ST_Distance_Sphere( @home, position ) / 1000, 2) AS distance
    FROM ca_zip_locations_srs_with_index
    WHERE ST_Within( position, ST_Buffer( @home, 21 * 1000 ) ) -- we use 21 instead of 20 to widen the searched range
    AND ROUND(ST_Distance_Sphere( @home, position ) / 1000, 2) <= 20
    ORDER BY distance;
-- Result set: 63450 rows in set (3.41 sec)

-- Can we do better ?

SELECT postal_code, city, province,
    round(ST_Distance_Sphere( @home, position ) / 1000, 2) AS distance
    FROM ca_zip_locations_srs_with_index
    WHERE ST_Within( position, ST_Buffer( @home, 21 * 1000 ) ) -- we use 21 instead of 20 to widen the searched range
    having distance <= 20
    ORDER BY distance;
-- 63450 rows in set (3.31 sec)


-- just for testing
-- The distance between these two places is 7.21km or 7210 meters.
SET @lotus_temple := ST_GeomFromText( 'POINT(28.553298 77.259221)', 4326);
SET @india_gate := ST_GeomFromText( 'POINT(28.612849 77.229883)', 4326 );


SELECT ST_Distance_Sphere( @lotus_temple, @india_gate ) AS distance;

mysql> SELECT ST_Distance_Sphere( @lotus_temple, @india_gate ) AS distance;
+-------------------+
| distance          |
+-------------------+
| 7214.858599756361 |
+-------------------+
1 row in set (0.00 sec)


