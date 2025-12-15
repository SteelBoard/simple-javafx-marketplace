package org.steelboard.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.PickupPoint;

@Repository
public interface PickupPointRepository extends JpaRepository<PickupPoint, Integer> {
}
