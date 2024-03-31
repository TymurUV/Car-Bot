package com.TelegramBot.Car.Bot.service.impl;

import com.TelegramBot.Car.Bot.domain.Product;
import com.TelegramBot.Car.Bot.repository.ProductRepository;
import com.TelegramBot.Car.Bot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProductById(Long id, Product product) {
        Product productFromDb = productRepository.findById(id).orElse(null);
        if (productFromDb == null) {
            throw new RuntimeException("Product not find by " + id + " for update");
        }
        productFromDb.setName(product.getName());
        productFromDb.setDescription(product.getDescription());
        return productRepository.save(productFromDb);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

}
