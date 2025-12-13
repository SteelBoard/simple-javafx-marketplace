package org.steelboard.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {

    void removeByCartAndProduct_Id(Cart cart, Long productId);

    Optional<CartItem> findByProduct_IdAndCart(Long productId, Cart cart);

    void removeByCart(Cart cart);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id IN :productIds")
    void deleteByCartAndProductIdsIn(@Param("cartId") Long cartId, @Param("productIds") List<Long> productIds);
}
