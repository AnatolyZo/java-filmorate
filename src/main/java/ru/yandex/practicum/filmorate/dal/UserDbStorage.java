package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    public static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    public static final String INSERT_USER_QUERY = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    //Добавляем пару пользователь-друг при условии существования id друга. Если зеркальной пары нет,
    //то индикатор дружбы - ложь, если есть - истина. В случае истинности обновляем индикатор зеркальной пары.
    public static final String ADD_FRIEND_QUERY = "INSERT INTO users_friends (user_id, friend_id, friendship_confirm)\n" +
            "        SELECT ?, ?,\n" +
            "            CASE\n" +
            "                WHEN (SELECT COUNT(*) FROM users_friends WHERE user_id = ? AND friend_id = ?) > 0\n" +
            "                THEN true\n" +
            "                ELSE false\n" +
            "           END\n" +
            "        WHERE EXISTS (SELECT 1 FROM users WHERE user_id = ?);\n" +
            "\n" +
            "UPDATE users_friends\n" +
            "SET friendship_confirm = CASE\n" +
            "    WHEN EXISTS (SELECT 1 FROM users_friends WHERE user_id = ? AND friend_id = ?) THEN TRUE\n" +
            "    ELSE FALSE\n" +
            "END\n" +
            "WHERE user_id = ?\n" +
            "  AND friend_id = ?";
    public static final String GET_USERS_FRIENDS_QUERY = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM users_friends WHERE user_id = ?)";
    public static final String DELETE_FRIEND_QUERY = "DELETE FROM users_friends WHERE user_id = ? AND friend_id = ?";
    //Обновление статуса дружбы на ложь при удалении зеркальной пары
    public static final String UPDATE_STATUS_AFTER_DELETE_QUERY = "UPDATE users_friends\n" +
            "SET friendship_confirm = CASE\n" +
            "    WHEN EXISTS (SELECT 1 FROM users_friends WHERE user_id = ? AND friend_id = ?) THEN FALSE\n" +
            "    ELSE TRUE\n" +
            "END\n" +
            "WHERE user_id = ?\n" +
            "  AND friend_id = ?";
    public static final String GET_COMMON_FRIENDS_QUERY = "SELECT u.*\n" +
            "FROM users u\n" +
            "INNER JOIN (\n" +
            "    SELECT friend_id\n" +
            "    FROM users_friends\n" +
            "    WHERE user_id = ?\n" +
            ") AS f1 ON u.user_id = f1.friend_id\n" +
            "INNER JOIN (\n" +
            "    SELECT friend_id\n" +
            "    FROM users_friends\n" +
            "    WHERE user_id = ?\n" +
            ") AS f2 ON u.user_id = f2.friend_id;";
    public static final String CHECK_ID_QUERY = "SELECT user_id FROM users WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> findAllUsers() {
        return findAll(FIND_ALL_USERS_QUERY);
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return findById(FIND_USER_BY_ID_QUERY, userId);
    }

    @Override
    public User addUser(User user) {
        long id = insert(
                INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        log.debug("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        update(
                UPDATE_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        log.debug("Обновлен пользователь {}", newUser);
        return newUser;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        int rowsAffected = processQuery(ADD_FRIEND_QUERY, userId, friendId, friendId, userId, friendId, userId, friendId, friendId, userId);
        log.debug("Пользователь с id {} добавил в друзья пользователя с {}", userId, friendId);
        if (rowsAffected == 0) {
            throw new NotFoundException("ID не найдены, проверьте правильность их указания");
        }
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        checkUserIdExistence(CHECK_ID_QUERY, userId, friendId);
        int rowsAffected = processQuery(DELETE_FRIEND_QUERY, userId, friendId);
        log.debug("Пользователь с id {} удалил из друзей пользователя с {}", userId, friendId);

        if (rowsAffected > 0) {
            processQuery(UPDATE_STATUS_AFTER_DELETE_QUERY, friendId, userId, friendId, userId);
        }
    }

    @Override
    public List<User> getUsersFriends(long userId) {
        checkUserIdExistence(CHECK_ID_QUERY, userId);
        return findAll(GET_USERS_FRIENDS_QUERY, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long anotherUserId) {
        return findAll(GET_COMMON_FRIENDS_QUERY, userId, anotherUserId);
    }

    //Проверка существования запрашиваемых id
    public void checkUserIdExistence(String query, Object... params) {
        for (Object param : params) {
            List<Long> checkResults = jdbc.query(query, (rs, rowNum) -> rs.getLong("user_id"), param);
            if (checkResults.isEmpty()) {
                throw new NotFoundException(String.format("Пользователь с ID %d не найден", (long) param));
            }
        }
    }
}
