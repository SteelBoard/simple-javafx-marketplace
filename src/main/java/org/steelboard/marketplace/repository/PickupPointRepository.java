package org.steelboard.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.PickupPoint;

import java.util.Optional;

@Repository
public interface PickupPointRepository extends JpaRepository<PickupPoint, Integer> {

    Optional<PickupPoint> findById(Long id);
}
