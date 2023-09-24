
DROP DATABASE IF EXISTS taxonomy;

CREATE DATABASE IF NOT EXISTS taxonomy;

USE taxonomy;

CREATE TABLE species
(
id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
path VARCHAR(500) NOT NULL UNIQUE,
is_group TINYINT(1) NOT NULL  -- true for groups having children, and false for leaf species
);

INSERT INTO species (path, is_group) VALUES
('/plants', 1),
('/animals', 1),
('/birds', 1),
('/animals/primates', 1),
('/animals/primates/humanoids', 0),
('/animals/primates/champanzes', 0),
('/animals/birds/parrots', 0);


select count(*) from species where path like '/animals/primates/%' and is_group=false;

select * from species where '/animals/primates' LIKE path||'%';
select * from species where '/animals/primates' LIKE path||'%';