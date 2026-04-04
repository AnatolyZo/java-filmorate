# java-filmorate
Template repository for Filmorate project.
![Схема базы данных](/src/main/resources/DB_scheme.jpg)
# Основные запросы к базе данных
## Получение всех фильмов
```sql
SELECT film_id,
       name,
       description,
       releaseDate,
       duration,
       mpa_rating
FROM films;
```

## Получение 10 наиболее популярных фильмов
```sql
SELECT film_id,
       name,
       description,
       releaseDate,
       duration,
       mpa_rating
FROM films 
WHERE film_id IN (
      SELECT film_id
      FROM film_likes
      GROUP BY film_id
      ORDER BY COUNT(user_id) DESC
      LIMIT 10);
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
SELECT user_id,
       email,
       login,
       name,
       birthday
FROM users 
WHERE user_id IN (
      SELECT friend_id,
      FROM user_friends
      WHERE user_id = 'заданное значение');
```