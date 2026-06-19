package com.peerisland.orders.repository;

import com.peerisland.orders.entity.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @EntityGraph(attributePaths = {"role"})
    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndCustomerIdNot(String email, Long customerId);

    @Transactional
    @Modifying
    @Query("update Customer c set c.status = ?1, c.modBy = ?2, c.modDate = ?3 where c.customerId = ?4")
    void updateStatusByCustomerId(String status, String modBy, LocalDateTime modDate, Long customerId);
}
