CREATE TABLE common_user (
    `login` VARCHAR(150) NOT NULL,
    `password` VARCHAR(512) DEFAULT NULL,
    `full_name` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`login`)
);

CREATE TABLE common_user_role (
    `user_role_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `login` VARCHAR(150) NOT NULL,
    `role_code` VARCHAR(150) NOT NULL,
    PRIMARY KEY (`user_role_id`),
    UNIQUE KEY `uk_userrole` (`login`,`role_code`),
    CONSTRAINT `fk_user` FOREIGN KEY (`login`) REFERENCES `common_user` (`login`)
);

INSERT INTO common_user (login, password, full_name) VALUES ('admin', 'admin', 'Administrador');
INSERT INTO common_user (login, password, full_name) VALUES ('user1', 'user1', 'Usuario 01');
INSERT INTO common_user (login, password, full_name) VALUES ('user2', 'user2', 'Usuario 02');
INSERT INTO common_user (login, password, full_name) VALUES ('user3', 'user3', 'Usuario 03');
INSERT INTO common_user (login, password, full_name) VALUES ('user4', 'user4', 'Usuario 04');
INSERT INTO common_user (login, password, full_name) VALUES ('user5', 'user5', 'Usuario 05');
INSERT INTO common_user_role (login, role_code) VALUES ('admin', 'admin');
INSERT INTO common_user_role (login, role_code) VALUES ('admin', 'user');
INSERT INTO common_user_role (login, role_code) VALUES ('user1', 'user');
INSERT INTO common_user_role (login, role_code) VALUES ('user2', 'user');
INSERT INTO common_user_role (login, role_code) VALUES ('user3', 'user');
INSERT INTO common_user_role (login, role_code) VALUES ('user4', 'user');
INSERT INTO common_user_role (login, role_code) VALUES ('user5', 'user');
