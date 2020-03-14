CREATE USER admin WITH PASSWORD 'admin';
CREATE DATABASE wst1_db;
GRANT ALL PRIVILEGES ON DATABASE wst1_db TO admin;
\c wst1_db admin;
CREATE TABLE users
(
  id            BIGSERIAL PRIMARY KEY,
  login         VARCHAR(255),
  password      VARCHAR(255),
  email         VARCHAR(255),
  gender        BOOLEAN,
  register_date   DATE
);

INSERT INTO users (login, password, email, gender, register_date)
    VALUES ('abc', '1234', 'abc@mail.ru', true, '1998.01.01');
INSERT INTO users (login, password, email, gender, register_date)
    VALUES ('def', '1234', 'def@mail.ru', true, '1998.02.02');
INSERT INTO users (login, password, email, gender, register_date)
    VALUES ('ghi', 'qwerty', 'ghi@mail.ru', true, '1998.03.03');