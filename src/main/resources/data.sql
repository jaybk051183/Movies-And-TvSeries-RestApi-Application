INSERT INTO categories (id, name, available_content, price) VALUES (1, 'Nederlandse films', 10, 4.0);
INSERT INTO categories (id, name, available_content, price) VALUES (2, 'Nederlandse Serie', 20, 6.0);
INSERT INTO categories (id, name, available_content, price) VALUES (3, 'Internationale films', 15, 8.0);

INSERT INTO users (username, password, email, enabled) VALUES ('kbhaggoe1535', '$2a$12$SEWy4ggZaD24H71T9wvVo.d8.LeBUlcZPJz87BHfIwTR7yQmSzthS', 'k.bhaggoe@outlook.nl', TRUE);
INSERT INTO users (username, password, email, enabled) VALUES ('mhuizinga1353', '$2a$12$EXmJf5dYI7ZQFQe/jUbtiOk/Xp15GEgPMSiXEHpuCNQDLKdpKIfEK', 'm.huizinga@outlook.nl', TRUE);
INSERT INTO users (username, password, email, enabled) VALUES ('pbakker4542', '$2a$12$bH6AJvdfMwJyTPJP5SoneeDOYzOgtqGa65/eX2qTiMOmj0hu/jebO', 'p.bakker@outlook.nl', TRUE);

INSERT INTO subscriptions(id, user_id, category_id, start_date, payment_due_date, remaining_content, is_active, reminder_sent)
VALUES(1, 'kbhaggoe1535', 3, '2018-01-01', '2023-09-02', 5, TRUE, FALSE);

INSERT INTO authorities (username, authority) VALUES ('kbhaggoe1535', 'ROLE_USER');
INSERT INTO authorities (username, authority) VALUES ('mhuizinga1353', 'ROLE_USER');
INSERT INTO authorities (username, authority) VALUES ('pbakker4542', 'ROLE_ADMIN');

