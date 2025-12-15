package org.steelboard.marketplace.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
