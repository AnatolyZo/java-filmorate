package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(initializers = {ConfigDataApplicationContextInitializer.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"classpath:schema-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserDbStorageTests {
    private final UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Configuration
    @ComponentScan(basePackages = "ru.yandex.practicum.filmorate.dal.mappers")
    static class TestConfig {
        @Autowired
        private UserRowMapper userRowMapper;

        @Bean
        public UserDbStorage userDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
            return new UserDbStorage(jdbc, mapper);
        }
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 5");
        jdbcTemplate.execute("INSERT INTO users (user_id, email, login, name, birthday) VALUES\n" +
                "(1, 'user1@test.com', 'user1', 'User One', '1990-01-01'),\n" +
                "(2, 'user2@test.com', 'user2', 'User Two', '1995-05-15'),\n" +
                "(3, 'user3@test.com', 'user3', 'User Three', '2000-01-01'),\n" +
                "(4, 'user4@test.com', 'user4', 'User Four', '2001-01-01')");
        jdbcTemplate.execute("INSERT INTO users_friends (user_id, friend_id, friendship_confirm) VALUES\n" +
                "(1, 3, true),\n" +
                "(3, 2, true),\n" +
                "(2, 3, true),\n" +
                "(2, 4, false)");
    }

    @AfterEach
    void set() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM users_friends");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
    }

    @Test
    public void testFindUserById() {
        User expectedUser = User.builder()
                .id(1L)
                .email("user1@test.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();


        Optional<User> userOptional = userStorage.findUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(user).isEqualTo(expectedUser);
                        }
                );
    }

    @Test
    public void testFindUserByNotExistingId() {

        Optional<User> userOptional = userStorage.findUserById(500);

        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    public void testFindAllUsers() {
        Collection<User> allUsers = userStorage.findAllUsers();

        User user1 = User.builder()
                .email("email1@ya.ru")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User user2 = User.builder()
                .email("email2@ya.ru")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1989, 12, 13))
                .build();

        User user3 = User.builder()
                .email("email3@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1989, 12, 14))
                .build();

        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        assertThat(allUsers).hasSize(4);
        assertThat(allUsers)
                .extracting("login")
                .contains("user1", "user2", "user3", "user4");

    }

    @Test
    public void testAddUsers() {
        User user1 = User.builder()
                .email("email1@ya.ru")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User user2 = User.builder()
                .email("email2@ya.ru")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1989, 12, 13))
                .build();

        User user3 = User.builder()
                .email("email3@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1989, 12, 14))
                .build();

        User addedUser1 = userStorage.addUser(user1);
        User addedUser2 = userStorage.addUser(user2);
        User addedUser3 = userStorage.addUser(user3);

        assertThat(addedUser1).isEqualTo(user1);
        assertThat(addedUser2).isEqualTo(user2);
        assertThat(addedUser3).isEqualTo(user3);
    }

    @Test
    public void testUpdateUsers() {
        User updateUser1 = User.builder()
                .id(1L)
                .email("email1@ya.ru")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1989, 12, 12))
                .build();

        User updateUser2 = User.builder()
                .id(1L)
                .email("email2@ya.ru")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1989, 12, 13))
                .build();

        User updateUser3 = User.builder()
                .id(1L)
                .email("email3@ya.ru")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1989, 12, 14))
                .build();

        User updatedUser1 = userStorage.updateUser(updateUser1);
        assertThat(updatedUser1).isEqualTo(updateUser1);
        User updatedUser2 = userStorage.updateUser(updateUser2);
        assertThat(updatedUser2).isEqualTo(updateUser2);
        User updatedUser3 = userStorage.updateUser(updateUser3);
        assertThat(updatedUser3).isEqualTo(updateUser3);
    }

    @Test
    public void testGetUsersFriends() {
        List<User> friends1 = userStorage.getUsersFriends(4);
        assertThat(friends1).isEmpty();

        User expectedUser = User.builder()
                .id(2L)
                .email("user2@test.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1995, 5, 15))
                .build();

        List<User> friends2 = userStorage.getUsersFriends(3);
        assertThat(friends2).contains(expectedUser);
        assertThat(friends2).hasSize(1);
    }

    @Test
    public void testAddFriend() {
        userStorage.addFriend(4, 2);
        List<User> friends = userStorage.getUsersFriends(4);

        User expectedUser = User.builder()
                .id(2L)
                .email("user2@test.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1995, 5, 15))
                .build();

        assertThat(friends).hasSize(1);
        assertThat(friends).contains(expectedUser);
    }

    @Test
    public void testRemoveFriend() {
        List<User> friends = userStorage.getUsersFriends(2);

        User expectedUser = User.builder()
                .id(3L)
                .email("user3@test.com")
                .login("user3")
                .name("User Three")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User expectedUser2 = User.builder()
                .id(4L)
                .email("user4@test.com")
                .login("user4")
                .name("User Four")
                .birthday(LocalDate.of(2001, 1, 1))
                .build();

        assertThat(friends).hasSize(2);
        assertThat(friends).contains(expectedUser, expectedUser2);

        userStorage.removeFriend(2, 4);

        List<User> friendsAfterDelete = userStorage.getUsersFriends(2);

        assertThat(friendsAfterDelete).hasSize(1);
        assertThat(friendsAfterDelete).contains(expectedUser);
    }

    @Test
    public void testGetCommonFriends() {
        List<User> commonFriends = userStorage.getCommonFriends(1, 2);

        User expectedUser = User.builder()
                .id(3L)
                .email("user3@test.com")
                .login("user3")
                .name("User Three")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends).contains(expectedUser);
    }
}