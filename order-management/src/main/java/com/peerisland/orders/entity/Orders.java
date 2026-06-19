package com.peerisland.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(nullable = false, unique = true)
    private String orderUniqueId;

    private String appStoreId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetails> orderDetails;

    private Integer orderStatus;

    @Column(nullable = false)
    private Integer orderType;

    private Boolean isCouponCodeUsed;

    private Double orderAmount;

    private Double orderAmountWithoutDiscount;

    private Double loyaltyDiscount;

    private Double loyalPointsEarned;

    private String orderAssignedTo;

    private String specialNotes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private CustomerAddress address;

    private Long branchId;

    private String branchUniqueId;

    private Double loyaltyOnOrder;

    @Builder.Default
    private LocalDateTime addDate = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime modDate = LocalDateTime.now();

    private LocalDateTime orderCompleteDate;

    private String addBy;

    private String modBy;
}
