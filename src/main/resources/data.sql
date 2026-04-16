INSERT INTO genres (genre)
SELECT genre
FROM (
    VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик')
) AS temp_genres(genre)
WHERE NOT EXISTS (
    SELECT 1 FROM genres g WHERE g.genre = temp_genres.genre
);

INSERT INTO ratings (rating)
SELECT rating
FROM (
    VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17')
) AS temp_ratings(rating)
WHERE NOT EXISTS (
    SELECT 1 FROM ratings g WHERE g.rating = temp_ratings.rating
);