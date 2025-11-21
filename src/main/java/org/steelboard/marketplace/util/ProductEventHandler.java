package org.steelboard.marketplace.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.entity.UserRole;
import org.steelboard.marketplace.repository.ProductRepository;
import org.steelboard.marketplace.repository.UserRepository;

@Component
@Transactional
@AllArgsConstructor
@Slf4j
public class ProductEventHandler {

    private ProductRepository productRepository;
    private UserRepository userRepository;

    @EventListener
    @Transactional
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        Product product = event.product();
        User seller = product.getSeller();

        if (seller != null && seller.getRole() == UserRole.USER) {
            seller.setRole(UserRole.SELLER);
            userRepository.save(seller);
            log.info("Seller {} has been created", seller.getName());
        }
    }

    @EventListener
    @Transactional
    public void handleProductDeletedEvent(ProductDeletedEvent event) {
        Product product = event.product();
        User seller = product.getSeller();

        if (seller != null && seller.getRole() == UserRole.SELLER && productRepository.countProductsByActiveAndSeller(true,  seller) == 0) {
            seller.setRole(UserRole.USER);
            userRepository.save(seller);
            log.info("Seller {} has been deleted", seller.getName());
        }
    }
}
