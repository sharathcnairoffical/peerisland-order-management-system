package com.peerisland.orders.repository;

import com.peerisland.orders.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {

}

