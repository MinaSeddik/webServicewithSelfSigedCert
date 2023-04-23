

-- in myapp database

CREATE TABLE event(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    action_type varchar(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    username varchar(255) NOT NULL,
    source json,
    details json
);