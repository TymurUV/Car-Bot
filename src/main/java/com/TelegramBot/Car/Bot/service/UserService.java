package com.TelegramBot.Car.Bot.service;

import com.TelegramBot.Car.Bot.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    User updateUserById(Long id, User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    void deleteUserById(Long id);

    User findUserByChatId(Long chatId);
}
