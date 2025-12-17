package org.steelboard.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.PickupPoint;

import java.util.List;
import java.util.Optional;

@Repository
public interface PickupPointRepository extends JpaRepository<PickupPoint, Integer> {

    Optional<PickupPoint> findById(Long id);
    @Query("SELECT p FROM PickupPoint p JOIN FETCH p.address")
    List<PickupPoint> findAllWithAddress(Sort sort);

    @Query("SELECT p FROM PickupPoint p JOIN p.address a WHERE " +
            "LOWER(a.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.street) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "p.phone LIKE %:search%")
    Page<PickupPoint> searchByCityOrStreetOrPhone(@Param("search") String search, Pageable pageable);

    @Query(value = "SELECT p FROM PickupPoint p JOIN FETCH p.address",
            countQuery = "SELECT count(p) FROM PickupPoint p")
    Page<PickupPoint> findAll(Pageable pageable);
}
