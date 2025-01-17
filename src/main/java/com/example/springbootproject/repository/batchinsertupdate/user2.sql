

CREATE TABLE user2
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

    date_of_birth DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

