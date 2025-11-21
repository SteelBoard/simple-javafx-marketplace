package org.steelboard.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.steelboard.marketplace.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
