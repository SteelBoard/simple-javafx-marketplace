package org.steelboard.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.CartItem;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
    CartItem findByProductAndCart(Product product, Cart cart);

    void removeByCartAndProduct_Id(Cart cart, Long productId);
}
