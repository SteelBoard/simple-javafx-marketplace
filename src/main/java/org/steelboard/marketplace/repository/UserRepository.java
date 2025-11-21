package org.steelboard.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.steelboard.marketplace.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
