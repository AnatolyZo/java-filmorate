# java-filmorate
Template repository for Filmorate project.
![Схема базы данных](/src/main/resources/DB_scheme.jpg)
# Основные запросы к базе данных
## Получение всех фильмов
```sql
SELECT
     f.film_id
     f.name
     f.description
     f.release_date
     f.duration
     r.rating_id AS mpa_id
     r.rating AS mpa_name
     GROUP_CONCAT(CONCAT(g.genre_id, ' ', g.genre) ORDER BY g.genre_id SEPARATOR ', ') AS genre_list
FROM films f
JOIN ratings AS r ON f.rating_id = r.rating_id
LEFT JOIN films_genres fg ON f.film_id = fg.film_id
LEFT JOIN genres g ON fg.genre_id = g.genre_id
GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id;
```

## Получение N наиболее популярных фильмов
```sql
SELECT
    f.film_id
    f.name
    f.description
    f.release_date
    f.duration
    r.rating_id AS mpa_id
    r.rating AS mpa_name
    GROUP_CONCAT(CONCAT(g.genre_id, ' ', g.genre) ORDER BY g.genre_id SEPARATOR ', ') AS genre_list
FROM films AS f
JOIN ratings AS r ON f.rating_id = r.rating_id
LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id
LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
JOIN (
   SELECT film_id,
          COUNT(user_id) AS likes
          FROM films_likes
          GROUP BY film_id
          ORDER BY likes
          LIMIT ?) AS popular_films ON f.film_id = popular_films.film_id
GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id
ORDER BY popular_films.likes DESC;
```

## Получение всех пользователей
```sql
SELECT user_id,
       email,
       login,
       name,
       birthday
FROM users;
```

## Получение списка друзей пользователя
```sql
SELECT *
FROM users
WHERE user_id IN (
    SELECT friend_id
    FROM users_friends
    WHERE user_id = ?);
```