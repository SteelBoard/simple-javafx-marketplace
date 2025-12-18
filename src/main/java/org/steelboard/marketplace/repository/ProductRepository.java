package org.steelboard.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findProductById(Long id);

    List<Product> findBySeller_Id(Long sellerId);

    Optional<Product> findBySku(String sku);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.sales = COALESCE(p.sales, 0) + :quantity WHERE p.id = :productId")
    void incrementSales(@Param("productId") Long productId, @Param("quantity") int quantity);

    Page<Product> findBySeller_Id(Long sellerId, Pageable pageable);

    Page<Product> findBySeller_IdAndNameContainingIgnoreCase(Long sellerId, String name, Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.sales DESC LIMIT 5")
    List<Product> findTop5BestSellers();
}
