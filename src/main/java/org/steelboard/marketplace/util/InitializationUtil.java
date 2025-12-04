package org.steelboard.marketplace.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.repository.ProductRepository;
import org.steelboard.marketplace.repository.UserRepository;

import java.util.Optional;
import java.util.Random;

@Service
public class InitializationUtil implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public InitializationUtil(UserRepository userRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        for (int i = 0; i < 30; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword(passwordEncoder.encode("password" + i));
            userRepository.save(user);
        }

        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            Product product = new Product();
            product.setName("product_" + i);
            Optional<User> currentUser = userRepository.findByUsername("user" + i);
            if (currentUser.isPresent()) {
                product.setSeller(currentUser.get());
            }
            else {
                continue;
            }
            product.setPrice(10.99);
            productRepository.save(product);
        }
    }
}
