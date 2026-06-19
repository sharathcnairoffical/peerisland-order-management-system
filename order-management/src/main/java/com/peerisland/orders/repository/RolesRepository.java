package com.peerisland.orders.repository;

import com.peerisland.orders.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles,Long> {

    Optional<Roles> findByRoleName(String roleName);
}
