package org.steelboard.marketplace.util;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.Role;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.repository.CartRepository;
import org.steelboard.marketplace.repository.ProductRepository;
import org.steelboard.marketplace.repository.RoleRepository;
import org.steelboard.marketplace.repository.UserRepository;
import org.steelboard.marketplace.service.ProductService;
import org.steelboard.marketplace.service.UserService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class InitializationUtil implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final ProductService productService;

    @Override
    @Transactional
    public void run(String... args) {
        Role userRole = roleRepository.save(new Role("ROLE_USER"));
        Role adminRole = roleRepository.save(new Role("ROLE_ADMIN"));
        Role sellerRole = roleRepository.save(new Role("ROLE_SELLER"));

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setRoles(Set.of(userRole, adminRole));
        userService.addAdmin(admin);

        for (int i = 0; i < 50; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword("password" + i);
            userService.addUser(user);
        }

        for (int i = 0; i < 50; i++) {
            Product product = new Product();
            product.setName("product_" + i);
            User currentUser = userService.findByUsername("user" + i);
            product.setSeller(currentUser);
            product.setPrice(BigDecimal.valueOf(10.99));
            productRepository.save(product);
        }
    }
}
