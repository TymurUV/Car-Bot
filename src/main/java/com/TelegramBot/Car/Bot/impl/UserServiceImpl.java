package com.TelegramBot.Car.Bot.impl;

import com.TelegramBot.Car.Bot.domain.User;
import com.TelegramBot.Car.Bot.repository.UserRepository;
import com.TelegramBot.Car.Bot.service.UserService;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUserById(Long id, User user) {
        User userFromDb = userRepository.findById(id).orElse(null);
        if (userFromDb == null) {
            throw new RuntimeException("User not found with id: " + id + " for update");
        }

        userFromDb.setName(user.getName());
        userFromDb.setEmail(user.getEmail());
        userFromDb.setChatId(user.getChatId());

        return userRepository.save(userFromDb);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
