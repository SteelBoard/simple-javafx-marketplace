package org.steelboard.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findProductById(Long id);

    List<Product> findBySeller_Id(Long sellerId);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
