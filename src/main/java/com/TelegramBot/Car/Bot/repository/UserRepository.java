package com.TelegramBot.Car.Bot.repository;

import com.TelegramBot.Car.Bot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByChatId(Long chatId);

}

