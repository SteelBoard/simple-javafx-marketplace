package org.steelboard.marketplace.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.PickupPoint;

import java.util.List;
import java.util.Optional;

@Repository
public interface PickupPointRepository extends JpaRepository<PickupPoint, Integer> {

    Optional<PickupPoint> findById(Long id);
    @Query("SELECT p FROM PickupPoint p JOIN FETCH p.address")
    List<PickupPoint> findAllWithAddress(Sort sort);
}
