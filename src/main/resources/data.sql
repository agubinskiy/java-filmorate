MERGE INTO Genres (name) KEY (name)
VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

MERGE INTO Rates (name) KEY (name)
VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');