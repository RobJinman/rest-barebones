INSERT INTO rl.salt (salt, ts) VALUES (GEN_SALT('bf'), CURRENT_TIMESTAMP);
INSERT INTO rl.salt (salt, ts) VALUES (GEN_SALT('bf'), CURRENT_TIMESTAMP);

INSERT INTO rl.config (key, value) VALUES ('Mailer.address', 'example@email.com');
INSERT INTO rl.config (key, value) VALUES ('Mailer.password', 'mypassword');
INSERT INTO rl.config (key, value) VALUES ('Mailer.smtp.auth', 'true');
INSERT INTO rl.config (key, value) VALUES ('Mailer.smtp.starttls.enable', 'true');
INSERT INTO rl.config (key, value) VALUES ('Mailer.smtp.host', 'smtp.gmail.com');
INSERT INTO rl.config (key, value) VALUES ('Mailer.smtp.port', 587);

