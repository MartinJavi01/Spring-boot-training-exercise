package org.plexus.gatewayservice.repository;

import org.plexus.gatewayservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(@Param(("email")) String email);
    User findByName(@Param("name") String name);
}
