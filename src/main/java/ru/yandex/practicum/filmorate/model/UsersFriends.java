package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsersFriends {
    private Long userId;
    private Long friendId;
    private boolean isConfirmedFriend;
}
