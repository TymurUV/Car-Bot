package com.TelegramBot.Car.Bot.service;

import com.TelegramBot.Car.Bot.domain.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ProductService {
    Product saveProduct(Product product);
    Product updateProductById(Long id, Product product);
    Product getProductById(Long id);
    List<Product> getAllProducts();

    void deleteProductById(Long id);

}
