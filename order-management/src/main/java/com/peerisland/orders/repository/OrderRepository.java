package com.peerisland.orders.repository;

import com.peerisland.orders.entity.Customer;
import com.peerisland.orders.entity.CustomerAddress;
import com.peerisland.orders.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query("""
            select c
            from Customer c
            where c.customerId = :customerId
            """)
    Optional<Customer> findCustomerById(@Param("customerId") Long customerId);

    @Query("""
            select a
            from CustomerAddress a
            join fetch a.customer
            where a.addressId = :addressId
            """)
    Optional<CustomerAddress> findCustomerAddressById(@Param("addressId") Long addressId);

    @Query("""
            select distinct o
            from Orders o
            left join fetch o.customer
            left join fetch o.address
            left join fetch o.orderDetails
            where o.orderId = :orderId
            """)
    Optional<Orders> findOrderByIdWithDetails(@Param("orderId") Long orderId);

    @Query(
            value = """
                    select o
                    from Orders o
                    left join fetch o.customer
                    left join fetch o.address
                    where (:status is null or o.orderStatus = :status)
                    """,
            countQuery = """
                    select count(o)
                    from Orders o
                    where (:status is null or o.orderStatus = :status)
                    """
    )
    Page<Orders> findAllByStatus(@Param("status") Integer status, Pageable pageable);

    @Modifying
    @Query("""
            update Orders o
            set o.orderStatus = :processingStatus,
                o.modBy = :modBy,
                o.modDate = :modDate
            where o.orderStatus = :pendingStatus
            """)
    int updatePendingOrdersToProcessing(@Param("pendingStatus") Integer pendingStatus,
                                        @Param("processingStatus") Integer processingStatus,
                                        @Param("modBy") String modBy,
                                        @Param("modDate") LocalDateTime modDate);
}
