CREATE DATABASE my_spatial_db
ENCODING 'UTF8'
LC_COLLATE = 'en_US.UTF-8'
LC_CTYPE = 'en_US.UTF-8'
TEMPLATE template0;

\connect my_spatial_db
CREATE EXTENSION postgis;

create TABLE ca_zip_locations
(
    postal_code CHAR(7) NOT NULL,
    city VARCHAR(32),
    province CHAR(2),
    timezone INT,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL
);


COPY ca_zip_locations(postal_code,city,province,timezone,latitude, longitude)
FROM '/home/mina/Desktop/CanadianPostalCodes202303.csv'
DELIMITER ','
CSV HEADER;



CREATE TABLE ca_zip_locations_srs
(
    postal_code CHAR(7) NOT NULL,
    city VARCHAR(32),
    province CHAR(2),
    timezone INT,
    position geometry(Point, 4326)
);

CREATE INDEX spatial_index
ON ca_zip_locations_srs
USING gist (position);

SELECT COUNT(*) FROM ca_zip_locations_srs;


INSERT INTO ca_zip_locations_srs (postal_code,city,province,timezone, position)
SELECT postal_code,city,province,timezone, ST_SetSRID(ST_MakePoint(longitude, latitude), 4326) FROM ca_zip_locations;

-- to display Whole Results
\pset pager off

--ST_GeomFromText('POINT(-73.55275300390369 45.54510224065946)',4326)
-- this is the long/lat of Hochelaga/Davidson Montreal
SET MyHome := ST_MakePoint(-73.55275300390369, 45.54510224065946)
-- OR
SET MyHome := ST_SetSRID(ST_MakePoint(-73.55275300390369, 45.54510224065946), 4326)

SELECT postal_code, city, province,
    ROUND(CAST(ST_DistanceSphere(position, ST_SetSRID(ST_MakePoint(-73.55275300390369, 45.54510224065946), 4326)) As numeric),2) As distance_in_Meters
    FROM ca_zip_locations_srs
    WHERE ST_DWithin(position::geography, ST_SetSRID(ST_MakePoint(-73.55275300390369, 45.54510224065946)::geography, 4326), 20000.00)
    ORDER BY distance_in_Meters;


SELECT postal_code, city, province,
    ROUND(CAST(ST_DistanceSphere(position, ST_SetSRID(ST_MakePoint(-73.55275300390369, 45.54510224065946), 4326)) / 1000 As numeric),2) As distance_in_KM
    FROM ca_zip_locations_srs
    WHERE ST_DWithin(position::geography, ST_SetSRID(ST_MakePoint(-73.55275300390369, 45.54510224065946)::geography, 4326), 20000.00)
    ORDER BY distance_in_KM;


-- just for testing
-- The distance between these two places is 7.21km or 7210 meters.
SET @lotus_temple := ST_MakePoint(77.259221, 28.553298)
SET @india_gate   := ST_MakePoint(77.229883 28.612849)

SELECT ST_Distance_Sphere(ST_MakePoint(77.259221, 28.553298),ST_MakePoint(77.229883, 28.612849));
my_spatial_db=# SELECT ST_DistanceSphere(ST_MakePoint(77.259221, 28.553298),ST_MakePoint(77.229883, 28.612849));
 st_distancesphere
-------------------
     7214.85859976


-- expected: 7214.858599756361




-- ADVANCED SRS TOPIC
-- for more accurate results
https://gis.stackexchange.com/questions/133450/st-distance-values-in-kilometers



SELECT  postal_code, city, province,
        ST_Distance(
            position,
            ST_Transform(
                ST_GeomFromText(
                    'POINT(-73.55275300390369 45.54510224065946)',
                    4326),
                3978)) / 1000.0 as km --- 3978 Canada SRS
FROM ca_zip_locations_srs2
WHERE   ST_DWithin(
            position,
            ST_Transform(
                ST_GeomFromText(
                    'POINT(-73.55275300390369 45.54510224065946)',
                    4326),
                3978),
            50000)
ORDER BY ST_Distance(
            position,
            ST_Transform(
                ST_GeomFromText(
                    'POINT(-73.55275300390369 45.54510224065946)',
                    4326),
                3978));

