package org.steelboard.marketplace.util;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.*;
import org.steelboard.marketplace.repository.*;
import org.steelboard.marketplace.service.ProductService;
import org.steelboard.marketplace.service.UserService;

import java.math.BigDecimal;
import java.util.Set;

@Service
@AllArgsConstructor
public class InitializationUtil implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final ProductService productService;
    private final PickupPointRepository pickupPointRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public void run(String... args) {

        // --- роли ---
        Role userRole = roleRepository.save(new Role("ROLE_USER"));
        Role adminRole = roleRepository.save(new Role("ROLE_ADMIN"));
        Role sellerRole = roleRepository.save(new Role("ROLE_SELLER"));

        // --- админ ---
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setRoles(Set.of(userRole, adminRole));
        userService.addAdmin(admin);

        // --- пользователи ---
        for (int i = 0; i < 50; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword("password" + i);
            userService.addUser(user);
        }

        // --- продукты ---
        for (int i = 0; i < 50; i++) {
            Product product = new Product();
            product.setName("Product " + (i + 1));
            User currentUser = userService.findByUsername("user" + i);
            product.setSeller(currentUser);
            product.setPrice(BigDecimal.valueOf(10 + i % 20 + 0.99)); // разные цены
            productRepository.save(product);
        }

        // --- адреса и ПВЗ ---
        Address addr1 = new Address();
        addr1.setCity("Москва");
        addr1.setStreet("Ленина");
        addr1.setHouseNumber("15");
        addr1.setPostalCode("101000");
        addressRepository.save(addr1);

        Address addr2 = new Address();
        addr2.setCity("Санкт-Петербург");
        addr2.setStreet("Невский проспект");
        addr2.setHouseNumber("28");
        addr2.setPostalCode("191025");
        addressRepository.save(addr2);

        Address addr3 = new Address();
        addr3.setCity("Новосибирск");
        addr3.setStreet("Красный проспект");
        addr3.setHouseNumber("100");
        addr3.setPostalCode("630049");
        addressRepository.save(addr3);

        // создаем ПВЗ с телефонами
        PickupPoint pp1 = new PickupPoint();
        pp1.setAddress(addr1);
        pp1.setPhone("+7 495 123-45-67");
        pickupPointRepository.save(pp1);

        PickupPoint pp2 = new PickupPoint();
        pp2.setAddress(addr2);
        pp2.setPhone("+7 812 987-65-43");
        pickupPointRepository.save(pp2);

        PickupPoint pp3 = new PickupPoint();
        pp3.setAddress(addr3);
        pp3.setPhone("+7 383 555-12-34");
        pickupPointRepository.save(pp3);

        System.out.println("Initialization complete: users, products, pickup points created.");
    }
}
