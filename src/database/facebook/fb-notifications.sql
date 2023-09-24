

-- Giving the following tables
CREATE TABLE action
(
action_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
action_code VARCHAR(20) NOT NULL


action_code
-----------
ADD_POST
UPDATE_POST
DELETE_POST
LIKE_POST
MENTIONED_IN_POST
ADD_COMMENT
UPDATE_COMMENT
DELETE_COMMENT
LIKE_COMMENT
MENTIONED_IN_COMMENT
CREATE_EVENT
UPDATE_EVENT
HAS_BIRTHDAY
....
);

CREATE TABLE notification
(
notification_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
receiver_id BIGINT UNSIGNED NOT NULL,   -- FK to user who will receive the notification
actor_id BIGINT UNSIGNED NOT NULL,      -- FK to user who did the action
actor_type ENUM('USER', 'GROUP') NOT NULL,
action_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
action_id BIGINT UNSIGNED NOT NULL,   -- FK to action
object_id BIGINT UNSIGNED DEFAULT NULL,
object_type ENUM('POST', 'COMMENT', 'EVENT', ...)
);


-- (1) Handle add post
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE add_post_notification(IN p_user_id BIGINT, IN p_post_id BIGINT)
    READS SQL DATA
BEGIN

    DECLARE v_action_id BIGINT;

    -- get action_id of add post
    SELECT action_id INTO v_action_id FROM action WHERE action_code = 'ADD_POST';

    INSERT INTO notification (receiver_id, actor_id, actor_type, action_id, object_id, object_type)
    SELECT f.friend_id, p_user_id, 'USER', v_action_id, p_post_id, 'POST'
    FROM friends f
    WHERE f.user_id = p_user_id AND status = 'ACTIVE';

END $$








