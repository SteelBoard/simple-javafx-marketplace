package org.steelboard.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findAll(Pageable pageable);
    Page<Product> findAllBySeller(User seller, Pageable pageable);
    Page<Product> findAllProductsByNameIgnoreCase(String name, Pageable pageable);
    Long countProductsByActiveAndSeller(Boolean active, User seller);
    boolean deleteProductById(Long id);
}
