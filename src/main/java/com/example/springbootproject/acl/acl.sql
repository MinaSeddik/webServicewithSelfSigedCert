
-- Reference: https://stackoverflow.com/questions/16139712/how-to-design-a-hierarchical-role-based-access-control-system?rq=1

CREATE DATABASE acl;

CREATE TABLE permission
(
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) NOT NULL, -- or module_name
    label VARCHAR(255) NOT NULL,
    desc VARCHAR(255),
    active BOOL NOT NULL DEFAULT true   -- optional field
);

CREATE TABLE role
(
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    label VARCHAR(255) NOT NULL,
    desc VARCHAR(255),
    active BOOL NOT NULL DEFAULT true   -- optional field
);

CREATE TABLE role_permission
(
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role_id  INTEGER NOT NULL,
    permission_id  INTEGER NOT NULL,
    grant BOOL NOT NULL DEFAULT false,   -- 0 = deny, 1 = grant

    CONSTRAINT FK_role FOREIGN KEY (role_id) REFERENCES role(id),
    CONSTRAINT FK_permission FOREIGN KEY (permission_id) REFERENCES permission(id)
);

CREATE TABLE user
(
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(255),
    active BOOL NOT NULL DEFAULT true,
    locked BOOL NOT NULL DEFAULT false,

    role_id INTEGER NOT NULL,
    CONSTRAINT FK_user_role FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE user_permission
(
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    permission_id INTEGER NOT NULL,
    grant BOOL NOT NULL DEFAULT false,   -- 0 = deny, 1 = grant

    CONSTRAINT FK_user_permission FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT FK_user_permission FOREIGN KEY (permission_id) REFERENCES permission(id)
);


-------------------------------------------------------------------------------


CREATE FUNCTION `getHierarchy`(`aRole` BIGINT UNSIGNED)
RETURNS VARCHAR(1024)
NOT DETERMINISTIC
READS SQL DATA
BEGIN
DECLARE `aResult` VARCHAR(1024) DEFAULT NULL;
DECLARE `aParent` BIGINT UNSIGNED;

SET `aParent` = (SELECT `parent` FROM `Roles` WHERE `id` = `aRole`);

WHILE NOT `aParent` IS NULL DO

    SET `aResult` = CONCAT_WS(',', `aResult`, `aParent`);
    SET `aParent` = (SELECT `parent` FROM `Roles` WHERE `id` = `aParent`);

END WHILE;

RETURN IFNULL(`aResult`, '');
END
-------------------------------------------------------------------------------


--Then, you might obtain all granted permissions with something like this:
SELECT `permission_id` FROM `Permission_Role`
    WHERE FIND_IN_SET(`role_id`, `getHierarchy`({$role})) AND grant;


-------------------------------------------------------------------------------
