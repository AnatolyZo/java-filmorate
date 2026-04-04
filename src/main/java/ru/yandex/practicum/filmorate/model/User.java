package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.annotations.IsContainsSpaceSigns;
import ru.yandex.practicum.filmorate.validation.annotations.IsDateBefore;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    @NotNull
    @Email
    private String email;
    @NotBlank
    @IsContainsSpaceSigns
    private String login;
    private String name;
    @IsDateBefore
    private LocalDate birthday;
    private final Map<Long, FriendshipStatus> friends = new HashMap<>();

    public void setNewFriend(Long friendId, FriendshipStatus friendshipStatus) {
        friends.put(friendId, friendshipStatus);
    }

    public void deleteFriend(Long friendId) {
        friends.remove(friendId);
    }

    public boolean checkFriendExist(Long userId) {
        return friends.containsKey(userId);
    }

    public Set<Long> getFriends() {
        return friends.keySet();
    }
}
