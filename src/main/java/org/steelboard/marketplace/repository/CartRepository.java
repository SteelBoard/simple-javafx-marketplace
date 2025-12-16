package org.steelboard.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {

}
