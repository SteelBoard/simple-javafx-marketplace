package org.steelboard.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.steelboard.marketplace.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
