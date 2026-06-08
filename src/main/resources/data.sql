INSERT INTO users (email, login, name, birthday) VALUES
('user1@yandex.ru', 'user1_login', 'Ivan Ivanov', '1990-01-01'),
('user2@gmail.com', 'user2_login', 'Petr Petrov', '1995-05-15'),
('friend_forever@mail.ru', 'best_friend', 'Alexey Smirnov', '2000-12-31'),
('ff@mail.ru', 'user3_friend', 'Mikhail Smirnov', '2000-12-31');

INSERT INTO mpa (id, name)
VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES
('The Matrix', 'A computer hacker learns about the true nature of his reality.', '1999-03-31', 136, 4),
('Interstellar', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity survival.', '2014-11-07', 169, 2),
('Inception', 'A thief who steals corporate secrets through the use of dream-sharing technology.', '2010-07-16', 148, 3);

INSERT INTO user_friends (user_id, friend_id)
VALUES
(1, 2),
(1, 3),
(4, 3);

INSERT INTO film_likes (film_id, user_id)
VALUES
(1, 1),
(1, 2),
(1, 3);

INSERT INTO genres (id, name)
VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');