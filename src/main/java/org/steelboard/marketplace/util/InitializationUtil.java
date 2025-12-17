package org.steelboard.marketplace.util;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.repository.AddressRepository;
import org.steelboard.marketplace.repository.PickupPointRepository;
import org.steelboard.marketplace.repository.RoleRepository;
import org.steelboard.marketplace.service.UserService;

@Service
@AllArgsConstructor
public class InitializationUtil implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PickupPointRepository pickupPointRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("Initialization complete: users, products, pickup points created.");
    }
}
