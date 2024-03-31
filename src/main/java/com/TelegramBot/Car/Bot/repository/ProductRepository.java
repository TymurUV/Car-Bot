package com.TelegramBot.Car.Bot.repository;

import com.TelegramBot.Car.Bot.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
